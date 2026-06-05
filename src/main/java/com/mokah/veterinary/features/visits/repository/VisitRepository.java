package com.mokah.veterinary.features.visits.repository;

import com.mokah.veterinary.features.visits.entity.VisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VisitRepository extends JpaRepository<VisitEntity, Long>, JpaSpecificationExecutor<VisitEntity> {

}
