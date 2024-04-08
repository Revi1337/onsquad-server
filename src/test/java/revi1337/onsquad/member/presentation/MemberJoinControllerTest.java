package revi1337.onsquad.member.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.application.MemberJoinService;
import revi1337.onsquad.member.domain.MemberRepository;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("회원가입 api 테스트")
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class MemberJoinControllerTest {

    @Autowired private MockMvc mockMvc;
    @SpyBean private MemberJoinService memberJoinService;

    @DisplayName("인증코드 발송이 정상적으로 동작하는지 확인한다.")
    @Test
    public void sendAuthCodeToEmail() throws Exception {
        // given
        String testEmail = "david122123@gmail.com";
        willDoNothing().given(memberJoinService).sendAuthCodeToEmail(testEmail);

        // when & then
        mockMvc.perform(
                        get("/api/v1/auth/send")
                                .queryParam("email", testEmail)
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(memberJoinService, times(1)).sendAuthCodeToEmail(testEmail);
    }
}
