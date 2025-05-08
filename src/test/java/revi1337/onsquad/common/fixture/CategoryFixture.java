package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.category.domain.vo.CategoryType.ACTIVITY;
import static revi1337.onsquad.category.domain.vo.CategoryType.ALL;
import static revi1337.onsquad.category.domain.vo.CategoryType.BADMINTON;
import static revi1337.onsquad.category.domain.vo.CategoryType.BASEBALL;
import static revi1337.onsquad.category.domain.vo.CategoryType.BASKETBALL;
import static revi1337.onsquad.category.domain.vo.CategoryType.BILLIARDS;
import static revi1337.onsquad.category.domain.vo.CategoryType.ESCAPEROOM;
import static revi1337.onsquad.category.domain.vo.CategoryType.EXHIBITION;
import static revi1337.onsquad.category.domain.vo.CategoryType.FISHING;
import static revi1337.onsquad.category.domain.vo.CategoryType.FITNESS;
import static revi1337.onsquad.category.domain.vo.CategoryType.FUTSAL;
import static revi1337.onsquad.category.domain.vo.CategoryType.GAME;
import static revi1337.onsquad.category.domain.vo.CategoryType.GOLF;
import static revi1337.onsquad.category.domain.vo.CategoryType.HIKING;
import static revi1337.onsquad.category.domain.vo.CategoryType.ICEFISHING;
import static revi1337.onsquad.category.domain.vo.CategoryType.ICESKATING;
import static revi1337.onsquad.category.domain.vo.CategoryType.MANGACAFE;
import static revi1337.onsquad.category.domain.vo.CategoryType.MOVIE;
import static revi1337.onsquad.category.domain.vo.CategoryType.MUSICAL;
import static revi1337.onsquad.category.domain.vo.CategoryType.PERFORMANCE;
import static revi1337.onsquad.category.domain.vo.CategoryType.PINGPONG;
import static revi1337.onsquad.category.domain.vo.CategoryType.PPAGI;
import static revi1337.onsquad.category.domain.vo.CategoryType.RAFTING;
import static revi1337.onsquad.category.domain.vo.CategoryType.RUNNING;
import static revi1337.onsquad.category.domain.vo.CategoryType.SCUBADIVING;
import static revi1337.onsquad.category.domain.vo.CategoryType.SKIRESORT;
import static revi1337.onsquad.category.domain.vo.CategoryType.SNOWFESTIVAL;
import static revi1337.onsquad.category.domain.vo.CategoryType.SOCCER;
import static revi1337.onsquad.category.domain.vo.CategoryType.SURFING;
import static revi1337.onsquad.category.domain.vo.CategoryType.SWIMMINGPOOL;
import static revi1337.onsquad.category.domain.vo.CategoryType.TENNIS;
import static revi1337.onsquad.category.domain.vo.CategoryType.TRAVEL;
import static revi1337.onsquad.category.domain.vo.CategoryType.VALLEY;
import static revi1337.onsquad.category.domain.vo.CategoryType.VR;
import static revi1337.onsquad.category.domain.vo.CategoryType.WATERPARK;

import java.util.List;
import revi1337.onsquad.category.domain.Category;

public class CategoryFixture {

    public static List<Category> ALL_CATEGORIES() {
        return Category.fromCategoryTypes(List.of(ALL));
    }

    public static List<Category> ALL_WITH_SOME_CATEGORIES() {
        return Category.fromCategoryTypes(List.of(ALL, GAME, BADMINTON));
    }

    public static List<Category> CATEGORIES_1() {
        return Category.fromCategoryTypes(List.of(GAME, BADMINTON, TENNIS));
    }

    public static List<Category> CATEGORIES_2() {
        return Category.fromCategoryTypes(List.of(FUTSAL, SOCCER, PINGPONG));
    }

    public static List<Category> CATEGORIES_3() {
        return Category.fromCategoryTypes(List.of(BILLIARDS, BASKETBALL, BASEBALL));
    }

    public static List<Category> CATEGORIES_4() {
        return Category.fromCategoryTypes(List.of(GOLF, FISHING, SCUBADIVING));
    }

