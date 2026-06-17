package com.mokah.veterinary.features.visits.service;

import com.mokah.veterinary.common.exception.AppointmentNotConfirmedException;
import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.service.AppointmentService;
import com.mokah.veterinary.features.diagnosis.dto.DiagnosisResponse;
import com.mokah.veterinary.features.diagnosis.mapper.DiagnosisMapper;
import com.mokah.veterinary.features.diagnosis.repository.DiagnosisRepository;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.mapper.PrescriptionMapper;
import com.mokah.veterinary.features.prescriptions.repository.PrescriptionRepository;
import com.mokah.veterinary.features.studies.dto.StudyResponse;
import com.mokah.veterinary.features.studies.mapper.StudyMapper;
import com.mokah.veterinary.features.studies.model.Study;
import com.mokah.veterinary.features.studiesbyvisit.model.StudyByVisit;
import com.mokah.veterinary.features.studiesbyvisit.repository.StudyByVisitRepository;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.features.visits.dto.VisitRequest;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.dto.VisitUpdateDTO;
import com.mokah.veterinary.features.visits.dto.WalkInVisitRequest;
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
    private final PetService petService;
    private final DiagnosisRepository diagnosisRepository;
    private final StudyByVisitRepository studyByVisitRepository;
    private final PrescriptionRepository prescriptionRepository;

    private final DiagnosisMapper diagnosisMapper;
    private final StudyMapper studyMapper;
    private final PrescriptionMapper prescriptionMapper;

    @Transactional
    @Override
    public VisitResponse create(VisitRequest dto) {

        if (repository.existsByAppointment_ExternalId(dto.appointmentExternalId())) {
            throw new BusinessRuleException(
                    "The appointment already has a registered visit."
            );
        }

        Appointment appointment =
                appointmentService.entityByExternalId(dto.appointmentExternalId());

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new AppointmentNotConfirmedException(
                    "The visit can not be created. Appointment must be confirmed. Status: "
                            + appointment.getStatus()
            );
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        Visit entity = mapper.toEntity(dto);

        entity.setVeterinarian(
                veterinarianService.entityByExternalId(dto.veterinarianExternalId())
        );

        entity.setAppointment(appointment);

        entity.setPet(appointment.getPet());

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @Override
    public VisitResponse walkInCreate(WalkInVisitRequest dto) {

        Visit entity = mapper.toEntity(dto);

        entity.setPet(
                petService.entityByExternalId(dto.petExternalId())
        );

        entity.setVeterinarian(
                veterinarianService.entityByExternalId(dto.veterinarianExternalId())
        );

        entity.setAppointment(null);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public Visit entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit", "externalId", externalId)
                );
    }

    @Override
    public VisitResponse findById(UUID externalId) {
        return mapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public List<VisitResponse> findAll(
            UUID visitExternalId,
            String veterinarianName,
            String petName,
            Boolean walkIn) {

        PredicateSpecification<Visit> spec = PredicateSpecification.allOf(
                VisitSpecification.hasExternalId(visitExternalId),
                VisitSpecification.hasVeterinarianName(veterinarianName),
                VisitSpecification.hasPetName(petName),
                VisitSpecification.isWalkIn(walkIn)
        );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    @Transactional
    public VisitResponse update(UUID externalId, VisitUpdateDTO dto) {

        Visit entity = entityByExternalId(externalId);

        entity.setObservations(dto.observations());

        entity.setVeterinarian(
                veterinarianService.entityByExternalId(dto.veterinarianExternalId())
        );

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<VisitResponse> findMedicalHistory(UUID petExternalId) {

        List<Visit> visits = repository.findAll(
                (root, query, cb) ->
                        cb.equal(root.get("pet").get("externalId"), petExternalId)
        );

        return mapper.toResponseList(visits);
    }

    @Override
    public List<DiagnosisResponse> findDiagnosesByVisit(UUID visitExternalId) {

        entityByExternalId(visitExternalId);

        return diagnosisMapper.toResponseList(
                diagnosisRepository.findByVisit_ExternalId(visitExternalId)
        );
    }

    @Override
    public List<PrescriptionResponse> findPrescriptionsByVisit(UUID visitExternalId) {

        entityByExternalId(visitExternalId);

        return prescriptionMapper.toResponseList(
                prescriptionRepository.findByDiagnosis_Visit_ExternalId(visitExternalId)
        );
    }

    @Override
    public List<StudyResponse> findStudiesByVisit(UUID visitExternalId) {

        entityByExternalId(visitExternalId);

        List<Study> studies =
                studyByVisitRepository.findByVisit_ExternalId(visitExternalId)
                        .stream()
                        .map(StudyByVisit::getStudy)
                        .toList();

        return studyMapper.toResponseList(studies);
    }


}
