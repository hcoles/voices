package org.pitest.voices;

import org.junit.jupiter.api.Test;
import org.pitest.g2p.util.Resource;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ModelConfigTest {

    @Test
    void parsesSampleRate() {
        InputStream in = Resource.readAsStream("/en_en_GB_alba_medium_en_GB-alba-medium.onnx.json");
        assertThat(ModelConfig.fromJson(in).sampleRate()).isEqualTo(22050L);
    }

    @Test
    void parsesIdMap() {
        InputStream in = Resource.readAsStream("/en_en_GB_alba_medium_en_GB-alba-medium.onnx.json");
        var actual = ModelConfig.fromJson(in).phonemeIdMap();
        assertThat(actual).containsEntry("$", 2L);
        assertThat(actual).containsEntry("\"", 150L);
    }
}