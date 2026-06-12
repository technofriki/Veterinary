package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.common.exception.AppointmentNotConfirmedException;
import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.service.AppointmentService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.model.Visit;
import com.mokah.veterinary.features.visits.mapper.VisitMapper;
import com.mokah.veterinary.features.visits.repository.VisitRepository;
import com.mokah.veterinary.features.visits.specification.VisitSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository repository;
    private final VisitMapper mapper;
    private final VeterinarianService veterinarianService;
    private final AppointmentService appointmentService;

    @Transactional
    @Override
    public VisitResponse create(VisitRequest dto) {

        if(repository.existsByAppointment_ExternalId(dto.appointmentExternalId())){
            throw new BusinessRuleException(
                    "The appointment already has a registered visit."
            );
        }

        Appointment appointment = appointmentService.entityByExternalId(dto.appointmentExternalId());
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new AppointmentNotConfirmedException("The visit can not be created. Appointment must be confirmed. Appointment Status: " + appointment.getStatus());
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);

        Visit entity = mapper.toEntity(dto);

        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

        entity.setAppointment(appointment);


        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Visit entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit", "externalId", externalId));
    }

    @Override
    public VisitResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<VisitResponse> findAll(
            UUID visitExternalId,
            String veterinarianName,
            String petName) {

        PredicateSpecification<Visit> spec = PredicateSpecification.allOf(
                VisitSpecification.hasExternalId(visitExternalId),
                VisitSpecification.hasVeterinarianName(veterinarianName),
                VisitSpecification.hasPetName(petName)
        );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    @Transactional
    public VisitResponse update(
            UUID externalId,
            VisitRequest dto) {

        Visit entity = entityByExternalId(externalId);

        mapper.update(entity, dto);

        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

        if (!entity.getAppointment().getExternalId().equals(dto.appointmentExternalId())) {
            throw new BusinessRuleException("Cannot reassign a clinical visit to a different appointment.");
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<VisitResponse> findMedicalHistory(UUID petExternalId) {

        return mapper.toResponseList(
                repository.findByAppointment_Pet_ExternalId(petExternalId)
        );
    }

}
