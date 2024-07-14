package revi1337.onsquad.factory;

import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad.domain.vo.*;

public class SquadFactory {

    public static final Title TITLE = new Title("Squad 타이틀");
    public static final Content CONTENT = new Content("Sauad 내용");
    public static final Capacity CAPACITY = new Capacity(8);
    public static final Categories CATEGORIES = new Categories(Category.BADMINTON);
    public static final Address ADDRESS = new Address("주소", "상세주소");
    public static final String KAKAO_LINK = "카카오 오픈채팅 링크";
    public static final String DISCORD_LINK = "디스코드 링크";

    public static Squad withTitle(Title title) {
        return defaultSquad().title(title).build();
    }

    public static Squad withContent(Content content) {
        return defaultSquad().content(content).build();
    }

    public static Squad withCapacity(Capacity capacity) {
        return defaultSquad().capacity(capacity).build();
    }

    public static Squad withCategory(Categories category) {
        return defaultSquad().categories(category).build();
    }

    public static Squad withAddress(Address address) {
        return defaultSquad().address(address).build();
    }

    public static Squad withKakaoLink(String kakaoLink) {
        return defaultSquad().kakaoLink(kakaoLink).build();
    }

    public static Squad withDiscordLink(String discordLink) {
        return defaultSquad().discordLink(discordLink).build();
    }

    public static Squad.SquadBuilder defaultSquad() {
        return Squad.builder()
                .title(TITLE)
                .content(CONTENT)
                .capacity(CAPACITY)
                .categories(CATEGORIES)
                .address(ADDRESS)
                .kakaoLink(KAKAO_LINK)
                .discordLink(DISCORD_LINK);
    }

}
