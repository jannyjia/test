package com.pkpm.bpm.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkpm.bpm.model.TestData;

public interface TestDataRepository extends JpaRepository<TestData, Integer>{

}
