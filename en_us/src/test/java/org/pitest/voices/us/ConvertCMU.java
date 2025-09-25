package org.pitest.voices.us;

import org.pitest.g2p.core.Dictionary;
import org.pitest.voices.ChorusConfig;
import org.pitest.voices.util.Fetch;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


    public class ConvertCMU {

        public static void main(String[] args) throws Exception {
            Path baseDir = new ChorusConfig(Dictionary.empty()).base();
            Fetch fetch = new Fetch(baseDir);

            Path cmu = fetch.fetch(new URL("https://raw.githubusercontent.com/Alexir/CMUdict/refs/heads/master/cmudict-0.7b"), "cmudict.txt");
            System.out.println("Using " + cmu.toAbsolutePath() + " " + Files.getLastModifiedTime(cmu));

            // not all lines are UTF-8
            List<String> lines = Files.readAllLines(cmu, StandardCharsets.ISO_8859_1).stream()
                    .map(String::trim)
                    .filter(s -> ! s.matches("^[^a-zA-Z].*$")) // skip comments and weird stuff
                    .filter(s -> !s.contains("�"))   // d�j� causes issues
                    .filter(s -> !s.contains("28")) // wtf??
                    .collect(Collectors.toList());


            Map<String, String> dict = new HashMap<>();
            for (String line : lines) {
                String[] parts = line.split(" ");

                String word = parts[0].toLowerCase();
                if (word.contains("(")) {
                    continue;
                }
                List<String> phonemes = Arrays.stream(parts)
                        .filter(s -> !s.isEmpty())
                        .skip(1)
                        .map(ArpabetToIPA::arpabetToIpa)
                        .collect(Collectors.toList());

                dict.put(word, String.join("", phonemes));
            }

            Path out = baseDir.resolve("voices_en_us.pre");
            Files.write(out, dict.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.toList()));


            addHomographs(baseDir.resolve("voices_en_us.pre"), baseDir.resolve("voices_en_us.dict"));

        }

        private static void addHomographs(Path base, Path out) throws IOException {
            Files.copy(base, out, StandardCopyOption.REPLACE_EXISTING);

            List<String> lines = new ArrayList<>();

            lines.add("abuse=ɐbjˈuːz|VB"); // hand generated
            lines.add("read=ɹˈɛd|VBD"); //vbd despite what our test says
            lines.add("live=lˈɪv|VB");
            lines.add("desert=dˈəʊnt|VB");
            lines.add("lead=lˈɛd|NN"); // hand crafted, for once the verb is the default form
            lines.add("wind=wˈaɪnd|VBP|VB");
            lines.add("winds=wˈaɪndz|VBP|VB");
            lines.add("bow=bˌaʊ|VB");
            lines.add("use=j'uːz|VB");
            lines.add("sow=s'oʊ|VB");
            lines.add("sow=s'aʊ|NN");
            lines.add("refuse=ɹˈɛfjuːs|NN");
            lines.add("rebel=ɹɪbˈɛl|VB");
            lines.add("moped=m'əʊpt|VBD");
            lines.add("invalid=ɪnvəlɪd|NN");

            Files.write(out, lines, StandardOpenOption.APPEND);

            System.out.println("Dictionary is at " + out.toAbsolutePath());

        }

    }


