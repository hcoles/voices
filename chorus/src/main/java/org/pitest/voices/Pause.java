package org.pitest.voices;

public record Pause(String symbol, int beats) {

    public boolean matches(String symbol) {
        return this.symbol.equals(symbol);
    }

}
