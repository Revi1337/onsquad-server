package revi1337.onsquad.common.presentation.validator;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.presentation.request.MemberCreateRequest;
import revi1337.onsquad.member.presentation.request.MemberPasswordUpdateRequest;

class StringCompareValidatorTest {

    private static final String MISMATCH_MESSAGE = "string does not match";

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("회원 가입 요청")
    class memberCreateRequest {

        @Test
        @DisplayName("비밀번호와 확인이 같으면 위반이 없다")
        void noViolation_whenSame() {
            MemberCreateRequest request = create("a@a.com", "password", "password");

            assertThat(validator.validate(request)).isEmpty();
        }

        @Test
        @DisplayName("비밀번호와 확인이 다르면 두 필드 모두 불일치 위반이 된다")
        void violatesBothFields_whenDifferent() {
            MemberCreateRequest request = create("a@a.com", "password", "different");

            Set<ConstraintViolation<MemberCreateRequest>> violations = validator.validate(request);

            assertThat(propertyNames(violations)).containsExactlyInAnyOrder("password", "passwordConfirm");
            assertThat(messages(violations)).containsOnly(MISMATCH_MESSAGE);
        }

        @Test
        @DisplayName("비밀번호가 없으면 예외 없이 비밀번호의 @NotEmpty 위반만 남는다")
        void onlyNotEmptyViolation_whenPasswordMissing() {
            MemberCreateRequest request = create("a@a.com", null, "password");

            Set<ConstraintViolation<MemberCreateRequest>> violations = validator.validate(request);

            assertThat(propertyNames(violations)).containsExactly("password");
            assertThat(messages(violations)).doesNotContain(MISMATCH_MESSAGE);
        }

        @Test
        @DisplayName("비밀번호 확인이 없으면 예외 없이 비밀번호 확인의 @NotEmpty 위반만 남는다")
        void onlyNotEmptyViolation_whenPasswordConfirmMissing() {
            MemberCreateRequest request = create("a@a.com", "password", null);

            Set<ConstraintViolation<MemberCreateRequest>> violations = validator.validate(request);

            assertThat(propertyNames(violations)).containsExactly("passwordConfirm");
            assertThat(messages(violations)).doesNotContain(MISMATCH_MESSAGE);
        }

        @Test
        @DisplayName("둘 다 없으면 예외 없이 두 필드의 @NotEmpty 위반만 남는다")
        void onlyNotEmptyViolations_whenBothMissing() {
            MemberCreateRequest request = create("a@a.com", null, null);

            Set<ConstraintViolation<MemberCreateRequest>> violations = validator.validate(request);

            assertThat(propertyNames(violations)).containsExactlyInAnyOrder("password", "passwordConfirm");
            assertThat(messages(violations)).doesNotContain(MISMATCH_MESSAGE);
        }

        @Test
        @DisplayName("비교 대상이 아닌 필드가 null 이어도 불일치 위반이 붙지 않는다")
        void doesNotFlagUncomparedField_whenItIsNull() {
            MemberCreateRequest request = create(null, "password", "different");

            Set<ConstraintViolation<MemberCreateRequest>> violations = validator.validate(request);

            Set<String> propertyAndMessages = violations.stream()
                    .map(v -> v.getPropertyPath() + ":" + v.getMessage())
                    .collect(Collectors.toSet());
            assertThat(propertyAndMessages)
                    .contains("password:" + MISMATCH_MESSAGE, "passwordConfirm:" + MISMATCH_MESSAGE)
                    .doesNotContain("email:" + MISMATCH_MESSAGE);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경 요청")
    class memberPasswordUpdateRequest {

        @Test
        @DisplayName("새 비밀번호와 확인이 같으면 위반이 없다")
        void noViolation_whenSame() {
            MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("current", "new", "new");

            assertThat(validator.validate(request)).isEmpty();
        }

        @Test
        @DisplayName("새 비밀번호와 확인이 다르면 두 필드 모두 불일치 위반이 된다")
        void violatesBothFields_whenDifferent() {
            MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("current", "new", "different");

            Set<ConstraintViolation<MemberPasswordUpdateRequest>> violations = validator.validate(request);

            assertThat(propertyNames(violations)).containsExactlyInAnyOrder("newPassword", "newPasswordConfirm");
            assertThat(messages(violations)).containsOnly(MISMATCH_MESSAGE);
        }

        @Test
        @DisplayName("새 비밀번호가 없으면 예외 없이 @NotEmpty 위반만 남는다")
        void onlyNotEmptyViolation_whenNewPasswordMissing() {
            MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("current", null, "new");

            Set<ConstraintViolation<MemberPasswordUpdateRequest>> violations = validator.validate(request);

            assertThat(propertyNames(violations)).containsExactly("newPassword");
            assertThat(messages(violations)).doesNotContain(MISMATCH_MESSAGE);
        }

        @Test
        @DisplayName("새 비밀번호 확인이 없으면 예외 없이 @NotEmpty 위반만 남는다")
        void onlyNotEmptyViolation_whenNewPasswordConfirmMissing() {
            MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("current", "new", null);

            Set<ConstraintViolation<MemberPasswordUpdateRequest>> violations = validator.validate(request);

            assertThat(propertyNames(violations)).containsExactly("newPasswordConfirm");
            assertThat(messages(violations)).doesNotContain(MISMATCH_MESSAGE);
        }
    }

    @Nested
    @DisplayName("레코드가 아닌 클래스")
    class plainClass {

        @Test
        @DisplayName("필드 값이 같으면 위반이 없다")
        void noViolation_whenSame() {
            assertThat(validator.validate(new PlainComparator("a", "a", "other"))).isEmpty();
        }

        @Test
        @DisplayName("필드 값이 다르면 비교 대상 필드만 불일치 위반이 된다")
        void violatesComparedFieldsOnly_whenDifferent() {
            Set<ConstraintViolation<PlainComparator>> violations = validator.validate(new PlainComparator("a", "b", null));

            assertThat(propertyNames(violations)).containsExactlyInAnyOrder("first", "second");
        }

        @Test
        @DisplayName("비교 필드가 null 이어도 예외 없이 통과한다")
        void passesWithoutException_whenComparedFieldIsNull() {
            assertThat(validator.validate(new PlainComparator(null, "b", "other"))).isEmpty();
        }
    }

    private MemberCreateRequest create(String email, String password, String passwordConfirm) {
        return new MemberCreateRequest(email, password, passwordConfirm, "nick", "address", "detail");
    }

    private Set<String> propertyNames(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
    }

    private Set<String> messages(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }

    @StringCompare
    static class PlainComparator implements StringComparator {

        private final String first;
        private final String second;
        private final String unrelated;

        PlainComparator(String first, String second, String unrelated) {
            this.first = first;
            this.second = second;
            this.unrelated = unrelated;
        }

        @Override
        public Map<String, String> getComparedFields() {
            return StringComparator.fields("first", first, "second", second);
        }
    }
}
