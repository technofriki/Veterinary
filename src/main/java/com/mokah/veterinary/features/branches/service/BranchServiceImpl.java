package com.mokah.veterinary.features.branches.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.entity.Branch;
import com.mokah.veterinary.features.branches.mapper.BranchMapper;
import com.mokah.veterinary.features.branches.repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchMapper branchMapper;
    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchMapper branchMapper, BranchRepository branchRepository) {
        this.branchMapper = branchMapper;
        this.branchRepository = branchRepository;
    }

    @Override
    public BranchResponse create(BranchRequest request) {

        if (branchRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Branch with name " + request.getName() + " already exists");
        }
        Branch branch = branchMapper.toEntity(request);
        Branch createdBranch = branchRepository.save(branch);

        return branchMapper.toResponse(createdBranch);
    }

    @Override
    public List<BranchResponse> findAll() {
        return branchMapper.toResponseList(branchRepository.findAll());
    }

    @Override
    public BranchResponse findById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));

        return branchMapper.toResponse(branch);
    }

    @Override
    public BranchResponse update(Long id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));

        if (!branch.getName().equals(request.getName())
                && branchRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Branch with name " + request.getName() + " already exists");
        }

        branchMapper.updateEntity(branch, request);

        Branch updatedBranch = branchRepository.save(branch);

        return branchMapper.toResponse(updatedBranch);
    }

    @Override
    public void delete(Long id) {
      if (!branchRepository.existsById(id)){
          throw new ResourceNotFoundException("Branch", id);
      }

      branchRepository.deleteById(id);
    }
}
