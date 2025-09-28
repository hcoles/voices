package org.pitest.voices.openvoice;

import org.pitest.voices.ModelFetcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ONNXModel {

    private final ModelFetcher remote;
    private final String local;

    public ONNXModel(ModelFetcher remote, String local) {
        this.remote = remote;
        this.local = local;
    }

    public Path resolve(Path cacheBase) throws IOException {
        Path location = cacheBase.resolve(this.local);
        Path onnx = location.resolve("model.onnx");
        if (!Files.exists(onnx)) {
            Path tempLocation = remote.fetch();
            Files.move(tempLocation.getParent(), location);
        }
        return onnx;
    }
}
