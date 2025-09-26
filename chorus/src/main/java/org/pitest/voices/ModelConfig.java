package org.pitest.voices;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
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
        Gson gson = new Gson();

        PiperJson result = gson.fromJson(new JsonReader(new InputStreamReader(json)), PiperJson.class);
        long sampleRate = result.audio.sample_rate;
        Map<String,Long[]> phonemeIdMap = result.phoneme_id_map;
        Map<String,Long> cleanCopy = new HashMap<>();
        for (Map.Entry<String, Long[]> entry : phonemeIdMap.entrySet()) {
            if (entry.getValue().length != 1) {
                throw new IllegalArgumentException("Expected single value in phoneme id map");
            }
            cleanCopy.put(entry.getKey(), entry.getValue()[0]);
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

class PiperJson {
    public Map<String,Long[]> phoneme_id_map;
    public AudioJson audio;
}

class AudioJson {
    public long sample_rate;
}