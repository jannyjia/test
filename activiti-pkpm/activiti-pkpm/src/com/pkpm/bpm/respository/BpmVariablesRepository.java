package com.pkpm.bpm.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.pkpm.bpm.model.BpmNode;
import com.pkpm.bpm.model.BpmVariables;

public interface BpmVariablesRepository extends JpaRepository<BpmVariables, Integer> {
	@Modifying(clearAutomatically = true)
	@Transactional
	void deleteByNodeId(int nodeId);
	
	List<BpmVariables> findByNodeId(int nodeId);
}
