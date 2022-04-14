package pl.kurs.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.java.model.Patient;
import pl.kurs.java.model.command.UpdatePatientCommand;
import pl.kurs.java.repository.PatientRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public Page<Patient> findAll(Pageable pageable) {
        return patientRepository.findAllWithAppointments(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Patient> findById(int id) {
        return patientRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Patient> findByIdWithAppointments(int id) {
        return patientRepository.findByIdWithAppointments(id);
    }

    @Transactional
    public Patient save(Patient patient) {
        return patientRepository.saveAndFlush(patient);
    }

    @Transactional
    public void deleteById(int id) {
        patientRepository.findById(id).ifPresent(patientRepository::delete);
    }

    @Transactional
    public Patient edit(Patient toEdit, UpdatePatientCommand updatePatientCommand) {
        toEdit.setName(updatePatientCommand.getName());
        toEdit.setOwnerName(updatePatientCommand.getOwnerName());
        toEdit.setOwnerSurname(updatePatientCommand.getOwnerSurname());
        toEdit.setEmail(updatePatientCommand.getEmail());
        toEdit.setVersion(updatePatientCommand.getVersion());
        return patientRepository.save(toEdit);
    }

}
