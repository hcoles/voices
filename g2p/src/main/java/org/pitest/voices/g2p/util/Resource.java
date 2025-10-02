package org.pitest.voices.g2p.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Resource {

    public static List<String> read(String resource) {
        try (InputStream is = readAsStream(resource);
             InputStreamReader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return bufferedReader.lines()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static InputStream readAsStream(String resource) {
        var stream = Resource.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new IllegalStateException("Could not find resource " + resource);
        }
        return stream;
    }
}
