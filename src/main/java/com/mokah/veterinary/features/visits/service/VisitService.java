package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.model.Visit;

import java.util.List;

public interface VisitService {


    VisitResponse create(VisitRequest visitRequest);

    List<VisitResponse> findAll(Long visitId, String veterinarianName, String petName);
    VisitResponse update(Long id, VisitRequest visitRequest);
    VisitResponse findById(Long id);
    void delete(Long id);
    Visit entityById(Long id);
}
