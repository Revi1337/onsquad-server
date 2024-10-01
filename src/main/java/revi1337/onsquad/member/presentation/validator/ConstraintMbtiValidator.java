package revi1337.onsquad.member.presentation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import revi1337.onsquad.member.domain.vo.Mbti;

public class ConstraintMbtiValidator implements ConstraintValidator<MbtiValidator, String> {

    private boolean ignoreCase;

    @Override
    public void initialize(MbtiValidator constraintAnnotation) {
        ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(String mbti, ConstraintValidatorContext constraintValidatorContext) {
        try {
            if (ignoreCase) {
                Mbti.valueOf(mbti.toUpperCase());
            } else {
                Mbti.valueOf(mbti);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
