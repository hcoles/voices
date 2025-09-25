package org.pitest.voices.us;

import org.pitest.g2p.core.Dictionary;

import static org.pitest.g2p.util.Resource.read;

public class EnUsDictionary {
    public static Dictionary en_us() {
        return Dictionary.fromList(read("/dictionary/voices_en_us.dict"));
    }
}
