package org.pitest.voices;


import com.cedarsoftware.io.JsonIo;
import com.cedarsoftware.io.ReadOptions;
import com.cedarsoftware.io.ReadOptionsBuilder;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ModelConfig {

    private final long sampleRate;
    private final Map<String,Long> phonemeIdMap;

    ModelConfig(long sampleRate, Map<String, Long> phonemeIdMap) {
        this.sampleRate = sampleRate;
        this.phonemeIdMap = phonemeIdMap;
    }

    public static ModelConfig fromJson(InputStream json) {
        ReadOptions readOptions = new ReadOptionsBuilder().returnAsNativeJsonObjects().build();
        Map<String,Object> root = JsonIo.toObjects(json, readOptions, Map.class);

        Map<String,Object> audio = (Map<String,Object>)root.get("audio");
        long sampleRate = (Long)audio.get("sample_rate");

        Map<String,Object[]> phonemeIdMap = (Map<String, Object[]>) root.get("phoneme_id_map");
        Map<String,Long> cleanCopy=  new HashMap<>();
        for (Map.Entry<String, Object[]> entry : phonemeIdMap.entrySet()) {
            if (entry.getValue().length != 1) {
                throw new IllegalArgumentException("Expected single value in phoneme id map");
            }
            cleanCopy.put(entry.getKey(), (Long) entry.getValue()[0]);
        }

        return new ModelConfig(sampleRate, cleanCopy);
    }


    public long sampleRate() {
        return sampleRate;
    }

    public Map<String, Long> phonemeIdMap() {
        return phonemeIdMap;
    }
}
