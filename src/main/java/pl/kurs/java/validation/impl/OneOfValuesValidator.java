package pl.kurs.java.validation.impl;

import org.springframework.stereotype.Service;
import pl.kurs.java.validation.annotation.OneOfValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

@Service
public class OneOfValuesValidator implements ConstraintValidator<OneOfValues, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(OneOfValues constraint) {
        this.allowedValues = Set.of(constraint.values());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return allowedValues.contains(value);
    }
}
