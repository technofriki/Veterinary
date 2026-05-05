package com.mokah.veterinary.features.animaltypes.repository;

import com.mokah.veterinary.features.animaltypes.entity.AnimalTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalTypeRepository extends JpaRepository<AnimalTypeEntity, Long> {

}
