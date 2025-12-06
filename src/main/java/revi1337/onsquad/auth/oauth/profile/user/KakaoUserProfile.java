package revi1337.onsquad.auth.oauth.profile.user;

import revi1337.onsquad.auth.oauth.profile.PlatformUserProfile;
import revi1337.onsquad.auth.oauth.provider.user.response.KakaoUserInfoResponse;

public record KakaoUserProfile(
        String name,
        String nickname,
        String email,
        boolean isEmailVerified,
        String profileImage,
        String thumbnailImage
) implements PlatformUserProfile {

    public static KakaoUserProfile from(KakaoUserInfoResponse response) {
        return new KakaoUserProfile(
                response.kakaoAccount().profile().nickname(),
                response.kakaoAccount().profile().nickname(),
                response.kakaoAccount().email(),
                response.kakaoAccount().isEmailVerified(),
                response.kakaoAccount().profile().profileImageUrl(),
                response.kakaoAccount().profile().thumbnailImageUrl()
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
