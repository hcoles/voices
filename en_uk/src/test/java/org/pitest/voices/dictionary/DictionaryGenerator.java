package org.pitest.voices.dictionary;

import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.Resource;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.pitest.voices.dictionary.ESpeakWrapper.extract;

// espeak-ng -v en-sc --ipa=3 -q -f words_alpha.txt

public class DictionaryGenerator {

    public static void main(String[] args) throws Exception {
        Path baseDir = new ChorusConfig(Dictionary.empty()).base();
        Fetch fetch = new Fetch(baseDir);

       Path cmu = fetch.fetch(new URL("https://raw.githubusercontent.com/Alexir/CMUdict/refs/heads/master/cmudict-0.7b"), "cmudict.txt");
       System.out.println("Using " + cmu.toAbsolutePath() + " " + Files.getLastModifiedTime(cmu));

       // not all lines are UTF-8
       List<String> cmuList = Files.readAllLines(cmu, StandardCharsets.ISO_8859_1).stream()
               .map(String::trim)
               .filter(s -> ! s.matches("^[^a-zA-Z].*$")) // skip comments and weird stuff
               .filter(s -> !s.contains("�"))   // d�j� causes issues
               .filter(s -> !s.contains("28")) // wtf??
               .map(s -> s.split(" ")[0])
               .filter(s -> !s.contains(("(")))
               .map(String::toLowerCase)
               .collect(Collectors.toList());

        var wordListPath = baseDir.resolve("wordlist.txt");
        Files.write(wordListPath, cmuList);

        // a tale of two cities
        appendFromGutenberg(wordListPath,98);

        // moby dick
        appendFromGutenberg(wordListPath,2701);

        // alice in wonderland
        appendFromGutenberg(wordListPath,28885);

        // The Ragged Trousered Philanthropists
        appendFromGutenberg(wordListPath,3608);

        // random book of english surnames
        appendFromGutenberg(wordListPath,59959);


        // some names
        var names = Resource.read("/names.txt");
        Files.write(wordListPath, names, StandardOpenOption.APPEND);

        List<String> words = Files.readAllLines(baseDir.resolve("wordlist.txt")).stream()
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());


        // generate phonemes. Will skip processing unless old files deleted
        Path dict = baseDir.resolve("dictionary_gb.txt");
        generate(ESpeakWrapper.EspeakOptions.UK, words, dict);

        addHomographs(dict, baseDir.resolve("voices_en_uk.dict"));


    }

    private static void addHomographs(Path base, Path out) throws IOException {
        Files.copy(base, out, StandardCopyOption.REPLACE_EXISTING);

        List<String> lines = new ArrayList<>();

        lines.add("abuse=ɐbjˈuːz|VB"); // hand generated
        lines.add("read=ɹˈɛd|VBD"); //vbd despite what our test says
        lines.add("live=lˈɪv|VB");
        lines.add("lives=lˈɪvz|VB");
        lines.add("desert=dˈəʊnt|VB");
        lines.add("lead=lˈɛd|NN"); // hand crafted, for once the verb is the default form
        lines.add("wind=wˈaɪnd|VBP|VB");
        lines.add("winds=wˈaɪndz|VBP|VB");
        lines.add("bow=bˌaʊ|VB");
        lines.add("bows=bˌaʊz|VB");
        lines.add("use=j'uːz|VB");
        lines.add("sow=s'oʊ|VB");
        lines.add("sow=s'aʊ|NN");
        lines.add("sows=s'aʊz|NN");
        lines.add("refuse=ɹˈɛfjuːs|NN");
        lines.add("rebel=ɹɪbˈɛl|VB|VBP");
        lines.add("rebels=ɹɪbˈɛlz|VB|VBP");
        lines.add("moped=m'əʊpt|VBD");
        lines.add("invalid=ɪnvəlɪd|NN");

        Files.write(out, lines, StandardOpenOption.APPEND);

        System.out.println("Dictionary is at " + out.toAbsolutePath());

    }

    private static void appendFromGutenberg(Path wordList, int id) throws Exception {
        Path baseDir = new ChorusConfig(Dictionary.empty()).base();
        Fetch fetch = new Fetch(baseDir);
        Path text = fetch.fetch(new URL(String.format("https://www.gutenberg.org/cache/epub/%d/pg%d.txt", id, id)), id + ".txt");
        Files.write(wordList, clean(text), StandardOpenOption.APPEND);
    }

    private static Dictionary generate(ESpeakWrapper.EspeakOptions options, List<String> words, Path out) throws IOException {
        if (Files.exists(out)) {
            System.err.println("Skipping " + out.toAbsolutePath() + " " + Files.getLastModifiedTime(out));
            return Dictionary.fromFile(out);
        }

        long start = System.currentTimeMillis();
        Map<String, String> dict = extract(words, options);
        System.out.println("Generated dictionary in " + (System.currentTimeMillis() - start) + "ms");

        Files.write(out, dict.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList()));

        return Dictionary.fromMap(dict);
    }

    private static List<String> clean(Path path) throws IOException {
        return Files.readAllLines(path).stream()
                .flatMap(DictionaryGenerator::toWords)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Stream<String> toWords(String s) {
        String[] words = s.split("[^a-zA-Z0-9'\\-]+");

        return Arrays.stream(words)
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(w -> w.length() > 3 )
                .filter(w -> !w.matches(".*[0-9]+.*"))
                .filter(w -> !w.contains("--"))
                .distinct()
                .peek(System.out::println);
    }


}


