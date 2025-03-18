package revi1337.onsquad.member.application;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RandomCodeGenerator {

    @Value("${spring.mail.code-seed}")
    private String CODE_BOOK;

    public String generate() {
        StringBuffer stringBuffer = new StringBuffer();
        mapCodeBookToIndex(stringBuffer);
        return stringBuffer.toString();
    }

    private void mapCodeBookToIndex(StringBuffer stringBuffer) {
        ThreadLocalRandom.current().ints(0, CODE_BOOK.length())
                .limit(8)
                .forEach(integer -> stringBuffer.append(CODE_BOOK.charAt(integer)));
    }
}
