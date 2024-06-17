package revi1337.onsquad.crew.dto;

import lombok.Builder;
import lombok.Getter;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.HashTags;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.image.dto.ImageDto;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.dto.MemberDto;

import java.util.List;

@Getter
public class CrewDto {

    private Long id;
    private Name name;
    private Introduce introduce;
    private Detail detail;
    private HashTags hashTags;
    private String kakaoLink;
    private ImageDto imageDto;
    private MemberDto memberDto;

    @Builder
    private CrewDto(Long id, Name name, Introduce introduce, Detail detail, HashTags hashTags, String kakaoLink, ImageDto imageDto, MemberDto memberDto) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.detail = detail;
        this.hashTags = hashTags;
        this.kakaoLink = kakaoLink;
        this.imageDto = imageDto;
        this.memberDto = memberDto;
    }

    /**
     * for CrewCreateRequest
     */
    public static CrewDto of(String name, String introduce, List<String> hashTags, String kakaoLink, ImageDto imageDto, MemberDto memberDto) {
        return CrewDto.builder()
                .name(new Name(name))
                .introduce(new Introduce(introduce))
                .hashTags(new HashTags(hashTags))
                .kakaoLink(kakaoLink)
                .imageDto(imageDto)
                .memberDto(memberDto)
                .build();
    }

    /**
     * for CrewCreateRequest
     */
    public static CrewDto of(String name, String introduce, List<String> hashTags, String kakaoLink, byte[] imageData, MemberDto memberDto) {
        return CrewDto.builder()
                .name(new Name(name))
                .introduce(new Introduce(introduce))
                .hashTags(new HashTags(hashTags))
                .kakaoLink(kakaoLink)
                .imageDto(ImageDto.builder()
                            .image(imageData)
                            .build())
                .memberDto(memberDto)
                .build();
    }

    /**
     * for CrewJoinRequest & CrewAcceptRequest
     */
    public static CrewDto of(String crewName, MemberDto memberDto) {
        return CrewDto.builder()
                .name(new Name(crewName))
                .memberDto(memberDto)
                .build();
    }

    public Crew toEntity(Image image, Member member) {
        return Crew.builder()
                .id(id)
                .name(name)
                .introduce(introduce)
                .detail(detail)
                .hashTags(hashTags)
                .kakaoLink(kakaoLink)
                .image(image)
                .member(member)
                .build();
    }

    public static CrewDto from(Crew crew) {
        return CrewDto.builder()
                .id(crew.getId())
                .name(crew.getName())
                .introduce(crew.getIntroduce())
                .detail(crew.getDetail())
                .hashTags(crew.getHashTags())
                .kakaoLink(crew.getKakaoLink())
                .imageDto(ImageDto.from(crew.getImage()))
                .memberDto(MemberDto.from(crew.getMember()))
                .build();
    }
}
