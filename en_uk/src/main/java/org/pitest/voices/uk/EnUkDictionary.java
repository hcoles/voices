package org.pitest.voices.uk;

import org.pitest.voices.g2p.core.Dictionary;

import static org.pitest.voices.Resource.read;

public class EnUkDictionary {
    public static Dictionary en_uk() {
        return Dictionary.fromList(read("/dictionary/voices_en_uk.dict"));
    }
}
