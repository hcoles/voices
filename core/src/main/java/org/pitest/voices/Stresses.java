package org.pitest.voices;

public enum Stresses implements Stress {

    KEEP_STRESS() {
        @Override
        public String apply(String s) {
            return s;
        }
    },

    NO_STRESS() {
        @Override
        public String apply(String s) {
            return s.replace("ˈ", "");
        }
    };
}
