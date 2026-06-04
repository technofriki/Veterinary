package com.mokah.veterinary.features.branches.service;

import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.adresses.entity.Address;
import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.entity.Branch;
import com.mokah.veterinary.features.branches.mapper.BranchMapper;
import com.mokah.veterinary.features.branches.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchMapper branchMapper;
    private final BranchRepository branchRepository;
    private final AddressMapper addressMapper;

    @Override
    public BranchResponse create(BranchRequest request) {

        if (branchRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Branch already exists");
        }

        Branch branch = branchMapper.toEntity(request);

        Branch saved = branchRepository.save(branch);

        return branchMapper.toResponse(saved);
    }

    @Override
    public List<BranchResponse> findAll() {
        return branchMapper.toResponseList(branchRepository.findAll());
    }

    @Override
    public Branch entityById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));
    }

    @Override
    public BranchResponse findById(Long id) {
        return branchMapper.toResponse(entityById(id));
    }

    @Override
    public BranchResponse update(Long id, BranchRequest request) {

        Branch branch = entityById(id);

        if (!branch.getName().equals(request.name())
                && branchRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Branch already exists");
        }

        // 🔹 update de campos simples
        branch.setName(request.name());
        branch.setPhone(request.phone());
        branch.setEmail(request.email());

        // 🔹 control explícito del agregado Address
        if (branch.getAddress() == null) {
            branch.setAddress(addressMapper.toEntity(request.address()));
        } else {
            addressMapper.update(branch.getAddress(), request.address());
        }

        return branchMapper.toResponse(branchRepository.save(branch));
    }

    @Override
    public void delete(Long id) {
        Branch entity = entityById(id);
        branchRepository.delete(entity);
    }
}
