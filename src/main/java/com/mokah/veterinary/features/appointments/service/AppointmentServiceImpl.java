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
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

        validateHalfHourSlot(dto.appointmentDate());

        validateVeterinarianSchedule(
                dto.veterinarianExternalId(),
                dto.appointmentDate());

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

        validateHalfHourSlot(dto.appointmentDate());
        validateVeterinarianSchedule(
                dto.veterinarianExternalId(),
                dto.appointmentDate());

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

        LocalTime lastAvailableSlot =
                branch.getClosingTime().minusMinutes(30);

        if (time.isBefore(branch.getOpeningTime())
                || time.isAfter(lastAvailableSlot)) {

            throw new InvalidAppointmentTimeException(
                    "Branch is closed at that time."
            );
        }
    }

    private void validateHalfHourSlot(LocalDateTime dateTime) {

        int minute = dateTime.getMinute();

        if (minute != 0 && minute != 30) {
            throw new InvalidAppointmentTimeException(
                    "Appointments can only start on the hour or half hour."
            );
        }
    }

    private void validateVeterinarianSchedule(UUID veterinarianExternalId, LocalDateTime dateTime) {
        Veterinarian vet = veterinarianService.entityByExternalId(veterinarianExternalId);
        LocalTime time = dateTime.toLocalTime();

        if (vet.getWorkStartTime() == null || vet.getWorkEndTime() == null) {
            throw new BusinessRuleException("Veterinarian does not have working hours configured.");
        }

        LocalTime lastSlot = vet.getWorkEndTime().minusMinutes(30);

        if (time.isBefore(vet.getWorkStartTime()) || time.isAfter(lastSlot)) {
            throw new InvalidAppointmentTimeException("Appointment is outside veterinarian working hours.");
        }
    }

    @Transactional
    @Override
    public void confirmAppointment(UUID externalId) {

        Appointment appointment = entityByExternalId(externalId);

        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            throw new BusinessRuleException("Appointment already confirmed.");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot confirm a cancelled appointment.");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);

        repository.save(appointment);
    }

    @Override
    public List<LocalTime> getAvailableSlots(UUID veterinarianExternalId, LocalDate date) {

        Veterinarian vet = veterinarianService.entityByExternalId(veterinarianExternalId);

        if (vet.getWorkStartTime() == null || vet.getWorkEndTime() == null) {
            throw new BusinessRuleException("Veterinarian does not have working hours configured.");
        }

        // Slots ocupados ese día
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        List<LocalTime> takenSlots = repository
                .findByVeterinarian_ExternalIdAndStatusInAndAppointmentDateBetween(
                        veterinarianExternalId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED),
                        dayStart,
                        dayEnd)
                .stream()
                .map(a -> a.getAppointmentDate().toLocalTime())
                .toList();

        // Slots disponibles
        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime slot = vet.getWorkStartTime();

        while (!slot.plusMinutes(30).isAfter(vet.getWorkEndTime())) {
            if (!takenSlots.contains(slot)) {
                availableSlots.add(slot);
            }
            slot = slot.plusMinutes(30);
        }

        return availableSlots;
    }

}