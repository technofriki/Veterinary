package com.mokah.veterinary.features.studies.service;

import com.mokah.veterinary.features.studies.dto.StudyRequest;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.mapper.StudyMapper;
import com.mokah.veterinary.features.studies.repository.StudyRepository;

import java.util.List;

public class StudyServiceImpl implements StudyService{

    private final StudyRepository studyRepository;
    private final StudyMapper studyMapper;

    public StudyServiceImpl(StudyRepository studyRepository, StudyMapper studyMapper) {
        this.studyRepository = studyRepository;
        this.studyMapper = studyMapper;
    }

    @Override
    public StudyResponse create(StudyRequest request) {
        return null;
    }

    @Override
    public List<StudyResponse> findAll() {
        return List.of();
    }

    @Override
    public StudyResponse findById(Long id) {
        return null;
    }

    @Override
    public StudyResponse update(Long id, StudyRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
