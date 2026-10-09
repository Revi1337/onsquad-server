package revi1337.onsquad.common.constant;

import java.util.Arrays;
import java.util.stream.Collectors;

@Deprecated
public enum SupportMediaType {

    JPG_JPEG(0xFF, 0xD8, 0xFF),
    JPEG_JFIF(0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01),
    JPEG_EXIF(0xFF, 0xD8, 0xFF, 0xE1, Wildcard.ANY, Wildcard.ANY, 0x45, 0x78, 0x69, 0x66, 0x00, 0x00),
    PNG(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A),
    SVG(0x3C, 0x3F, 0x78, 0x6D, 0x6C, 0x20, 0x76, 0x65, 0x72, 0x73, 0x69, 0x6F, 0x6E, 0x3D),
    SVG2(0x3C, 0x73, 0x76, 0x67),
    WEBP(0x52, 0x49, 0x46, 0x46, Wildcard.ANY, Wildcard.ANY, Wildcard.ANY, Wildcard.ANY, 0x57, 0x45, 0x42, 0x50);

    private static final SupportMediaType[] VALUES = values();
    public static final int MAX_SIGNATURE_LENGTH = Arrays.stream(VALUES)
            .mapToInt(type -> type.signature.length)
            .max()
            .orElse(0);
    private static final String SUPPORTED_TYPES = Arrays.stream(VALUES)
            .map(type -> type.name().toLowerCase())
            .collect(Collectors.joining(", "));

    private final int[] signature;

    SupportMediaType(int... signature) {
        this.signature = signature;
    }

    public boolean matches(byte[] binary) {
        if (binary.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            int expected = signature[i];
            if (expected != Wildcard.ANY && (binary[i] & 0xFF) != expected) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchesAny(byte[] binary) {
        for (SupportMediaType type : VALUES) {
            if (type.matches(binary)) {
                return true;
            }
        }
        return false;
    }

    public static String convertSupportedTypeString() {
        return SUPPORTED_TYPES;
    }

    private static final class Wildcard {

        private static final int ANY = -1;
    }
}
