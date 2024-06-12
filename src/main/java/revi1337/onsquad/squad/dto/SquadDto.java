package revi1337.onsquad.squad.dto;

import lombok.Builder;
import lombok.Getter;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.dto.MemberDto;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.vo.Capacity;
import revi1337.onsquad.squad.domain.vo.Categories;
import revi1337.onsquad.squad.domain.vo.Content;
import revi1337.onsquad.squad.domain.vo.Title;

import java.util.List;

@Getter
public class SquadDto {

    private Title title;
    private Content content;
    private Capacity capacity;
    private Categories categories;
    private Address address;
    private String kakakoLink;
    private String discordLink;
    private MemberDto memberDto;

    @Builder(builderClassName = "SquadDtoBuilder")
    private SquadDto(Title title, Content content, Capacity capacity, Categories categories, Address address, String kakakoLink, String discordLink, MemberDto memberDto) {
        this.title = title;
        this.content = content;
        this.capacity = capacity;
        this.categories = categories;
        this.address = address;
        this.kakakoLink = kakakoLink;
        this.discordLink = discordLink;
        this.memberDto = memberDto;
    }

    @Builder(builderClassName = "SquadDtoValueBuilder", builderMethodName = "create")
    private SquadDto(String title, String content, int capacity, List<String> categories, String address, String addressDetail, String kakakoLink, String discordLink, MemberDto memberDto) {
        this.title = new Title(title);
        this.content = new Content(content);
        this.capacity = new Capacity(capacity);
        this.categories = new Categories(categories.toArray(new String[0]));
        this.address = new Address(address, addressDetail);
        this.kakakoLink = kakakoLink;
        this.discordLink = discordLink;
        this.memberDto = memberDto;
    }

    public Squad toEntity(Member member) {
        return Squad.builder()
                .title(title)
                .content(content)
                .capacity(capacity)
                .categories(categories)
                .address(address)
                .kakaoLink(kakakoLink)
                .discordLink(discordLink)
                .member(member)
                .build();
    }
}
