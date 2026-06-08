package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository repository;
    private final VisitMapper mapper;
    private final VeterinarianService veterinarianService;
    private final AppointmentService appointmentService;

    @Override
    public VisitResponse create(VisitRequest request) {

        Visit entity = mapper.toEntity(request);

        entity.setVeterinarian(
                veterinarianService.entityByExternalId(
                        request.veterinarianExternalId()
                )
        );

        entity.setAppointment(
                appointmentService.entityByExternalId(
                        request.appointmentExternalId()
                )
        );

        return mapper.toResponse(
                repository.save(entity)
        );
    }

    @Override
    public Visit entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit",
                                "externalId",
                                externalId
                        ));
    }

    @Override
    public VisitResponse findById(UUID externalId) {
        return mapper.toResponse(
                entityByExternalId(externalId)
        );
    }

    @Override
    public List<VisitResponse> findAll(
            UUID visitExternalId,
            String veterinarianName,
            String petName
    ) {

        PredicateSpecification<Visit> spec = PredicateSpecification.allOf(
                VisitSpecification.hasExternalId(visitExternalId),
                VisitSpecification.hasVeterinarianName(veterinarianName),
                VisitSpecification.hasPetName(petName)
        );

        return mapper.toResponseList(
                repository.findAll(spec)
        );
    }

    @Override
    public VisitResponse update(
            UUID externalId,
            VisitRequest request
    ) {

        Visit entity = entityByExternalId(externalId);

        mapper.update(entity, request);

        entity.setVeterinarian(
                veterinarianService.entityByExternalId(
                        request.veterinarianExternalId()
                )
        );

        entity.setAppointment(
                appointmentService.entityByExternalId(
                        request.appointmentExternalId()
                )
        );

        return mapper.toResponse(
                repository.save(entity)
        );
    }

    @Override
    public void delete(UUID externalId) {
        repository.delete(
                entityByExternalId(externalId)
        );
    }
}
