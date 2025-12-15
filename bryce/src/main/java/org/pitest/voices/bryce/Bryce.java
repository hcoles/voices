package org.pitest.voices.bryce;

import org.pitest.voices.ClassPathModel;
import org.pitest.voices.Language;
import org.pitest.voices.Model;

import static org.pitest.voices.piper.PiperHandler.piper;

public class Bryce {

    public static Model bryceMedium() {
         return new ClassPathModel(piper(), "/models/vits-piper-en_US-bryce-medium/en_US-bryce-medium.onnx",
                 Language.en_US,
                 1.0f);
    }
}
