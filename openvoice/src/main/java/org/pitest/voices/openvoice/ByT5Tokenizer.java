package org.pitest.voices.openvoice;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class ByT5Tokenizer {
    // Special tokens (match Hugging Face ByT5)
    public static final int PAD_TOKEN_ID = 0;
    public static final int EOS_TOKEN_ID = 1;
    public static final int UNK_TOKEN_ID = 2;

    // ByT5 encodes bytes with +3 offset to reserve space for PAD/EOS/UNK
    private static final int OFFSET = 3;

    /**
     * Encode text to token IDs.
     * Example: "<en>: hello" -> [104, 101, 108, 108, 111] (+3 offset)
     */
    int[] encode(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        int[] tokenIds = new int[length];

        for (int i = 0; i < bytes.length; i++) {
            tokenIds[i] = (bytes[i] & 0xFF) + OFFSET;
        }

        return tokenIds;
    }

    String decode(int[] tokenIds) {
        List<Byte> byteList = new ArrayList<>();
        for (int id : tokenIds) {
            if (id == PAD_TOKEN_ID || id == EOS_TOKEN_ID || id == UNK_TOKEN_ID) {
                continue;
            }
            byteList.add((byte) (id - OFFSET));
        }

        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    int padTokenId() {
        return PAD_TOKEN_ID;
    }

    int eosTokenId() {
        return EOS_TOKEN_ID;
    }
}
