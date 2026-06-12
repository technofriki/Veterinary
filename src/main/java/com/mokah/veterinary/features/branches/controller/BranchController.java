package com.mokah.veterinary.features.branches.controller;

import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MANAGE_BRANCHES')")
    public BranchResponse create(@Valid @RequestBody BranchRequest dto) {
        return branchService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_BRANCHES')")
    public List<BranchResponse> findAll() {
        return branchService.findAll();
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAuthority('VIEW_BRANCHES')")
    public BranchResponse findById(@PathVariable UUID externalId) {
        return branchService.findById(externalId);
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('MANAGE_BRANCHES')")
    public BranchResponse update(
            @PathVariable UUID externalId,
            @Valid @RequestBody BranchRequest dto) {

        return branchService.update(externalId, dto);
    }

    @DeleteMapping("/{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MANAGE_BRANCHES')")
    public void delete(@PathVariable UUID externalId) {
        branchService.delete(externalId);
    }
}
