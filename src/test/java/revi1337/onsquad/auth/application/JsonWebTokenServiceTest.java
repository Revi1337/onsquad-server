package revi1337.onsquad.auth.application;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.auth.domain.vo.AccessToken;
import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;
import revi1337.onsquad.auth.domain.vo.RefreshToken;
import revi1337.onsquad.auth.error.exception.AuthTokenException;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.dto.MemberDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("JsonWebTokenService 테스트")
@ExtendWith(MockitoExtension.class)
class JsonWebTokenServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private RefreshTokenManager refreshTokenManager;
    @Mock private JsonWebTokenProvider jsonWebTokenProvider;
    @Mock private JsonWebTokenEvaluator jsonWebTokenEvaluator;
    @InjectMocks private JsonWebTokenService jsonWebTokenService;

    @DisplayName("AccessToken 과 RefreshToken 을 포함한 TokenPair 가 만들어지는지 확인한다.")
    @Test
    public void generateTokenPair() {
        // given
        MemberDto memberDto = MemberDto.from(MemberFactory.defaultMember().build());
        AccessToken accessToken = AccessToken.of("ACCESS-TOKEN");
        RefreshToken refreshToken = RefreshToken.of("REFRESH-TOKEN");
        given(jsonWebTokenProvider.generateAccessToken(anyString(), anyMap())).willReturn(accessToken);
        given(jsonWebTokenProvider.generateRefreshToken(anyString(), anyMap())).willReturn(refreshToken);

        // when
        JsonWebTokenResponse jsonWebTokenResponse = jsonWebTokenService.generateTokenPair(memberDto);

        // then
        assertThat(jsonWebTokenResponse.refreshToken()).isEqualTo(refreshToken);
        assertThat(jsonWebTokenResponse.accessToken()).isEqualTo(accessToken);
    }

    @DisplayName("RefreshToken 의 저장은 refreshTokenStoreService 에게 위임한다.")
    @Test
    public void storeTemporaryTokenInMemory() {
        // given
        RefreshToken refreshToken = RefreshToken.of("REFRESH-TOKEN");
        MemberDto memberDto = MemberDto.builder().id(1L).build();

        // when
        jsonWebTokenService.storeTemporaryTokenInMemory(refreshToken, memberDto.getId());

        // then
        then(refreshTokenManager).should(times(1))
                .storeTemporaryToken(refreshToken, memberDto.getId());
    }

    @Test
    @DisplayName("토큰 재발급을 테스트한다.")
    public void reissueToken() {
        // given
        RefreshToken refreshToken = new RefreshToken("valid-refresh-token");
        Long memberId = 1L;
        Claims claims = mock(Claims.class);
        Member member = MemberFactory.defaultMember().id(memberId).build();
        AccessToken accessToken = new AccessToken("new-access-token");
        RefreshToken newRefreshToken = new RefreshToken("new-refresh-token");
        given(jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value())).willReturn(claims);
        given(claims.get("memberId", Long.class)).willReturn(memberId);
        given(refreshTokenManager.findTemporaryToken(memberId)).willReturn(Optional.of(refreshToken));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(jsonWebTokenProvider.generateAccessToken(anyString(), anyMap())).willReturn(accessToken);
        given(jsonWebTokenProvider.generateRefreshToken(anyString(), anyMap())).willReturn(newRefreshToken);

        // when
        JsonWebTokenResponse response = jsonWebTokenService.reissueToken(refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo(accessToken);
        assertThat(response.refreshToken()).isEqualTo(newRefreshToken);
        then(refreshTokenManager).should().findTemporaryToken(memberId);
        then(jsonWebTokenEvaluator).should().verifyRefreshToken(refreshToken.value());
        then(memberRepository).should().findById(memberId);
        then(jsonWebTokenProvider).should().generateAccessToken(anyString(), anyMap());
        then(jsonWebTokenProvider).should().generateRefreshToken(anyString(), anyMap());
        then(refreshTokenManager).should().updateTemporaryToken(memberId, newRefreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰을 찾을 수 없으면 실패한다.")
    public void reissueToken2() {
        // given
        Long memberId = 1L;
        RefreshToken refreshToken = new RefreshToken("valid-refresh-token");
        Claims claims = mock(Claims.class);
        given(jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value())).willReturn(claims);
        given(claims.get("memberId", Long.class)).willReturn(1L);
        given(refreshTokenManager.findTemporaryToken(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jsonWebTokenService.reissueToken(refreshToken))
                .isInstanceOf(AuthTokenException.NotFoundRefresh.class);
    }

    @Test
    @DisplayName("리프레시 토큰의 주인이 없으면 실패한다.")
    public void reissueToken3() {
        // given
        RefreshToken refreshToken = new RefreshToken("valid-refresh-token");
        Long memberId = 1L;
        Claims claims = mock(Claims.class);
        given(refreshTokenManager.findTemporaryToken(memberId)).willReturn(Optional.of(refreshToken));
        given(jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value())).willReturn(claims);
        given(claims.get("memberId", Long.class)).willReturn(memberId);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jsonWebTokenService.reissueToken(refreshToken))
                .isInstanceOf(AuthTokenException.NotFoundRefreshOwner.class);
    }
}













