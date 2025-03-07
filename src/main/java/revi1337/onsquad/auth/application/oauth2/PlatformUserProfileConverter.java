package revi1337.onsquad.auth.application.oauth2;

import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.auth.presentation.oauth2.dto.response.GoogleUserInfoResponse;
import revi1337.onsquad.auth.presentation.oauth2.dto.response.KakaoUserInfoResponse;

@Component
public class PlatformUserProfileConverter {

    public PlatformUserProfile convertKakaoProfile(KakaoUserInfoResponse kakaoUserInfoResponse) {
        String name = kakaoUserInfoResponse.kakaoAccount().profile().nickname();
        String nickname = kakaoUserInfoResponse.kakaoAccount().profile().nickname();
        String email = kakaoUserInfoResponse.kakaoAccount().email();
        boolean isEmailVerified = kakaoUserInfoResponse.kakaoAccount().isEmailVerified();
        String profileImage = kakaoUserInfoResponse.kakaoAccount().profile().profileImageUrl();
        String thumbnailImage = kakaoUserInfoResponse.kakaoAccount().profile().thumbnailImageUrl();

        return new KakaoUserProfile(name, nickname, email, isEmailVerified, profileImage, thumbnailImage);
    }

    public PlatformUserProfile convertGoogleProfile(GoogleUserInfoResponse googleUserInfoResponse) {
        String name = googleUserInfoResponse.name();
        String nickname = googleUserInfoResponse.name();
        String email = googleUserInfoResponse.email();
        boolean isEmailVerified = googleUserInfoResponse.verifiedEmail();
        String profileImage = googleUserInfoResponse.picture();
        String thumbnailImage = googleUserInfoResponse.picture();

        return new GoogleUserProfile(name, nickname, email, isEmailVerified, profileImage, thumbnailImage);
    }
}

//package revi1337.onsquad.auth.application.oauth2;
//
//import org.springframework.stereotype.Component;
//import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
//import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.GoogleUserInfoResponse;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.KakaoUserInfoResponse;
//
//@Component
//public class PlatformUserProfileConverter {
//
//    public KakaoUserProfile convertKakaoProfile(KakaoUserInfoResponse kakaoUserInfoResponse) {
//        String name = kakaoUserInfoResponse.kakaoAccount().profile().nickname();
//        String nickname = kakaoUserInfoResponse.kakaoAccount().profile().nickname();
//        String email = kakaoUserInfoResponse.kakaoAccount().email();
//        boolean isEmailVerified = kakaoUserInfoResponse.kakaoAccount().isEmailVerified();
//        String profileImage = kakaoUserInfoResponse.kakaoAccount().profile().profileImageUrl();
//        String thumbnailImage = kakaoUserInfoResponse.kakaoAccount().profile().thumbnailImageUrl();
//
//        return new KakaoUserProfile(name, nickname, email, isEmailVerified, profileImage, thumbnailImage);
//    }
//
//    public GoogleUserProfile convertGoogleProfile(GoogleUserInfoResponse googleUserInfoResponse) {
//        String name = googleUserInfoResponse.name();
//        String nickname = googleUserInfoResponse.name();
//        String email = googleUserInfoResponse.email();
//        boolean isEmailVerified = googleUserInfoResponse.verifiedEmail();
//        String profileImage = googleUserInfoResponse.picture();
//        String thumbnailImage = googleUserInfoResponse.picture();
//
//        return new GoogleUserProfile(name, nickname, email, isEmailVerified, profileImage, thumbnailImage);
//    }
//}