    public static List<Category> CATEGORIES_5() {
        return Category.fromCategoryTypes(List.of(SURFING, RAFTING, FITNESS));
    }

    public static List<Category> CATEGORIES_6() {
        return Category.fromCategoryTypes(List.of(TRAVEL, RUNNING, HIKING));
    }

    public static List<Category> CATEGORIES_7() {
        return Category.fromCategoryTypes(List.of(ACTIVITY, MOVIE, PERFORMANCE));
    }

    public static List<Category> CATEGORIES_8() {
        return Category.fromCategoryTypes(List.of(EXHIBITION, MUSICAL, ESCAPEROOM));
    }

    public static List<Category> CATEGORIES_9() {
        return Category.fromCategoryTypes(List.of(MANGACAFE, VR, SWIMMINGPOOL));
    }

    public static List<Category> CATEGORIES_10() {
        return Category.fromCategoryTypes(List.of(WATERPARK, PPAGI, VALLEY));
    }

    public static List<Category> CATEGORIES_11() {
        return Category.fromCategoryTypes(List.of(SKIRESORT, ICESKATING, ICEFISHING, SNOWFESTIVAL));
    }

    public static final Category ALL_CATEGORY = new Category(ALL);
    public static final Category GAME_CATEGORY = new Category(GAME);
    public static final Category BADMINTON_CATEGORY = new Category(BADMINTON);
    public static final Category TENNIS_CATEGORY = new Category(TENNIS);
    public static final Category FUTSAL_CATEGORY = new Category(FUTSAL);
    public static final Category SOCCER_CATEGORY = new Category(SOCCER);
    public static final Category PINGPONG_CATEGORY = new Category(PINGPONG);
    public static final Category BILLIARDS_CATEGORY = new Category(BILLIARDS);
    public static final Category BASKETBALL_CATEGORY = new Category(BASKETBALL);
    public static final Category BASEBALL_CATEGORY = new Category(BASEBALL);
    public static final Category GOLF_CATEGORY = new Category(GOLF);
    public static final Category FISHING_CATEGORY = new Category(FISHING);
    public static final Category SCUBADIVING_CATEGORY = new Category(SCUBADIVING);
    public static final Category SURFING_CATEGORY = new Category(SURFING);
    public static final Category RAFTING_CATEGORY = new Category(RAFTING);
    public static final Category FITNESS_CATEGORY = new Category(FITNESS);
    public static final Category TRAVEL_CATEGORY = new Category(TRAVEL);
    public static final Category RUNNING_CATEGORY = new Category(RUNNING);
    public static final Category HIKING_CATEGORY = new Category(HIKING);
    public static final Category ACTIVITY_CATEGORY = new Category(ACTIVITY);
    public static final Category MOVIE_CATEGORY = new Category(MOVIE);
    public static final Category PERFORMANCE_CATEGORY = new Category(PERFORMANCE);
    public static final Category EXHIBITION_CATEGORY = new Category(EXHIBITION);
    public static final Category MUSICAL_CATEGORY = new Category(MUSICAL);
    public static final Category ESCAPEROOM_CATEGORY = new Category(ESCAPEROOM);
    public static final Category MANGACAFE_CATEGORY = new Category(MANGACAFE);
    public static final Category VR_CATEGORY = new Category(VR);
    public static final Category SWIMMINGPOOL_CATEGORY = new Category(SWIMMINGPOOL);
    public static final Category WATERPARK_CATEGORY = new Category(WATERPARK);
    public static final Category PPAGI_CATEGORY = new Category(PPAGI);
    public static final Category VALLEY_CATEGORY = new Category(VALLEY);
    public static final Category SKIRESORT_CATEGORY = new Category(SKIRESORT);
    public static final Category ICESKATING_CATEGORY = new Category(ICESKATING);
    public static final Category ICEFISHING_CATEGORY = new Category(ICEFISHING);
    public static final Category SNOWFESTIVAL_CATEGORY = new Category(SNOWFESTIVAL);

}
