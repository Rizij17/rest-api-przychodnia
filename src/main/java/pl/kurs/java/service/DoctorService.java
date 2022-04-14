package pl.kurs.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.java.model.Doctor;
import pl.kurs.java.model.command.UpdatedDoctorCommand;
import pl.kurs.java.repository.DoctorRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;


    @Transactional(readOnly = true)
    public Page<Doctor> findAll(Pageable pageable) {
        return doctorRepository.findAllWithAppointments(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> findById(int id) {
        return doctorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> findByIdWithAppointments(int id) {
        return doctorRepository.findByIdWithAppointments(id);
    }

    @Transactional
    public Doctor save(Doctor doctor) {
        return doctorRepository.saveAndFlush(doctor);
    }

    public Doctor fire(Doctor toFire) {
        toFire.setFired(true);
        return doctorRepository.saveAndFlush(toFire);
    }

    public Doctor edit(Doctor toEdit, UpdatedDoctorCommand updatedDoctorCommand) {
        toEdit.setName(updatedDoctorCommand.getName());
        toEdit.setSurname(updatedDoctorCommand.getSurname());
        toEdit.setType(updatedDoctorCommand.getType());
        toEdit.setAnimalType(updatedDoctorCommand.getAnimalType());
        toEdit.setRate(updatedDoctorCommand.getRate());
        toEdit.setNip(updatedDoctorCommand.getNip());
        toEdit.setVersion(updatedDoctorCommand.getVersion());
        return doctorRepository.saveAndFlush(toEdit);
    }
}
