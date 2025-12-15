package org.pitest.voices.alba;

import org.pitest.voices.ClassPathModel;
import org.pitest.voices.Language;
import org.pitest.voices.Model;

import static org.pitest.voices.piper.PiperHandler.piper;

public class Alba {

    public static Model albaMedium() {
         return new ClassPathModel(piper(),"/models/vits-piper-en_GB-alba-medium/en_GB-alba-medium.onnx",
                 Language.en_GB,
                 2.0f);
    }
}
