package org.pitest.g2p.core;


import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.tracing.Trace;

public class NoModel implements G2PModel {

    @Override
    public String predict(Trace trace, Language lang, String word, Pos pos) {
        return null;
    }

    @Override
    public void close() throws Exception {
        // no op
    }

}
