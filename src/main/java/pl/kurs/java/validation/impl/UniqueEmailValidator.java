package pl.kurs.java.validation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.java.repository.PatientRepository;
import pl.kurs.java.validation.annotation.UniqueEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

   private final PatientRepository patientRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !patientRepository.existsByEmail(email);
    }

}
