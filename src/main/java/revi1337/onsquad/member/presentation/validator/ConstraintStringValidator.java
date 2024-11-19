package revi1337.onsquad.member.presentation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.Objects;
import org.springframework.util.ReflectionUtils;

public class ConstraintStringValidator implements ConstraintValidator<StringValidator, StringComparator> {

    private static final String MESSAGE_TEMPLATE = "string does not match";
    private static final String UNSUPPORTED_CLASS = "unsupported class for validation";
    private static final String UNEXPECTED_FIELD = "unexpected record field";

    @Override
    public boolean isValid(StringComparator comparator, ConstraintValidatorContext context) {
        if (!comparator.compareResult()) {
            modifyDefaultConstraintViolation(comparator, context);
            return false;
        }
        return true;
    }

    private void modifyDefaultConstraintViolation(StringComparator comparator, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        Class<? extends StringComparator> comparatorClass = comparator.getClass();
        if (comparatorClass.isRecord()) {
            modifyClassMetadataIfRecord(comparator, context);
        } else if (!comparatorClass.isInterface() && !Modifier.isAbstract(comparatorClass.getModifiers())) {
            modifyClassMetadataIfGeneralClass(comparator, context);
        } else {
            throw new IllegalArgumentException(UNSUPPORTED_CLASS);
        }
    }

    private void modifyClassMetadataIfRecord(StringComparator comparator, ConstraintValidatorContext context) {
        Map<String, String> entries = comparator.inspectStrings();
        for (RecordComponent recordComponent : comparator.getClass().getRecordComponents()) {
            try {
                String recordFieldName = recordComponent.getAccessor().getName();
                Object recordFieldValue = recordComponent.getAccessor().invoke(comparator);
                if (entries.get(recordFieldName) == recordFieldValue) {
                    addAdditionalConstraintViolation(recordFieldName, context);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(UNEXPECTED_FIELD);
            }
        }
    }

    private void modifyClassMetadataIfGeneralClass(StringComparator comparator, ConstraintValidatorContext context) {
        Map<String, String> entries = comparator.inspectStrings();
        for (Field declaredField : comparator.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            String declaredFieldName = declaredField.getName();
            Object declaredFieldValue = ReflectionUtils.getField(declaredField, comparator);
            if (Objects.equals(entries.get(declaredFieldName), declaredFieldValue)) {
                addAdditionalConstraintViolation(declaredFieldName, context);
            }
        }
    }

    private void addAdditionalConstraintViolation(String fieldName, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(MESSAGE_TEMPLATE)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
