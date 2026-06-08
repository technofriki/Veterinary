package com.mokah.veterinary.features.appointments.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.Specification.AppointmentSpecification;
import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.mapper.AppointmentMapper;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.repository.AppointmentRepository;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final PetService petService;
    private final VeterinarianService veterinarianService;

    @Override
    public AppointmentResponse create(AppointmentCreateDTO dto) {

        Appointment entity = mapper.toEntity(dto);

        entity.setPet(petService.entityByExternalId(dto.petExternalId()));

        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

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
    public AppointmentResponse update(
            UUID externalId,
            AppointmentUpdateDTO dto) {

        Appointment entity = entityByExternalId(externalId);

        mapper.update(entity, dto);

        entity.setPet(petService.entityByExternalId(dto.petExternalId()));

        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(entityByExternalId(externalId));
    }
}
