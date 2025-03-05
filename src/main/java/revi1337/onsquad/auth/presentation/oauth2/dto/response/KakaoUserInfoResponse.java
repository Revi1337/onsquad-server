package revi1337.onsquad.auth.presentation.oauth2.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        Long id,
        String connectedAt,
        Properties properties,
        KakaoAccount kakaoAccount
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Properties(
            String nickname,
            String profileImage,
            String thumbnailImage
    ) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
            boolean profileNicknameNeedsAgreement,
            boolean profileImageNeedsAgreement,
            Profile profile,
            boolean hasEmail,
            boolean emailNeedsAgreement,
            boolean isEmailValid,
            boolean isEmailVerified,
            String email
    ) {

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Profile(
                String nickname,
                String thumbnailImageUrl,
                String profileImageUrl,
                boolean isDefaultImage,
                boolean isDefaultNickname
        ) {
        }
    }
}
