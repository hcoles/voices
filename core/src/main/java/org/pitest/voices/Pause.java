package org.pitest.voices;

import java.util.List;

public record Pause(String symbol, int beats) {

    public boolean matches(String symbol) {
        return this.symbol.equals(symbol);
    }

    public static List<Pause> defaultPauses() {
        return List.of(new Pause("—", 3),
                new Pause("–", 2),
                new Pause(":", 2)
        );
    }

}
