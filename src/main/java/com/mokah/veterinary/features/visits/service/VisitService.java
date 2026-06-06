package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.service.AppointmentServiceImpl;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianServiceImpl;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.entity.VisitEntity;
import com.mokah.veterinary.features.visits.mapper.VisitMapper;
import com.mokah.veterinary.features.visits.repository.VisitRepository;
import com.mokah.veterinary.features.visits.specification.VisitSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class VisitService implements VisitServiceInterface {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final VeterinarianServiceImpl veterinarianService;
    private final AppointmentServiceImpl appointmentService;
    private final PetService petService;


    @Override
    public VisitEntity entityById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Visit not found by id"+id));
    }
    @Override
    public VisitResponse create(VisitRequest request){
        VisitEntity entity = visitMapper.toEntity(request);
        entity.setVeterinarian(veterinarianService.entityById(request.veterinarianId()));
        entity.setAppointment(appointmentService.entityById(request.appointmentId()));

        return visitMapper.toResponse(visitRepository.save(entity));
    }

    @Override
    public List<VisitResponse> findAll(Long visitId, String veterinarianName, String petName){
        PredicateSpecification<VisitEntity> specification = PredicateSpecification.allOf(
                VisitSpecification.hasId(visitId),
                VisitSpecification.hasVeterinarianName(veterinarianName),
                VisitSpecification.hasPetName(petName)
        );
        return visitMapper.toResponseList(visitRepository.findAll(specification));
    }
    @Override
    public VisitResponse update(Long id, VisitRequest visitRequest){
       VisitEntity entity = entityById(id);
       visitMapper.update(entity, visitRequest);
       entity.setVeterinarian(veterinarianService.entityById(visitRequest.veterinarianId()));
       entity.setAppointment(appointmentService.entityById(visitRequest.appointmentId()));
       return visitMapper.toResponse(visitRepository.save(entity));
    }
    @Override
    public VisitResponse findById(Long id){
        return visitMapper.toResponse(entityById(id));
    }

    @Override
    public void delete(Long id){
        visitRepository.delete(entityById(id));
    }

}
