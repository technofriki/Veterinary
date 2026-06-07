package com.mokah.veterinary.features.prescriptions.controller;

import com.mokah.veterinary.features.prescriptions.dto.PrescriptionRequest;
import com.mokah.veterinary.features.prescriptions.dto.PrescriptionResponse;
import com.mokah.veterinary.features.prescriptions.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;


    @GetMapping("/{id}")
    public PrescriptionResponse findById(@PathVariable Long id){
        return prescriptionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponse create(@RequestBody PrescriptionRequest request){
        return prescriptionService.create(request);
    }

    @GetMapping
    public List<PrescriptionResponse> findAll(
            @RequestParam(required = false) Long diagnosisId,
            @RequestParam(required = false) Long petId
    ){
    return prescriptionService.findAll(diagnosisId, petId);
    }


}
