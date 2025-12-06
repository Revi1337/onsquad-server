package revi1337.onsquad.auth.oauth.profile;

public interface PlatformUserProfile {

    String getName();

    String getNickname();

    String getEmail();

    boolean isEmailVerified();

    String getProfileImage();

    String getThumbnailImage();

}
