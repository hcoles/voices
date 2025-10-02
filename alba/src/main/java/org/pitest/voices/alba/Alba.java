package org.pitest.voices.alba;

import org.pitest.voices.ClassPathModel;
import org.pitest.voices.Language;
import org.pitest.voices.Model;

public class Alba {

    public static Model albaMedium() {
         return new ClassPathModel("/models/vits-piper-en_GB-alba-medium/en_GB-alba-medium.onnx",
                 Language.en_GB,
                 2.0f);
    }
}
