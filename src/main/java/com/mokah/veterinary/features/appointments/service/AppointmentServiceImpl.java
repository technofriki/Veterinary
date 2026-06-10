package com.mokah.veterinary.features.appointments.service;

import com.mokah.veterinary.common.exception.AppointmentOverlapException;
import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.InvalidAppointmentTimeException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.Specification.AppointmentSpecification;
import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.mapper.AppointmentMapper;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.repository.AppointmentRepository;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.branches.service.BranchService;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final PetService petService;
    private final VeterinarianService veterinarianService;
    private final BranchService branchService;

    @Override
    public AppointmentResponse create(AppointmentCreateDTO dto) {

        Boolean busyVet = repository.existsByVeterinarian_ExternalIdAndAppointmentDateAndStatus(
                dto.veterinarianExternalId(),
                dto.appointmentDate(), // Asegúrate de que el método en el DTO se llame así (o usa dto.getAppointmentDate())
                AppointmentStatus.CONFIRMED
        );

        if (busyVet) {
            throw new AppointmentOverlapException("El veterinario seleccionado ya tiene un turno confirmado en esa franja horaria.");
        }

        Boolean busyPet = repository.existsByPet_ExternalIdAndAppointmentDateAndStatus(
                dto.petExternalId(),
                dto.appointmentDate(),
                AppointmentStatus.CONFIRMED
        );

        if (busyPet) {
            throw new BusinessRuleException("La mascota ya tiene un turno confirmado asignado a la misma hora.");
        }

        if (dto.appointmentDate().isBefore(LocalDateTime.now())){
            throw new InvalidAppointmentTimeException("Cannot schedule an appointment in the past.");
        }
        Branch branch = branchService.entityByExternalId(dto.branchExternalId());
        LocalTime appoinmentTime = dto.appointmentDate().toLocalTime();
        if (appoinmentTime.isBefore(branch.getOpeningTime()) || appoinmentTime.isAfter(branch.getClosingTime())){
            throw new InvalidAppointmentTimeException("The branch is closed at that time.");
        }

        Appointment entity = mapper.toEntity(dto);
        entity.setStatus(AppointmentStatus.PENDING);

        entity.setPet(petService.entityByExternalId(dto.petExternalId()));

        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

        entity.setBranch(branch);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Appointment entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "externalId", externalId));
    }

    @Override
    public AppointmentResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<AppointmentResponse> findAll(
            LocalDateTime appointmentDate,
            String reason,
            AppointmentStatus status,
            UUID petExternalId,
            UUID veterinarianExternalId) {

        PredicateSpecification<Appointment> spec = PredicateSpecification.allOf(
                AppointmentSpecification.hasAppointmentDate(appointmentDate),
                AppointmentSpecification.hasReason(reason),
                AppointmentSpecification.hasStatus(status),
                AppointmentSpecification.hasPetExternalId(petExternalId),
                AppointmentSpecification.hasVeterinarianExternalId(veterinarianExternalId)
        );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    @Transactional
    public AppointmentResponse update(UUID externalId, AppointmentUpdateDTO dto) {

        Appointment entity = entityByExternalId(externalId);


        if(dto.status() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("To cancel an appointment, please use the DELETE endpoint.");
        }
        if (dto.status() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Appointments can only be marked as COMPLETED by creating a Visit.");
        }


        if (dto.appointmentDate().isBefore(LocalDateTime.now())){
            throw new InvalidAppointmentTimeException("Cannot reschedule an appointment to the past.");
        }


        Branch branch = branchService.entityByExternalId(dto.branchExternalId());
        LocalTime appointmentTime = dto.appointmentDate().toLocalTime();
        if (appointmentTime.isBefore(branch.getOpeningTime()) || appointmentTime.isAfter(branch.getClosingTime())){
            throw new InvalidAppointmentTimeException("The branch is closed at that time.");
        }


        boolean busyVet = repository.existsByVeterinarian_ExternalIdAndAppointmentDateAndStatusAndExternalIdNot(
                dto.veterinarianExternalId(),
                dto.appointmentDate(),
                AppointmentStatus.CONFIRMED,
                externalId // Pasamos el ID actual para que no choque consigo mismo
        );
        if (busyVet) {
            throw new AppointmentOverlapException("veterinarian has already been scheduled for that time.");
        }

        boolean busyPet = repository.existsByPet_ExternalIdAndAppointmentDateAndStatusAndExternalIdNot(
                dto.petExternalId(),
                dto.appointmentDate(),
                AppointmentStatus.CONFIRMED,
                externalId
        );
        if (busyPet) {
            throw new BusinessRuleException("Pet has already been scheduled for that time.");
        }


        mapper.update(entity, dto);
        entity.setBranch(branch);
        entity.setPet(petService.entityByExternalId(dto.petExternalId()));
        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {

        Appointment appointment = entityByExternalId(externalId);
        if(appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new BusinessRuleException("Can not cancel a COMPLETED appointment.");
        }

        if (appointment.getStatus().equals(AppointmentStatus.CANCELLED)) {
            throw new BusinessRuleException("The appointment is already cancelled.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        repository.save(appointment);
    }
}
