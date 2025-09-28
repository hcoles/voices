package org.pitest.voices;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.G2PModel;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Creates G2P models
 */
public interface G2PModelSupplier {
    /**
     *
     * @param options Supplier of OrtSession options that can be optionally called
     *                by implementations
     * @param dictionary Dictionary to use
     * @param env     The OrtEnvironment
     * @param base    Path of the cache directory
     * @return A G2PModel
     */
    G2PModel create(Supplier<OrtSession.SessionOptions> options, Dictionary dictionary, OrtEnvironment env, Path base);
}
