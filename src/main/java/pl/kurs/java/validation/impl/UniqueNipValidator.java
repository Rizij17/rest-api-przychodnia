package pl.kurs.java.validation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.java.repository.DoctorRepository;
import pl.kurs.java.validation.annotation.UniqueNip;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@Service
@RequiredArgsConstructor
public class UniqueNipValidator implements ConstraintValidator<UniqueNip, String> {

  private final DoctorRepository doctorRepository;

    @Override
    public boolean isValid(String nip, ConstraintValidatorContext context) {
        return !doctorRepository.existsByNip(nip);
    }
}
