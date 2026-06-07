package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.service.AppointmentServiceImpl;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianServiceImpl;
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

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final VeterinarianServiceImpl veterinarianService;
    private final AppointmentServiceImpl appointmentService;


    @Override
    public Visit entityById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Visit not found by id"+id));
    }
    @Override
    public VisitResponse create(VisitRequest request){
        Visit entity = visitMapper.toEntity(request);
        entity.setVeterinarian(veterinarianService.entityById(request.veterinarianId()));
        entity.setAppointment(appointmentService.entityById(request.appointmentId()));

        return visitMapper.toResponse(visitRepository.save(entity));
    }

    @Override
    public List<VisitResponse> findAll(Long visitId, String veterinarianName, String petName){
        PredicateSpecification<Visit> specification = PredicateSpecification.allOf(
                VisitSpecification.hasId(visitId),
                VisitSpecification.hasVeterinarianName(veterinarianName),
                VisitSpecification.hasPetName(petName)
        );
        return visitMapper.toResponseList(visitRepository.findAll(specification));
    }
    @Override
    public VisitResponse update(Long id, VisitRequest visitRequest){
       Visit entity = entityById(id);
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
