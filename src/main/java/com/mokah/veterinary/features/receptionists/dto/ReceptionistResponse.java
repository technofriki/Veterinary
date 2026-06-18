package com.mokah.veterinary.features.receptionists.dto;

import com.mokah.veterinary.features.branches.dto.BranchResponse;

import java.util.UUID;

public record ReceptionistResponse(
        UUID externalId,
        String firstName,
        String lastName,
        String phone,
        String email,
        Boolean active,
        BranchResponse branch
) {
}
