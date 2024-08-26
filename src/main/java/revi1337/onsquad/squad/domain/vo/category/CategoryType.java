package revi1337.onsquad.squad.domain.vo.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad.error.exception.SquadDomainException;

import java.util.*;
import java.util.stream.Collectors;

import static revi1337.onsquad.squad.error.SquadErrorCode.INVALID_CATEGORY;

@Getter
@RequiredArgsConstructor
public enum CategoryType {

    ALL("전체", 1L),
    GAME("게임", 2L),
    BADMINTON("배드민턴", 3L),
    TENNIS("테니스", 4L),
    FUTSAL("풋살",5L),
    SOCCER("축구", 6L),
    PINGPONG("탁구", 7L),
    BILLIARDS("당구", 8L),
    BASKETBALL("농구", 9L),
    BASEBALL("야구", 10L),
    GOLF("골프", 11L),
    FISHING("낚시", 12L),
    SCUBADIVING("스쿠버다이빙", 13L),
    SURFING("서핑", 14L),
    RAFTING("카약/레프팅/보트", 15L),
    FITNESS("헬스", 16L),
    TRAVEL("여행", 17L),
    RUNNING("러닝", 18L),
    HIKING("등산", 19L),
    ACTIVITY("액티비티", 20L),
    MOVIE("영화", 21L),
    PERFORMANCE("공연", 22L),
    EXHIBITION("전시", 23L),
    MUSICAL("뮤지컬", 24L),
    ESCAPEROOM("방탈출", 25L),
    MANGACAFE("만화카페", 26L),
    VR("VR", 27L),
    SWIMMINGPOOL("수영장", 28L),
    WATERPARK("워터파크", 29L),
    PPAGI("빠지", 30L),
    VALLEY("계곡", 31L),
    SKIRESORT("스키장", 32L),
    ICESKATING("스케이트", 33L),
    ICEFISHING("빙어낚시", 34L),
    SNOWFESTIVAL("눈꽃축제", 35L);

    private final String text;
    private final Long pk;

    private static final Map<String, CategoryType> categoryHashMap = Collections.unmodifiableMap(new HashMap<>() {
        { unmodifiableList().forEach(category -> put(category.getText(), category)); }
    });

    public static List<CategoryType> unmodifiableList() {
        return List.of(CategoryType.values());
    }

    public static List<CategoryType> fromTexts(List<String> categoryTexts) {
        return new LinkedHashSet<>(categoryTexts).stream()
                .map(CategoryType::mapFromText)
                .peek(CategoryType::validateCategory)
                .collect(Collectors.toList());
    }

    public static CategoryType mapFromText(String categoryText) {
        return categoryHashMap.get(categoryText);
    }

    private static void validateCategory(CategoryType categoryType) {
        if (categoryType == null) {
            throw new SquadDomainException.InvalidCategory(INVALID_CATEGORY);
        }
    }
}
