package com.mokah.veterinary.features.branches.controller;

import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BranchResponse create(@Valid @RequestBody BranchRequest request) {
        return branchService.create(request);
    }

    @GetMapping
    public List<BranchResponse> findAll() {
        return branchService.findAll();
    }

    @GetMapping("/{id}")
    public BranchResponse findById(@PathVariable Long id) {
        return branchService.findById(id);
    }

    @PutMapping("/{id}")
    public BranchResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequest request) {
        return branchService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        branchService.delete(id);
    }
}
