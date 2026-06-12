package com.mokah.veterinary.features.branches.service;

import com.mokah.veterinary.common.exception.BranchNameExistsException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.branches.mapper.BranchMapper;
import com.mokah.veterinary.features.branches.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchMapper branchMapper;
    private final BranchRepository branchRepository;
    private final AddressMapper addressMapper;

    @Override
    public BranchResponse create(BranchRequest dto) {

        if (branchRepository.existsByName(dto.name())) {
            throw new BranchNameExistsException("Branch already exists");
        }

        Branch branch = branchMapper.toEntity(dto);

        return branchMapper.toResponse(branchRepository.save(branch));
    }

    @Override
    public List<BranchResponse> findAll() {
        return branchMapper.toResponseList(branchRepository.findAll());
    }

    @Override
    public Branch entityByExternalId(UUID externalId) {
        return branchRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "externalId", externalId));
    }

    @Override
    public BranchResponse findById(UUID externalId) {
        return branchMapper.toResponse(entityByExternalId(externalId));
    }

    @Override
    public BranchResponse update(UUID externalId, BranchRequest dto) {

        Branch branch = entityByExternalId(externalId);

        if (!branch.getName().equals(dto.name())
                && branchRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Branch already exists");
        }

        branch.setName(dto.name());
        branch.setPhone(dto.phone());
        branch.setEmail(dto.email());

        if (dto.address() != null) {

            if (branch.getAddress() == null) {
                branch.setAddress(addressMapper.toEntity(dto.address()));
            } else {
                addressMapper.update(branch.getAddress(), dto.address());
            }
        }

        return branchMapper.toResponse(branchRepository.save(branch));
    }

    @Override
    public void delete(UUID externalId) {
        branchRepository.delete(entityByExternalId(externalId));
    }
}