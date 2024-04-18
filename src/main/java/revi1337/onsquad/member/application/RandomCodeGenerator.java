package revi1337.onsquad.member.application;

import java.util.Random;

public class RandomCodeGenerator {

    private static final String CODE_BOOK = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String generate() {
        StringBuilder stringBuilder = new StringBuilder();
        mapCodeBookToIndex(stringBuilder);
        return stringBuilder.toString();
    }

    private void mapCodeBookToIndex(StringBuilder stringBuilder) {
        Random random = new Random();
        random.ints(0, CODE_BOOK.length())
                .limit(8)
                .forEach(integer -> stringBuilder.append(CODE_BOOK.charAt(integer)));
    }
}
