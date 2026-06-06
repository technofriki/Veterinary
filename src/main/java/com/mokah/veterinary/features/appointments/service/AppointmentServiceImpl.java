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

        entity.setPet(petService.entityById(dto.petId()));
        entity.setVeterinarian(veterinarianService.entityById(dto.veterinarianId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Appointment entityById(Long id){
        return repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Appointment not found with id " + id));
    }
    @Override
    public List<AppointmentResponse> findAll(
            LocalDateTime date,
            String reason,
            AppointmentStatus status,
            Long petId,
            Long veterinarianId
    ) {

        PredicateSpecification<Appointment> spec = PredicateSpecification.allOf(
                AppointmentSpecification.hasDate(date),
                AppointmentSpecification.hasReason(reason),
                AppointmentSpecification.hasStatus(status),
                AppointmentSpecification.hasPetId(petId),
                AppointmentSpecification.hasVeterinarianId(veterinarianId)
        );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    public AppointmentResponse findById(Long id) {
        return mapper.toResponse(entityById(id));
    }


    @Override
    public AppointmentResponse update(Long id, AppointmentUpdateDTO dto) {

        Appointment entity = entityById(id);

        mapper.update(entity, dto);

        entity.setPet(petService.entityById(dto.petId()));
        entity.setVeterinarian(veterinarianService.entityById(dto.veterinarianId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.delete(entityById(id));
    }
}
