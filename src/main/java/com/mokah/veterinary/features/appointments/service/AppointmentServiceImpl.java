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
    @Transactional
    public AppointmentResponse create(AppointmentCreateDTO dto) {

        validatePastDate(dto.appointmentDate());
        validateBranchSchedule(dto.branchExternalId(), dto.appointmentDate());

        validateAppointmentOverlap(
                dto.veterinarianExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                null
        );

        validatePetOverlap(
                dto.petExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                null
        );

        Branch branch = branchService.entityByExternalId(dto.branchExternalId());

        Appointment entity = mapper.toEntity(dto);

        entity.setStatus(AppointmentStatus.PENDING);
        entity.setPet(petService.entityByExternalId(dto.petExternalId()));
        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));
        entity.setBranch(branch);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public AppointmentResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public Appointment entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "externalId", externalId));
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

        validatePastDate(dto.appointmentDate());
        validateBranchSchedule(dto.branchExternalId(), dto.appointmentDate());

        if (dto.status() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Use DELETE endpoint to cancel appointments.");
        }

        if (dto.status() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Appointments can only be completed via Visit.");
        }

        validateAppointmentOverlap(
                dto.veterinarianExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                externalId
        );

        validatePetOverlap(
                dto.petExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                externalId
        );

        mapper.update(entity, dto);

        entity.setBranch(branchService.entityByExternalId(dto.branchExternalId()));
        entity.setPet(petService.entityByExternalId(dto.petExternalId()));
        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {

        Appointment appointment = entityByExternalId(externalId);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot cancel a COMPLETED appointment.");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Appointment already cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        repository.save(appointment);
    }

    private void validateAppointmentOverlap(
            UUID veterinarianExternalId,
            LocalDateTime start,
            Integer durationMinutes,
            UUID currentAppointmentId) {

        LocalDateTime end = start.plusMinutes(durationMinutes);

        List<Appointment> appointments =
                repository.findByVeterinarian_ExternalIdAndStatusIn(
                        veterinarianExternalId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                );

        for (Appointment a : appointments) {

            if (currentAppointmentId != null
                    && a.getExternalId().equals(currentAppointmentId)) {
                continue;
            }

            LocalDateTime aStart = a.getAppointmentDate();
            LocalDateTime aEnd = aStart.plusMinutes(a.getDurationMinutes());

            boolean overlap = start.isBefore(aEnd) && end.isAfter(aStart);

            if (overlap) {
                throw new AppointmentOverlapException(
                        "Veterinarian already has an overlapping appointment."
                );
            }
        }
    }

    private void validatePetOverlap(
            UUID petExternalId,
            LocalDateTime start,
            Integer durationMinutes,
            UUID currentAppointmentId) {

        LocalDateTime end = start.plusMinutes(durationMinutes);

        List<Appointment> appointments =
                repository.findByPet_ExternalIdAndStatusIn(
                        petExternalId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                );

        for (Appointment a : appointments) {

            if (currentAppointmentId != null
                    && a.getExternalId().equals(currentAppointmentId)) {
                continue;
            }

            LocalDateTime aStart = a.getAppointmentDate();
            LocalDateTime aEnd = aStart.plusMinutes(a.getDurationMinutes());

            boolean overlap = start.isBefore(aEnd) && end.isAfter(aStart);

            if (overlap) {
                throw new BusinessRuleException(
                        "Pet already has an overlapping appointment."
                );
            }
        }
    }

    private void validatePastDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("Cannot schedule in the past.");
        }
    }

    private void validateBranchSchedule(UUID branchId, LocalDateTime dateTime) {

        Branch branch = branchService.entityByExternalId(branchId);

        LocalTime time = dateTime.toLocalTime();

        if (time.isBefore(branch.getOpeningTime()) ||
                time.isAfter(branch.getClosingTime())) {

            throw new InvalidAppointmentTimeException("Branch is closed at that time.");
        }
    }
}