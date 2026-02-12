package revi1337.onsquad.auth.oauth.domain;

public interface PlatformUserProfile {

    String getName();

    String getNickname();

    String getEmail();

    boolean isEmailVerified();

    String getProfileImage();

    String getThumbnailImage();

}
