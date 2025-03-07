package revi1337.onsquad.auth.application.oauth2.model;

public interface PlatformUserProfile {

    String getName();

    String getNickname();

    String getEmail();

    boolean isEmailVerified();

    String getProfileImage();

    String getThumbnailImage();

}
