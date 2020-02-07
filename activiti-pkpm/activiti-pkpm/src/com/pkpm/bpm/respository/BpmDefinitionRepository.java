package com.pkpm.bpm.respository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pkpm.bpm.model.BpmDefinition;

public interface BpmDefinitionRepository extends JpaRepository<BpmDefinition, Integer> {

	Page<BpmDefinition> findAll(Specification<BpmDefinition> spec, Pageable pageable);
}
