package com.mokah.veterinary.features.branches.service;


import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;

import java.util.List;

public interface BranchService {
   BranchResponse create(BranchRequest request);
   List<BranchResponse> findAll();
   BranchResponse findById(Long id);
   BranchResponse update(Long id, BranchRequest request);
   void delete(Long id);
}
