package revi1337.onsquad.common.fixture;

import revi1337.onsquad.crew.domain.vo.Detail;
import revi1337.onsquad.crew.domain.vo.Introduce;
import revi1337.onsquad.crew.domain.vo.Name;

public class CrewValueFixture {

    public static final String CREW_NAME_VALUE = "크루 이름";
    public static final String CREW_INTRODUCE_VALUE = "크루 한줄 소개";
    public static final String CREW_DETAIL_VALUE = "크루 상세 정보";
    public static final String CREW_IMAGE_LINK_VALUE = "https://크루_이미지_링크.com";
    public static final String CREW_KAKAO_LINK_VALUE = "https://크루_카카오_링크.com";

    public static final String CHANGED_CREW_NAME_VALUE = "변경된 크루 이름";
    public static final String CHANGED_CREW_INTRODUCE_VALUE = "변경된 크루 한줄 소개";
    public static final String CHANGED_CREW_DETAIL_VALUE = "변경된 크루 상세 정보";
    public static final String CHANGED_CREW_IMAGE_LINK_VALUE = "https://변경된_크루_이미지_링크.com";
    public static final String CHANGED_CREW_KAKAO_LINK_VALUE = "https://변경된_크루_카카오_링크.com";

    public static final Name CREW_NAME = new Name("크루 이름");
    public static final Introduce CREW_INTRODUCE = new Introduce("크루 한줄 소개");
    public static final Detail CREW_DETAIL = new Detail("크루 상세 정보");

    public static final Name CHANGED_CREW_NAME = new Name("변경된 크루 이름");
    public static final Introduce CHANGED_CREW_INTRODUCE = new Introduce("변경된 크루 한줄 소개");
    public static final Detail CHANGED_CREW_DETAIL = new Detail("변경된 크루 상세 정보");

    public static final String DEFAULT_ORIGINAL_FILENAME = "dummy.png";

}
