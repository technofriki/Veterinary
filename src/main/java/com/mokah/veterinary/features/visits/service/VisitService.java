package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.features.appointments.service.AppointmentServiceImpl;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianServiceImpl;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.entity.VisitEntity;
import com.mokah.veterinary.features.visits.mapper.VisitMapper;
import com.mokah.veterinary.features.visits.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final VeterinarianServiceImpl veterinarianService;
    private final AppointmentServiceImpl appointmentService;
    private final PetService petService;


    @Override
    public VisitResponse create(VisitRequest request){
        VisitEntity entity = visitMapper.toEntity(request);
        entity.setVeterinarian(veterinarianService.entityById(request.veterinarianId()));
        entity.setAppointment(appointmentService.);
    }
}
