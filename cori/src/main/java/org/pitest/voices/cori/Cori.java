package org.pitest.voices.cori;

import org.pitest.voices.ClassPathModel;
import org.pitest.voices.Language;
import org.pitest.voices.Model;

import static org.pitest.voices.piper.PiperHandler.piper;

public class Cori {

    public static Model coriHigh() {
         return new ClassPathModel(piper(), "/models/vits-piper-en_GB-cori-high/en_GB-cori-high.onnx",
                 Language.en_GB,
                 1.0f);
    }
}
