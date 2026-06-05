package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;

import java.util.List;

public interface VisitServiceInterface {


    VisitResponse create(VisitRequest visitRequest);

    List<Appointment> findAll(Long visitId, String veterinarianName, String petName);
    VisitResponse update(Long id, VisitRequest visitRequest);
    VisitResponse findById(Long id);
    void delete(Long id);
}
