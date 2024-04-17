package revi1337.onsquad.member.presentation.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.presentation.request.MemberJoinRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintStringValidatorTest {

    @DisplayName("StringValidation 에 실패하면 잘못된 파라미터들이 리스트로 넘어온다 (Record)")
    @Test
    public void validateWhenRecord() {
        // given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                "test@mail.com", "password", "passwordNotSame", "nickname", "anywhere"
        );
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        // when
        Set<ConstraintViolation<MemberJoinRequest>> validate = validator.validate(memberJoinRequest);
        List<String> invalidParameters = validate.stream()
                .map(constraintViolation -> Arrays.stream(constraintViolation.getPropertyPath().toString().split("\\."))
                        .reduce((first, second) -> second).orElse(null))
                .toList();

        // then
        assertThat(invalidParameters).containsExactlyInAnyOrder("password", "passwordConfirm");
    }

    @DisplayName("StringValidation 에 실패하면 잘못된 파라미터들이 리스트로 넘어온다 (Class)")
    @Test
    public void validateWhenGeneralClass() {
        // given
        TestRequest testRequest = new TestRequest(
                "test@mail.com", "password", "passwordNotSame", "nickname", "anywhere"
        );
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        // when
        Set<ConstraintViolation<TestRequest>> validate = validator.validate(testRequest);
        List<String> invalidParameters = validate.stream()
                .map(constraintViolation -> Arrays.stream(constraintViolation.getPropertyPath().toString().split("\\."))
                        .reduce((first, second) -> second).orElse(null))
                .toList();

        // then
        assertThat(invalidParameters).containsExactlyInAnyOrder("password", "passwordConfirm");
    }

    @StringValidator
    static class TestRequest implements StringComparator {

        private String email;
        private String password;
        private String passwordConfirm;
        private String nickname;
        private String address;

        public TestRequest(String email, String password, String passwordConfirm, String nickname, String address) {
            this.email = email;
            this.password = password;
            this.passwordConfirm = passwordConfirm;
            this.nickname = nickname;
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getPasswordConfirm() {
            return passwordConfirm;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public Map<String, String> inspectStrings() {
            return Map.of("password", password, "passwordConfirm", passwordConfirm);
        }
    }
}