package org.pitest.voices.uk;

import org.pitest.voices.g2p.core.Dictionary;
import org.pitest.voices.g2p.core.dictionary.Dictionaries;
import org.pitest.voices.g2p.core.dictionary.MapDictionary;

import static org.pitest.voices.Resource.read;

public class EnUkDictionary {
    public static Dictionary en_uk() {
        return MapDictionary.fromList(read("/dictionary/voices_en_uk.dict"))
                .withAdditions(Dictionaries.englishHomographs());
    }
}
