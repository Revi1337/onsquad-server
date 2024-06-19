package revi1337.onsquad.factory;

import revi1337.onsquad.image.domain.Image;

public class ImageFactory {

    public static final byte[] IMAGE_DATA = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    public static Image defaultImage() {
        return new Image();
    }
}
