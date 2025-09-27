package org.pitest.g2p.core;


import org.pitest.g2p.core.pos.Pos;
import org.pitest.g2p.core.tracing.Trace;

public class NoModel implements G2PModel {
    @Override
    public String predict(Trace trace, String word, Pos pos) {
        return null;
    }
    
}
