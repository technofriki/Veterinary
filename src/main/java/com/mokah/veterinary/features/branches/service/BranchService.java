package com.mokah.veterinary.features.branches.service;


import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.model.Branch;

import java.util.List;
import java.util.UUID;

public interface BranchService {

   BranchResponse create(BranchRequest request);

   List<BranchResponse> findAll();

   Branch entityByExternalId(UUID externalId);

   BranchResponse findById(UUID externalId);

   BranchResponse update(UUID externalId, BranchRequest request);

   void delete(UUID externalId);
}
