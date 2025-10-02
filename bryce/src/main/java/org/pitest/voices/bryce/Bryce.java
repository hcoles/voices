package org.pitest.voices.bryce;

import org.pitest.voices.ClassPathModel;
import org.pitest.voices.Language;
import org.pitest.voices.Model;

public class Bryce {

    public static Model bryceMedium() {
         return new ClassPathModel("/models/vits-piper-en_US-bryce-medium/en_US-bryce-medium.onnx",
                 Language.en_US,
                 1.0f);
    }
}
