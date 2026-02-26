package revi1337.onsquad.category.domain.entity.vo;

import static revi1337.onsquad.squad.domain.error.SquadErrorCode.INVALID_CATEGORY;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad.domain.error.SquadDomainException;

@Getter
@RequiredArgsConstructor
public enum CategoryType {

    GAME("게임", 1L),
    BADMINTON("배드민턴", 2L),
    TENNIS("테니스", 3L),
    FUTSAL("풋살", 4L),
    SOCCER("축구", 5L),
    PINGPONG("탁구", 6L),
    BILLIARDS("당구", 7L),
    BASKETBALL("농구", 8L),
    BASEBALL("야구", 9L),
    GOLF("골프", 10L),
    FISHING("낚시", 11L),
    SCUBADIVING("스쿠버다이빙", 12L),
    SURFING("서핑", 13L),
    RAFTING("카약/레프팅/보트", 14L),
    FITNESS("헬스", 15L),
    TRAVEL("여행", 16L),
    RUNNING("러닝", 17L),
    HIKING("등산", 18L),
    ACTIVITY("액티비티", 19L),
    MOVIE("영화", 20L),
    PERFORMANCE("공연", 21L),
    EXHIBITION("전시", 22L),
    MUSICAL("뮤지컬", 23L),
    ESCAPEROOM("방탈출", 24L),
    MANGACAFE("만화카페", 25L),
    VR("VR", 26L),
    SWIMMINGPOOL("수영장", 27L),
    WATERPARK("워터파크", 28L),
    PPAGI("빠지", 29L),
    VALLEY("계곡", 30L),
    SKIRESORT("스키장", 31L),
    ICESKATING("스케이트", 32L),
    ICEFISHING("빙어낚시", 33L),
    SNOWFESTIVAL("눈꽃축제", 34L);

    private final String text;
    private final Long pk;

    private static final Map<String, CategoryType> categoryTypeStorage = Stream.of(values()).collect(Collectors.toUnmodifiableMap(
            c -> c.getText().toUpperCase(),
            c -> c
    ));

    public static List<CategoryType> unmodifiableList() {
        return List.of(CategoryType.values());
    }

    public static List<CategoryType> fromTexts(List<String> categoryTexts) {
        return categoryTexts.stream()
                .map(CategoryType::fromText)
                .distinct()
                .toList();
    }

    public static CategoryType fromText(String categoryText) {
        try {
            CategoryType categoryType = categoryTypeStorage.get(categoryText.toUpperCase());
            if (categoryType == null) {
                throw new IllegalArgumentException();
            }
            return categoryType;
        } catch (Exception exception) {
            throw new SquadDomainException.InvalidCategory(INVALID_CATEGORY);
        }
    }
}

//package revi1337.onsquad.category.domain.entity.vo;
//
//import static revi1337.onsquad.squad.domain.error.SquadErrorCode.INVALID_CATEGORY;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import revi1337.onsquad.squad.domain.error.SquadDomainException;
//
//@Getter
//@RequiredArgsConstructor
//public enum CategoryType {
//
//    ALL("전체", 1L),
//    GAME("게임", 2L),
//    BADMINTON("배드민턴", 3L),
//    TENNIS("테니스", 4L),
//    FUTSAL("풋살", 5L),
//    SOCCER("축구", 6L),
//    PINGPONG("탁구", 7L),
//    BILLIARDS("당구", 8L),
//    BASKETBALL("농구", 9L),
//    BASEBALL("야구", 10L),
//    GOLF("골프", 11L),
//    FISHING("낚시", 12L),
//    SCUBADIVING("스쿠버다이빙", 13L),
//    SURFING("서핑", 14L),
//    RAFTING("카약/레프팅/보트", 15L),
//    FITNESS("헬스", 16L),
//    TRAVEL("여행", 17L),
//    RUNNING("러닝", 18L),
//    HIKING("등산", 19L),
//    ACTIVITY("액티비티", 20L),
//    MOVIE("영화", 21L),
//    PERFORMANCE("공연", 22L),
//    EXHIBITION("전시", 23L),
//    MUSICAL("뮤지컬", 24L),
//    ESCAPEROOM("방탈출", 25L),
//    MANGACAFE("만화카페", 26L),
//    VR("VR", 27L),
//    SWIMMINGPOOL("수영장", 28L),
//    WATERPARK("워터파크", 29L),
//    PPAGI("빠지", 30L),
//    VALLEY("계곡", 31L),
//    SKIRESORT("스키장", 32L),
//    ICESKATING("스케이트", 33L),
//    ICEFISHING("빙어낚시", 34L),
//    SNOWFESTIVAL("눈꽃축제", 35L);
//
//    private final String text;
//    private final Long pk;
//
//    private static final Map<String, CategoryType> categoryTypeStorage = Stream.of(values()).collect(Collectors.toUnmodifiableMap(
//            c -> c.getText().toUpperCase(),
//            c -> c
//    ));
//
//    public static List<CategoryType> unmodifiableList() {
//        return List.of(CategoryType.values());
//    }
//
//    public static List<CategoryType> fromTexts(List<String> categoryTexts) {
//        return categoryTexts.stream()
//                .map(CategoryType::fromText)
//                .distinct()
//                .toList();
//    }
//
//    public static CategoryType fromText(String categoryText) {
//        try {
//            CategoryType categoryType = categoryTypeStorage.get(categoryText.toUpperCase());
//            if (categoryType == null) {
//                throw new IllegalArgumentException();
//            }
//            return categoryType;
//        } catch (Exception exception) {
//            throw new SquadDomainException.InvalidCategory(INVALID_CATEGORY);
//        }
//    }
//}
