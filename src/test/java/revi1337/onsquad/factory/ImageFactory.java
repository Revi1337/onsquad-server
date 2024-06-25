package revi1337.onsquad.factory;

import revi1337.onsquad.image.domain.Image;

public class ImageFactory {

    public static final String UPLOAD_REMOTE_ADDRESS = "[REMOTE ADDRESS]";

    public static Image defaultImage() {
        return new Image(UPLOAD_REMOTE_ADDRESS);
    }
}
