package revi1337.onsquad.member.presentation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Map;

public class ConstraintStringValidator implements ConstraintValidator<StringValidator, StringComparator> {

    @Override
    public boolean isValid(StringComparator comparator, ConstraintValidatorContext context) {
        if (!comparator.getCompareResult()) {
            context.disableDefaultConstraintViolation();
            Map<String, String> entries = comparator.inspectStrings();
            Class<? extends StringComparator> comparatorClass = comparator.getClass();
            if (comparatorClass.isRecord()) {
                RecordComponent[] recordComponents = comparator.getClass().getRecordComponents();
                for (RecordComponent recordComponent : recordComponents) {
                    try {
                        String recordFieldName = recordComponent.getAccessor().getName();
                        Object recordFieldValue = recordComponent.getAccessor().invoke(comparator);
                        if (entries.get(recordFieldName) == recordFieldValue) {
                            context.buildConstraintViolationWithTemplate("string does not match")
                                    .addPropertyNode(recordFieldName)
                                    .addConstraintViolation();
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("unexpected record field");
                    }
                }
            }
            return false;
        }
        return true;
    }
}
