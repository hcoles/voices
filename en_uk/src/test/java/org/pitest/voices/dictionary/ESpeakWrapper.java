package org.pitest.voices.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ESpeakWrapper {

    public static Map<String, String> extract(List<String> words) {
        return extract(words, EspeakOptions.ALBA);
    }


    public static Map<String, String> extract(List<String> words, EspeakOptions options) {
          // Preserve order of input
        Map<String, String> result = new LinkedHashMap<>();

        try {
            // Create a temporary file
            Path tempFile = Files.createTempFile("espeak_input", ".txt");
            try {
                // Write all words to the temp file, one per line
                Files.write(tempFile, words.stream().map(w -> w+"\n").collect(Collectors.toList()));

                // Run espeak with the file input
                ProcessBuilder pb = new ProcessBuilder();
                pb.command().add(options.getBinaryPath());
                pb.command().addAll(options.getArguments());
                pb.command().add("-f");
                pb.command().add(tempFile.toString());

                pb.redirectErrorStream(true);

                Process proc = pb.start();

                // Read output lines - one IPA transcription per input word
                try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                    int i = 0;
                    String line;
                    while ((line = br.readLine()) != null && i < words.size()) {
                        String word = words.get(i);
                        result.put(word, line.trim());
                        i++;
                    }
                }

                int exitCode = proc.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException(
                            String.format("espeak-ng returned non‑zero exit code %d", exitCode));
                }

            } finally {
                // Clean up - delete the temporary file
                Files.deleteIfExists(tempFile);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to start espeak-ng process (is it installed and on $PATH?)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserve interrupt status
            throw new RuntimeException("Interrupted while waiting for espeak-ng to finish", e);
        }

        return result;
    }


    public static String runEspeak(String word, EspeakOptions options) {

        ProcessBuilder pb = new ProcessBuilder();
        pb.command().add(options.getBinaryPath());
        pb.command().addAll(options.getArguments());
        pb.command().add(word);

        // Do not inherit the parent’s stdio – we need to capture stdout.
        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();

            // Read the whole output (should be a single line with the IPA string).
            StringBuilder out = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    out.append(line);
                    // espeak‑ng prints a newline after the IPA; we keep only the first line.
                    break;
                }
            }

            int exitCode = proc.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException(
                        String.format("espeak-ng returned non‑zero exit code %d for word \"%s\"; output: %s",
                                exitCode, word, out));
            }


            return out.toString().trim();

        } catch (IOException e) {
            throw new RuntimeException("Failed to start espeak-ng process (is it installed and on $PATH?)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserve interrupt status
            throw new RuntimeException("Interrupted while waiting for espeak-ng to finish", e);
        }
    }


    public static final class EspeakOptions {

        public static final EspeakOptions UK = new EspeakOptions(
                "espeak-ng",
                List.of("-v", "en-uk", "--ipa", "-q")
        );


        public static final EspeakOptions ALBA = new EspeakOptions(
                "espeak-ng",
                List.of("-v", "en-sc", "--ipa", "-q")
        );

        public static final EspeakOptions US = new EspeakOptions(
                "espeak-ng",
                List.of("-v", "en-us", "--ipa", "-q")
        );

        private final String binaryPath;
        private final List<String> arguments;

        public EspeakOptions(String binaryPath, List<String> arguments) {
            this.binaryPath = binaryPath;
            this.arguments = List.copyOf(arguments);
        }

        public String getBinaryPath() {
            return binaryPath;
        }

        public List<String> getArguments() {
            return arguments;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String binaryPath = "espeak-ng";
            private final List<String> args = new java.util.ArrayList<>();

            public Builder binaryPath(String path) {
                this.binaryPath = path;
                return this;
            }

            public Builder addArgument(String arg) {
                this.args.add(arg);
                return this;
            }

            public Builder addArguments(String... args) {
                Collections.addAll(this.args, args);
                return this;
            }

            public EspeakOptions build() {
                return new EspeakOptions(binaryPath, args);
            }
        }
    }


    public static void main(String[] args) {
        List<String> words = List.of("hello", "world", "java", "phonetics", "espeak-ng");

        System.out.println("=== Using default options (-v en-sc --ipa -q) ===");
        Map<String, String> ipaMap = extract(words);
        ipaMap.forEach((w, ipa) -> System.out.printf("%-12s → %s%n", w, ipa));

        System.out.println("\n=== Using a custom voice (US English) ===");
        EspeakOptions usOptions = EspeakOptions.builder()
                .addArgument("-v")
                .addArgument("en-us")
                .addArgument("--ipa")
                .addArgument("-q")
                .build();

        Map<String, String> usMap = extract(words, usOptions);
        usMap.forEach((w, ipa) -> System.out.printf("%-12s → %s%n", w, ipa));
    }
}
