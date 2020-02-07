package com.pkpm.bpm.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.pkpm.bpm.model.BpmDefinition;
import com.pkpm.bpm.model.BpmNode;

public interface BpmNodeRepository extends JpaRepository<BpmNode, Integer> {

	List<BpmNode> findByNodeKey(String nodekey);
	
	List<BpmNode> findByDefinitionId(int definitionId);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	void deleteByDefinitionId(int definitionId);
	//List<BpmNode> findByBpmDefinition(BpmDefinition bpmDefinition);
	
	//@Query("select DISTINCT x from BpmNode x left join x.bpmDefinition as p  where p.id = ?1")//正确
   //// public List<BpmNode> findBpmNodeByBpmDefinitionId(Integer id);
}
