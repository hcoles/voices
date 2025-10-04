package org.pitest.voices.us;

import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.dictionary.Dictionaries;
import org.pitest.voices.g2p.core.dictionary.MapDictionary;

import static org.pitest.voices.g2p.util.Resource.read;

public class EnUsDictionary {
    public static Dictionary en_us() {
        return MapDictionary.fromList(read("/dictionary/voices_en_us.dict"))
                .withAdditions(Dictionaries.englishHomographs());
    }
}
