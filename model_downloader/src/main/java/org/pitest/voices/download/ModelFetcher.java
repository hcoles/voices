package org.pitest.voices.download;

import java.io.IOException;
import java.nio.file.Path;

public interface ModelFetcher {
    Path fetch() throws IOException;
}
