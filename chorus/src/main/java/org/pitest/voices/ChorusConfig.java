package org.pitest.voices;

import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.pitest.g2p.core.EnglishModel;
import org.pitest.g2p.core.Dictionary;
import org.pitest.g2p.core.Expansion;
import org.pitest.g2p.core.expansions.NumberExpander;
import org.pitest.g2p.core.syllables.RulesSyllabiliser;
import org.pitest.g2p.core.tracing.Trace;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class ChorusConfig {

    private final Path base;
    private final Dictionary dictionary;
    private final G2PModelSupplier phonemeModel;
    private final Trace trace;
    private final List<Expansion> expansions;
    private final Consumer<OrtSession.SessionOptions> cudaOptions;

    public static ChorusConfig gpuChorusConfig(Dictionary dictionary){
        return chorusConfig(dictionary)
                .withCudaOptions(ChorusConfig::useGpu);
    }

    public static ChorusConfig chorusConfig(Dictionary dictionary) {
        return new ChorusConfig(dictionary);
    }

    public ChorusConfig(Dictionary dictionary) {
        this(defaultCacheDir(),
                dictionary
                , ((s, d,e, p) -> new EnglishModel(d, new RulesSyllabiliser()))
                , Trace.noTrace(),
                List.of(new NumberExpander()), c -> {});
    }

    public ChorusConfig(Path base,
                        Dictionary dictionary,
                        G2PModelSupplier phonemeModel,
                        Trace trace,
                        List<Expansion> expansions,
                        Consumer<OrtSession.SessionOptions> cudaOptions) {
        this.base = base;
        this.dictionary = dictionary;
        this.phonemeModel = phonemeModel;
        this.trace = trace;
        this.expansions = expansions;
        this.cudaOptions = cudaOptions;
    }

    public Path base() {
        return base;
    }

    public Dictionary dictionary() {
        return dictionary;
    }

    public G2PModelSupplier model() {
        return phonemeModel;
    }

    public Trace trace() {
        return trace;
    }

    public List<Expansion> expansions() {
        return expansions;
    }

    public Consumer<OrtSession.SessionOptions> cudaOptions() {
        return cudaOptions;
    }

    public ChorusConfig withBase(Path base) {
        return new ChorusConfig(base, dictionary, phonemeModel, trace, expansions, cudaOptions);
    }

    public ChorusConfig withModel(G2PModelSupplier phonemeModel) {
        return new ChorusConfig(base, dictionary, phonemeModel, trace, expansions, cudaOptions);
    }

    public ChorusConfig withTrace(Trace trace) {
        return new ChorusConfig(base, dictionary, phonemeModel, trace, expansions, cudaOptions);
    }

    public ChorusConfig withExpansions(List<Expansion> expansions)  {
        return new ChorusConfig(base, dictionary, phonemeModel, trace, expansions, cudaOptions);
    }

    public ChorusConfig withCudaOptions(Consumer<OrtSession.SessionOptions> cudaOptions) {
        return new ChorusConfig(base, dictionary, phonemeModel, trace, expansions, cudaOptions);
    }

    private static Path defaultCacheDir() {
        String home = System.getProperty("user.home");
        return Path.of(home).resolve(".cache").resolve("voices");
    }

    private static void useGpu(OrtSession.SessionOptions options) {
        try {
           options.addCUDA(0);
        } catch (OrtException e) {
            throw new RuntimeException(e);
        }
    }


}
