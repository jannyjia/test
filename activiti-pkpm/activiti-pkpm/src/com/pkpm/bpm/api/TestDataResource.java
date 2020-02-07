package com.pkpm.bpm.api;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.pkpm.bpm.identity.User;
import com.pkpm.bpm.model.BpmDefinition;
import com.pkpm.bpm.model.TestData;
import com.pkpm.bpm.respository.TestDataRepository;
import com.pkpm.bpm.service.BpmDefinitionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = { "TEST" }, description = "TEST", authorizations = { @Authorization(value = "basicAuth") })
public class TestDataResource {
	private Logger logger = LoggerFactory.getLogger(TestDataResource.class);
	@Autowired
	private TestDataRepository testDataRepository;
	/**
	     *     查询
	* @param allRequestParams
	* @param request
	* @return
	*/
	@RequestMapping(value = "/testdata/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public String getList(@ApiParam(hidden = true) @RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {
		List<TestData> mlist0 = testDataRepository.findAll();
		JSONObject data = new JSONObject();
		JSONObject page = new JSONObject();
		page.put("total", mlist0.size());
		page.put("pageSize", 15);
		page.put("currentPage", 1);
		page.put("totalResult", mlist0.size());
		data.put("page", page);
		data.put("result", mlist0);
		return data.toString();
	}
	
	/**
	 *    保存
	* @param bpmDefinition
	* @param request
	* @param response
	*/
	@RequestMapping(value = "/testdata/save", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public String save(@RequestBody TestData testData, 
		  HttpServletRequest request, HttpServletResponse response) {
	try {
			testData.setId(0);
			testDataRepository.save(testData);
			return new BpmResponse(1, JSONObject.toJSONString(testData)).toString();
	
	} catch (Exception e) {
		logger.error("保存失败：", e);
		return new BpmResponse(0, e.getMessage()).toString();
	}
	}
	/**
	* 删除
	* @param ids
	* @param request
	* @param response
	* @return
	*/
	@RequestMapping(value = "/testData/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public String delete(@RequestBody String ids, 
		  HttpServletRequest request, HttpServletResponse response) {
	try {
		String[] idArray = ids.split(",");
		for(String id : idArray) {
			testDataRepository.delete(Integer.parseInt(id));
			
		}
		return new BpmResponse(1, "删除成功！").toString();
	
	} catch (Exception e) {
		logger.error("删除模型失败：", e);
		return new BpmResponse(0, e.getMessage()).toString();
	}
	}  
	
	/**
	* 获取模型
	* @param id
	* @param request
	* @param response
	* @return
	*/
	@RequestMapping(value = "/testdata/getById/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public String get(@PathVariable(value = "id") String id, HttpServletRequest request, HttpServletResponse response) {
	try {
		TestData testData = testDataRepository.findOne(Integer.parseInt(id));		
		return new BpmResponse(1, JSONObject.toJSONString(testData)).toString();
	} catch (Exception e) {
		logger.error("获取模型失败：", e);
		return new BpmResponse(0, e.getMessage()).toString();
	}
	} 
}
