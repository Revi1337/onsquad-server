package revi1337.onsquad.auth.application.oauth.model;

import revi1337.onsquad.auth.presentation.oauth.dto.response.GoogleUserInfoResponse;

public record GoogleUserProfile(
        String name,
        String nickname,
        String email,
        boolean isEmailVerified,
        String profileImage,
        String thumbnailImage
) implements PlatformUserProfile {

    public static GoogleUserProfile from(GoogleUserInfoResponse response) {
        return new GoogleUserProfile(
                response.name(),
                response.name(),
                response.email(),
                response.verifiedEmail(),
                response.picture(),
                response.picture()
        );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getProfileImage() {
        return profileImage;
    }

    @Override
    public String getThumbnailImage() {
        return thumbnailImage;
    }
}

//package revi1337.onsquad.auth.application.oauth2.model;
//
//public record GoogleUserProfile(
//        String name,
//        String nickname,
//        String email,
//        boolean isEmailVerified,
//        String profileImage,
//        String thumbnailImage
//) {
//}
