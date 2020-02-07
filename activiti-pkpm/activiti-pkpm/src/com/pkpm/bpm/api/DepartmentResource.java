package com.pkpm.bpm.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pkpm.bpm.identity.Department;
import com.pkpm.bpm.service.BpmDefinitionService;
import com.pkpm.bpm.service.BpmIdentityDepartmentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = { "Department" }, description = "Department", authorizations = { @Authorization(value = "basicAuth") })
public class DepartmentResource {
	private Logger logger = LoggerFactory.getLogger(DepartmentResource.class);
	  
	  @Autowired
	  BpmIdentityDepartmentService bpmIdentityDepartmentService;
	  
	  @RequestMapping(value = "/department/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	  public List<Department> getDepList(@ApiParam(hidden = true) @RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {
	    
	    List<Department> mlist0 = bpmIdentityDepartmentService.getList("selectList");
	    return mlist0;
	  }
}
