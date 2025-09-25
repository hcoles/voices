package org.pitest.voices.us;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArpabetToIPA {
    private static final Map<String,String> MAP = mapping();

    public static List<String> arpabetToIpa(List<String> arpabet) {
        return arpabet.stream()
                .map(ArpabetToIPA::arpabetToIpa)
                .collect(Collectors.toList());
    }

    public static String arpabetToIpa(String arpabet) {
        return MAP.get(arpabet.toUpperCase());
    }

    private static Map<String,String> mapping() {
        var map = new HashMap<String,String>();
        map.put("AA0", "ɒ");
        map.put("AA1", "ˈɒ");
        map.put("AA2", "ˌɒ");
        map.put("AE0", "æ");
        map.put("AE1", "ˈæ");
        map.put("AE2", "ˌæ");
        map.put("AH0", "ʌ");
        map.put("AH1", "ˈʌ");
        map.put("AH2", "ˌʌ");
        map.put("AO0", "ɔ");
        map.put("AO1", "ˈɔ");
        map.put("AO2", "ˌɔ");
        map.put("AW0", "aʊ");
        map.put("AW1", "ˈaʊ");
        map.put("AW2", "ˌaʊ");
        map.put("AY0", "aɪ");
        map.put("AY1", "ˈaɪ");
        map.put("AY2", "ˌaɪ");
        map.put("B", "b");
        map.put("CH", "tʃ");
        map.put("D", "d");
        map.put("DH", "ð");
        map.put("EH0", "ɛ");
        map.put("EH1", "ˈɛ");
        map.put("EH2", "ˌɛ");
        map.put("ER0", "ɜː");
        map.put("ER1", "ˈɜː");
        map.put("ER2", "ˌɜː");
        map.put("EY0", "eɪ");
        map.put("EY1", "ˈeɪ");
        map.put("EY2", "ˌeɪ");
        map.put("F", "f");
        map.put("G", "ɡ");
        map.put("HH", "h");
        map.put("IH0", "ɪ");
        map.put("IH1", "ˈɪ");
        map.put("IH2", "ˌɪ");
        map.put("IY0", "i");
        map.put("IY1", "ˈi");
        map.put("IY2", "ˌi");
        map.put("JH", "dʒ");
        map.put("K", "k");
        map.put("L", "l");
        map.put("M", "m");
        map.put("N", "n");
        map.put("NG", "ŋ");
        map.put("OW0", "oʊ");
        map.put("OW1", "ˈoʊ");
        map.put("OW2", "ˌoʊ");
        map.put("OY0", "ɔɪ");
        map.put("OY1", "ˈɔɪ");
        map.put("OY2", "ˌɔɪ");
        map.put("P", "p");
        map.put("R", "ɹ");
        map.put("S", "s");
        map.put("SH", "ʃ");
        map.put("T", "t");
        map.put("TH", "θ");
        map.put("UH0", "ʊ");
        map.put("UH1", "ˈʊ");
        map.put("UH2", "ˌʊ");
        map.put("UW0", "u");
        map.put("UW1", "ˈu");
        map.put("UW2", "ˌuː");
        map.put("V", "v");
        map.put("W", "w");
        map.put("Y", "j");
        map.put("Z", "z");
        map.put("ZH", "ʒ");
        return map;
    }

}
