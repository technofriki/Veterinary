package com.mokah.veterinary.features.branches.mapper;

import com.mokah.veterinary.features.adresses.mapper.AddressMapper;
import com.mokah.veterinary.features.branches.dto.BranchRequest;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface BranchMapper {

    Branch toEntity(BranchRequest request);

    BranchResponse toResponse(Branch entity);

    List<BranchResponse> toResponseList(List<Branch> entities);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Branch entity, BranchRequest request);
}
