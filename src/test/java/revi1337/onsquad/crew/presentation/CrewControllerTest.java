//package revi1337.onsquad.crew.presentation;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockMultipartFile;
//import revi1337.onsquad.auth.application.JsonWebTokenProvider;
//import revi1337.onsquad.crew.domain.Crew;
//import revi1337.onsquad.crew.domain.CrewJpaRepository;
//import revi1337.onsquad.crew.domain.vo.Name;
//import revi1337.onsquad.crew.dto.request.CrewCreateRequest;
//import revi1337.onsquad.crew.dto.request.CrewJoinRequest;
//import revi1337.onsquad.crew.error.CrewErrorCode;
//import revi1337.onsquad.crew_member.domain.CrewMember;
//import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
//import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
//import revi1337.onsquad.factory.CrewFactory;
//import revi1337.onsquad.factory.CrewMemberFactory;
//import revi1337.onsquad.factory.ImageFactory;
//import revi1337.onsquad.factory.MemberFactory;
//import revi1337.onsquad.image.domain.Image;
//import revi1337.onsquad.image.domain.vo.SupportAttachmentType;
//import revi1337.onsquad.inrastructure.s3.application.S3BucketUploader;
//import revi1337.onsquad.member.domain.Member;
//import revi1337.onsquad.member.domain.MemberRepository;
//import revi1337.onsquad.member.domain.vo.Nickname;
//import revi1337.onsquad.support.IntegrationTestSupport;
//
//import java.util.Collections;
//import java.util.List;
//
//import static java.nio.charset.StandardCharsets.*;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.http.HttpHeaders.*;
//import static org.springframework.http.HttpMethod.*;
//import static org.springframework.http.MediaType.*;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@DisplayName("CrewController 통합 테스트")
//class CrewControllerTest extends IntegrationTestSupport {
//
//    @Autowired private CrewJpaRepository crewJpaRepository;
//    @Autowired private CrewMemberRepository crewMemberRepository;
//    @Autowired private MemberRepository memberRepository;
//    @MockBean private S3BucketUploader s3BucketUploader;
//    @Autowired private JsonWebTokenProvider jsonWebTokenProvider;
//
//    @Nested
//    @DisplayName("CheckCrewNameDuplicate 메소드를 테스트한다.")
//    class CheckCrewNameDuplicate {
//
//        @Test
//        @DisplayName("크루명이 중복되면 false 를 반환한다.")
//        public void checkCrewNameDuplicate1() throws Exception {
//            // given
//            Member member = MemberFactory.defaultMember().build();
//            Image image = ImageFactory.defaultImage();
//            Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
//            memberRepository.save(member);
//            crewJpaRepository.save(crew);
//            String validCrewName = CrewFactory.NAME.getValue();
//
//            // when & then
//            mockMvc.perform(
//                            get("/api/v1/crew/check")
//                                    .param("crewName", validCrewName)
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.success").value(true))
//                    .andExpect(jsonPath("$.error").doesNotExist())
//                    .andExpect(jsonPath("$.data.duplicate").value(true));
//        }
//
//        @Test
//        @DisplayName("크루명이 중복되지 않으면 true 를 반환한다.")
//        public void checkCrewNameDuplicate2() throws Exception {
//            // given
//            Member member = MemberFactory.defaultMember().build();
//            Image image = ImageFactory.defaultImage();
//            Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
//            memberRepository.save(member);
//            crewJpaRepository.save(crew);
//            String invalidCrewName = "없는 크루 이름";
//
//            // when & then
//            mockMvc.perform(
//                            get("/api/v1/crew/check")
//                                    .param("crewName", invalidCrewName)
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.success").value(true))
//                    .andExpect(jsonPath("$.error").doesNotExist())
//                    .andExpect(jsonPath("$.data.duplicate").value(false));
//        }
//    }
//
//    @Nested
//    @DisplayName("createNewCrew 메소드를 테스트한다.")
//    class CreateNewCrew {
//
//        private String accessToken;
//
//        @BeforeEach
//        void tearDown() {
//            this.accessToken = jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, Collections.singletonMap("memberId", 1L)).value();
//            this.accessToken = "Bearer " + this.accessToken;
//        }
//
//        @Test
//        @DisplayName("Crew 게시글 생성을 성공한다.")
//        public void createNewCrew() throws Exception {
//            // given
//            CrewCreateRequest crewCreateRequest = new CrewCreateRequest("크루 이름", "크루 소개", "크루 디테일", List.of("해시태그1", "해시태그2"), "카카오 링크");
//            byte[] pngImage = SupportAttachmentType.PNG.getMagicByte();
//            MockMultipartFile file = new MockMultipartFile("file", "test.png", "multipart/form-data", pngImage);
//            MockMultipartFile request = new MockMultipartFile("crewCreateRequest", null, "application/json", objectMapper.writeValueAsString(crewCreateRequest).getBytes(UTF_8));
//            Member member = MemberFactory.defaultMember().nickname(new Nickname("닉네임 1")).build();
//            memberRepository.save(member);
//            given(s3BucketUploader.uploadCrew(file.getBytes(), file.getOriginalFilename())).willReturn("[imageLink]");
//
//            // when & then
//            mockMvc.perform(
//                            multipart(POST, "/api/v1/crew/new")
//                                    .file(file)
//                                    .file(request)
//                                    .accept(APPLICATION_JSON)
//                                    .contentType(MULTIPART_FORM_DATA)
//                                    .header(AUTHORIZATION, accessToken)
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value(201));
//        }
//    }
//
//    @Nested
//    @DisplayName("findCrew 메소드를 테스트한다.")
//    class FindCrew {
//
//        @Test
//        @DisplayName("크루명이 존재하면 단일 Crew 를 반환한다.")
//        public void findCrew1() throws Exception {
//            // given
//            Member member = MemberFactory.defaultMember().build();
//            Image image = ImageFactory.defaultImage();
//            Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
//            memberRepository.save(member);
//            crewJpaRepository.save(crew);
//            String validCrewName = CrewFactory.NAME.getValue();
//
//            // when & then
//            mockMvc.perform(
//                            get("/api/v1/crew")
//                                    .param("crewName", validCrewName)
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.success").value(true))
//                    .andExpect(jsonPath("$.error").doesNotExist())
//                    .andExpect(jsonPath("$.data.crewName").value(CrewFactory.NAME.getValue()))
//                    .andExpect(jsonPath("$.data.crewDetail").value(CrewFactory.DETAIL.getValue()))
//                    .andExpect(jsonPath("$.data.hashTags").isArray())
//                    .andExpect(jsonPath("$.data.crewOwner").value(MemberFactory.NICKNAME.getValue() + " 크루장"))
//                    .andExpect(jsonPath("$.data.imageUrl").isString());
//        }
//
//        @Test
//        @DisplayName("크루명이 존재하지 않으면 예외를 반환한다.")
//        public void findCrew2() throws Exception {
//            // given
//            Member member = MemberFactory.defaultMember().build();
//            Image image = ImageFactory.defaultImage();
//            Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
//            memberRepository.save(member);
//            crewJpaRepository.save(crew);
//            String invalidCrewName = "asdadasd";
//
//            // when & then
//            mockMvc.perform(
//                            get("/api/v1/crew")
//                                    .param("crewName", invalidCrewName)
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value(404))
//                    .andExpect(jsonPath("$.success").value(false))
//                    .andExpect(jsonPath("$.error").exists())
//                    .andExpect(jsonPath("$.error.message").value(String.format("%s 크루 게시글이 존재하지 않습니다.", invalidCrewName)));
//        }
//    }
//
//    @Nested
//    @DisplayName("findCrews 메소드를 테스트한다.")
//    class FindCrews {
//
//        @Test
//        @DisplayName("3개의 크루를 저장하면 3개의 Crew 가 반환된다.")
//        public void findCrews1() throws Exception {
//            // given
//            Member member1 = MemberFactory.defaultMember().nickname(new Nickname("닉네임 1")).build();
//            Member member2 = MemberFactory.defaultMember().nickname(new Nickname("닉네임 2")).build();
//            Image image1 = ImageFactory.defaultImage();
//            Image image2 = ImageFactory.defaultImage();
//            Image image3 = ImageFactory.defaultImage();
//            Crew crew1 = CrewFactory.defaultCrew().name((new Name("크루 이름 1"))).member(member1).image(image1).build();
//            Crew crew2 = CrewFactory.defaultCrew().name((new Name("크루 이름 2"))).member(member1).image(image2).build();
//            Crew crew3 = CrewFactory.defaultCrew().name((new Name("크루 이름 3"))).member(member2).image(image3).build();
//            memberRepository.saveAll(List.of(member1, member2));
//            crewJpaRepository.saveAll(List.of(crew1, crew2, crew3));
//
//            // when & then
//            mockMvc.perform(
//                            get("/api/v1/crews")
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.success").value(true))
//                    .andExpect(jsonPath("$.error").doesNotExist())
//                    .andExpect(jsonPath("$.data").isArray())
//                    .andExpect(jsonPath("$.data[0].crewName").value("크루 이름 1"));
//        }
//
//        @Test
//        @DisplayName("Crew 를 저장하지 않으면 빈 배열이 반환된다.")
//        public void findCrews2() throws Exception {
//            // when & then
//            mockMvc.perform(
//                            get("/api/v1/crews")
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.success").value(true))
//                    .andExpect(jsonPath("$.error").doesNotExist())
//                    .andExpect(jsonPath("$.data").isArray())
//                    .andExpect(jsonPath("$.data").isEmpty());
//        }
//    }
//
//    @Nested
//    @DisplayName("JoinCrew 메소드를 테스트한다.")
//    class JoinCrew {
//
//        private String accessToken;
//
//        @BeforeEach
//        void tearDown() {
//            this.accessToken = jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, Collections.singletonMap("memberId", 1L)).value();
//            this.accessToken = "Bearer " + this.accessToken;
//        }
//
//        @Test
//        @DisplayName("Crew 에 이미 가입신청을 한 사용자면, 오류를 뱉는다.")
//        public void joinCrew1() throws Exception {
//            // given
//            Member member = MemberFactory.defaultMember().nickname(new Nickname("닉네임 1")).build();
//            Image image = ImageFactory.defaultImage();
//            Crew crew = CrewFactory.defaultCrew().name((new Name("크루 이름 1"))).member(member).image(image).build();
//            CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).status(JoinStatus.PENDING).build();
//            memberRepository.save(member);
//            crewJpaRepository.save(crew);
//            crewMemberRepository.save(crewMember);
//            CrewJoinRequest crewJoinRequest = new CrewJoinRequest("크루 이름 1");
//
//            // when & then
//            mockMvc.perform(
//                            post("/api/v1/crew/join")
//                                    .content(objectMapper.writeValueAsString(crewJoinRequest))
//                                    .contentType(APPLICATION_JSON)
//                                    .header(AUTHORIZATION, accessToken)
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.success").value(false))
//                    .andExpect(jsonPath("$.error.message").value(String.format(CrewErrorCode.ALREADY_REQUEST.getDescription(), "크루 이름 1")));
//        }
//
//        @Test
//        @DisplayName("Crew 에 이미 소속된 사용자라면 오류를 뱉는다.")
//        public void joinCrew2() throws Exception {
//            // given
//            Member member = MemberFactory.defaultMember().nickname(new Nickname("닉네임 1")).build();
//            Image image = ImageFactory.defaultImage();
//            Crew crew = CrewFactory.defaultCrew().name((new Name("크루 이름 1"))).member(member).image(image).build();
//            CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).status(JoinStatus.ACCEPT).build();
//            memberRepository.save(member);
//            crewJpaRepository.save(crew);
//            crewMemberRepository.save(crewMember);
//            CrewJoinRequest crewJoinRequest = new CrewJoinRequest("크루 이름 1");
//
//            // when & then
//            mockMvc.perform(
//                            post("/api/v1/crew/join")
//                                    .content(objectMapper.writeValueAsString(crewJoinRequest))
//                                    .contentType(APPLICATION_JSON)
//                                    .header(AUTHORIZATION, accessToken)
//                    )
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.success").value(false))
//                    .andExpect(jsonPath("$.error.message").value(String.format(CrewErrorCode.ALREADY_JOIN.getDescription(), "크루 이름 1")));
//        }
//
//        @Test
//        @DisplayName("Crew 에 가입인청을 한 이력이 없으면 성공한다.")
//        public void joinCrew3() throws Exception {
//            // given
//            Member member = MemberFactory.defaultMember().nickname(new Nickname("닉네임 1")).build();
//            Image image = ImageFactory.defaultImage();
//            Crew crew = CrewFactory.defaultCrew().name((new Name("크루 이름 1"))).member(member).image(image).build();
//            memberRepository.save(member);
//            crewJpaRepository.save(crew);
//            CrewJoinRequest crewJoinRequest = new CrewJoinRequest("크루 이름 1");
//
//            // when & then
//            mockMvc.perform(
//                            post("/api/v1/crew/join")
//                                    .content(objectMapper.writeValueAsString(crewJoinRequest))
//                                    .contentType(APPLICATION_JSON)
//                                    .header(AUTHORIZATION, accessToken)
//                    )
//                    .andExpect(status().isOk());
//        }
//    }
//}