//package revi1337.onsquad.auth.application;
//
//import io.jsonwebtoken.Claims;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import revi1337.onsquad.auth.domain.vo.AccessToken;
//import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;
//import revi1337.onsquad.auth.domain.vo.RefreshToken;
//import revi1337.onsquad.auth.error.exception.AuthTokenException;
//import revi1337.onsquad.factory.MemberFactory;
//import revi1337.onsquad.member.domain.Member;
//import revi1337.onsquad.member.domain.MemberRepository;
//import revi1337.onsquad.member.dto.MemberDto;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.BDDMockito.*;
//
//@DisplayName("JsonWebTokenService 테스트")
//@ExtendWith(MockitoExtension.class)
//class JsonWebTokenServiceTest {
//
//    @Mock private MemberRepository memberRepository;
//    @Mock private RefreshTokenManager refreshTokenManager;
//    @Mock private JsonWebTokenProvider jsonWebTokenProvider;
//    @Mock private JsonWebTokenEvaluator jsonWebTokenEvaluator;
//    @InjectMocks private JsonWebTokenService jsonWebTokenService;
//
//    @DisplayName("AccessToken 과 RefreshToken 을 포함한 TokenPair 가 만들어지는지 확인한다.")
//    @Test
//    public void generateTokenPair() {
//        // given
//        MemberDto memberDto = MemberDto.from(MemberFactory.defaultMember().build());
//        AccessToken accessToken = AccessToken.of("ACCESS-TOKEN");
//        RefreshToken refreshToken = RefreshToken.of("REFRESH-TOKEN");
//        given(jsonWebTokenProvider.generateAccessToken(anyString(), anyMap())).willReturn(accessToken);
//        given(jsonWebTokenProvider.generateRefreshToken(anyString(), anyMap())).willReturn(refreshToken);
//
//        // when
//        JsonWebTokenResponse jsonWebTokenResponse = jsonWebTokenService.generateTokenPair(memberDto);
//
//        // then
//        assertThat(jsonWebTokenResponse.refreshToken()).isEqualTo(refreshToken);
//        assertThat(jsonWebTokenResponse.accessToken()).isEqualTo(accessToken);
//    }
//
//    @DisplayName("RefreshToken 의 저장은 refreshTokenStoreService 에게 위임한다.")
//    @Test
//    public void storeTemporaryTokenInMemory() {
//        // given
//        RefreshToken refreshToken = RefreshToken.of("REFRESH-TOKEN");
//        MemberDto memberDto = MemberDto.builder().id(10L).build();
//
//        // when
//        jsonWebTokenService.storeTemporaryTokenInMemory(refreshToken, memberDto.getId());
//
//        // then
//        then(refreshTokenManager).should(times(1))
//                .storeTemporaryToken(refreshToken, memberDto.getId());
//    }
//
//    @Test
//    @DisplayName("토큰 재발급을 테스트한다.")
//    public void reissueToken() {
//        // given
//        RefreshToken refreshToken = new RefreshToken("valid-refresh-token");
//        Long memberId = 1L;
//        Claims claims = mock(Claims.class);
//        Member member = MemberFactory.defaultMember().id(memberId).build();
//
//        given(refreshTokenManager.findTemporaryToken(refreshToken)).willReturn(Optional.of(memberId));
//        given(jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value())).willReturn(claims);
//        given(claims.get("memberId", Long.class)).willReturn(memberId);
//        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
//
//        AccessToken accessToken = new AccessToken("new-access-token");
//        RefreshToken newRefreshToken = new RefreshToken("new-refresh-token");
//
//        given(jsonWebTokenProvider.generateAccessToken(anyString(), anyMap())).willReturn(accessToken);
//        given(jsonWebTokenProvider.generateRefreshToken(anyString(), anyMap())).willReturn(newRefreshToken);
//
//        // when
//        JsonWebTokenResponse response = jsonWebTokenService.reissueToken(refreshToken);
//
//        // then
//        assertThat(response.accessToken()).isEqualTo(accessToken);
//        assertThat(response.refreshToken()).isEqualTo(newRefreshToken);
//        then(refreshTokenManager).should().findTemporaryToken(refreshToken);
//        then(jsonWebTokenEvaluator).should().verifyRefreshToken(refreshToken.value());
//        then(memberRepository).should().findById(memberId);
//        then(jsonWebTokenProvider).should().generateAccessToken(anyString(), anyMap());
//        then(jsonWebTokenProvider).should().generateRefreshToken(anyString(), anyMap());
//        then(refreshTokenManager).should().removeTemporaryToken(refreshToken);
//        then(refreshTokenManager).should().storeTemporaryToken(newRefreshToken, memberId);
//    }
//
//    @Test
//    @DisplayName("리프레시 토큰의 주인이 일치하지 않으면 실패한다.")
//    public void reissueToken2() {
//        // given
//        RefreshToken refreshToken = new RefreshToken("valid-refresh-token");
//        Long memberId = 1L;
//        Claims claims = mock(Claims.class);
//        given(refreshTokenManager.findTemporaryToken(refreshToken)).willReturn(Optional.of(memberId));
//        given(jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value())).willReturn(claims);
//        given(claims.get("memberId", Long.class)).willReturn(2L);
//
//        // when & then
//        assertThatThrownBy(() -> jsonWebTokenService.reissueToken(refreshToken))
//                .isInstanceOf(AuthTokenException.NotFoundRefresh.class);
//    }
//
//    @Test
//    @DisplayName("리프레시 토큰의 주인이 없으면 실패한다.")
//    public void reissueToken3() {
//        // given
//        RefreshToken refreshToken = new RefreshToken("valid-refresh-token");
//        Long memberId = 1L;
//        Claims claims = mock(Claims.class);
//        given(refreshTokenManager.findTemporaryToken(refreshToken)).willReturn(Optional.of(memberId));
//        given(jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value())).willReturn(claims);
//        given(claims.get("memberId", Long.class)).willReturn(memberId);
//        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> jsonWebTokenService.reissueToken(refreshToken))
//                .isInstanceOf(AuthTokenException.NotFoundRefreshOwner.class);
//    }
//}