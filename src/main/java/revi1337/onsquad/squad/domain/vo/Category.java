package revi1337.onsquad.squad.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum Category {

    ALL("전체"),

    GAME("게임"), BADMINTON("배드민턴"), TENNIS("테니스"),
    FUTSAL("풋살"), SOCCER("축구"), PINGPONG("탁구"),
    BILLIARDS("당구"), BASKETBALL("농구"), BASEBALL("야구"),

    GOLF("골프"), FISHING("낚시"), SCUBADIVING("스쿠버다이빙"),
    SURFING("서핑"), RAFTING("카약/레프팅/보트"), FITNESS("헬스"),
    TRAVEL("여행"), RUNNING("러닝"), HIKING("등산"),
    ACTIVITY("액티비티"),

    MOVIE("영화"), PERFORMANCE("공연"), EXHIBITION("전시"),
    MUSICAL("뮤지컬"), ESCAPEROOM("방탈출"), MANGACAFE("만화카페"),
    VR("VR"),

    SWIMMINGPOOL("수영장"), WATERPARK("워터파크"), PPAGI("빠지"), VALLEY("계곡"),

    SKIRESORT("스키장"), ICESKATING("스케이트"), ICEFISHING("빙어낚시"), SNOWFESTIVAL("눈꽃축제");

    private final String text;

    private static EnumSet<Category> defaultEnumSet() {
        return EnumSet.allOf(Category.class);
    }

    public static Set<String> categoryTextValues() {
        return defaultEnumSet().stream()
                .map(Category::getText)
                .collect(Collectors.toSet());
    }

    public static boolean contains(String targetCategory) {
        return categoryTextValues().contains(targetCategory);
    }

    public static Category fromText(String text) {
        return defaultEnumSet().stream()
                .filter(category -> category.getText().equals(text))
                .findFirst()
                .orElse(Category.ALL);
    }
}
