package revi1337.onsquad.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

class ErrorCodeDocsTest {

    private static final String BASE_PACKAGE = "revi1337.onsquad";
    private static final Path SNIPPET_PATH = Path.of("build/generated-snippets/error-codes/error-codes.adoc");
    private static final String SUFFIX = "ErrorCode";

    private static final Map<String, String> GROUP_TITLES = new LinkedHashMap<>();

    static {
        GROUP_TITLES.put("CommonErrorCode", "공통");
        GROUP_TITLES.put("AuthErrorCode", "로그인");
        GROUP_TITLES.put("TokenErrorCode", "토큰");
        GROUP_TITLES.put("VerificationErrorCode", "이메일 인증");
        GROUP_TITLES.put("MemberErrorCode", "회원");
        GROUP_TITLES.put("CrewErrorCode", "크루");
        GROUP_TITLES.put("CrewMemberErrorCode", "크루 멤버");
        GROUP_TITLES.put("CrewRequestErrorCode", "크루 가입 신청");
        GROUP_TITLES.put("AnnounceErrorCode", "크루 공지");
        GROUP_TITLES.put("HashtagErrorCode", "해시태그");
        GROUP_TITLES.put("SquadErrorCode", "스쿼드");
        GROUP_TITLES.put("SquadMemberErrorCode", "스쿼드 멤버");
        GROUP_TITLES.put("SquadRequestErrorCode", "스쿼드 참가 신청");
        GROUP_TITLES.put("SquadCommentErrorCode", "스쿼드 댓글");
        GROUP_TITLES.put("SquadCategoryErrorCode", "스쿼드 카테고리");
        GROUP_TITLES.put("CategoryErrorCode", "카테고리");
        GROUP_TITLES.put("FileErrorCode", "파일");
        GROUP_TITLES.put("MagicByteErrorCode", "이미지 형식");
    }

    @Test
    @DisplayName("모든 ErrorCode 의 코드는 서로 중복되지 않는다.")
    void codesAreUnique() {
        Map<Class<?>, List<ErrorCode>> grouped = scan();

        Map<String, List<String>> usages = new HashMap<>();
        for (Map.Entry<Class<?>, List<ErrorCode>> entry : grouped.entrySet()) {
            for (ErrorCode errorCode : entry.getValue()) {
                usages.computeIfAbsent(errorCode.getCode(), key -> new ArrayList<>())
                        .add(entry.getKey().getSimpleName() + "." + errorCode);
            }
        }
        Map<String, List<String>> duplicated = usages.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertThat(usages).isNotEmpty();
        assertThat(duplicated).as("중복된 에러 코드").isEmpty();
    }

    @Test
    @DisplayName("ErrorCode 구현 enum 을 모아 에러 코드 문서 스니펫을 생성한다.")
    void generateErrorCodeSnippet() throws IOException {
        Map<Class<?>, List<ErrorCode>> grouped = scan();

        List<Class<?>> ordered = new ArrayList<>(grouped.keySet());
        ordered.sort(Comparator
                .comparingInt((Class<?> type) -> indexOf(type))
                .thenComparing(Class::getSimpleName));

        StringBuilder adoc = new StringBuilder();
        for (Class<?> type : ordered) {
            adoc.append(renderGroup(type, grouped.get(type)));
        }

        Files.createDirectories(SNIPPET_PATH.getParent());
        Files.writeString(SNIPPET_PATH, adoc.toString(), StandardCharsets.UTF_8);

        assertThat(Files.readString(SNIPPET_PATH)).contains("C001");
    }

    private Map<Class<?>, List<ErrorCode>> scan() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(ErrorCode.class));

        Map<Class<?>, List<ErrorCode>> grouped = new LinkedHashMap<>();
        for (BeanDefinition component : provider.findCandidateComponents(BASE_PACKAGE)) {
            Class<?> type = load(component.getBeanClassName());
            if (type.isEnum()) {
                grouped.put(type, List.of((ErrorCode[]) type.getEnumConstants()));
            }
        }
        return grouped;
    }

    private Class<?> load(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("에러 코드 클래스를 불러올 수 없습니다. " + className, e);
        }
    }

    private int indexOf(Class<?> type) {
        int index = 0;
        for (String name : GROUP_TITLES.keySet()) {
            if (name.equals(type.getSimpleName())) {
                return index;
            }
            index++;
        }
        return GROUP_TITLES.size();
    }

    private String renderGroup(Class<?> type, List<ErrorCode> errorCodes) {
        String simpleName = type.getSimpleName();
        String title = GROUP_TITLES.getOrDefault(simpleName, simpleName.replace(SUFFIX, ""));

        StringBuilder group = new StringBuilder();
        group.append("[[error-codes-").append(simpleName.replace(SUFFIX, "").toLowerCase()).append("]]\n");
        group.append("=== ").append(title).append("\n\n");
        group.append("[cols=\"1,1,6\",options=\"header\"]\n|===\n|코드|HTTP|설명\n\n");
        for (ErrorCode errorCode : errorCodes) {
            group.append("|`+").append(errorCode.getCode()).append("+`\n");
            group.append("|").append(errorCode.getStatus()).append("\n");
            group.append("|").append(describe(errorCode)).append("\n\n");
        }
        group.append("|===\n\n");
        return group.toString();
    }

    private String describe(ErrorCode errorCode) {
        return errorCode.getDescription()
                .replace("%d", "N")
                .replace("%s", "...")
                .replace("|", "\\|");
    }
}
