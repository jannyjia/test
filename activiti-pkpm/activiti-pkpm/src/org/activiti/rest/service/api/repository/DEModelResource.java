package org.activiti.rest.service.api.repository;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.app.domain.editor.AbstractModel;
import org.activiti.app.service.api.ModelService;
import org.activiti.engine.repository.ModelQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.activiti.app.security.SecurityUtils;
/**
 * DE_Model的操作
 * @author wangjia
 *
 */
@RestController
@Api(tags = { "Models" }, description = "Manage DEModels", authorizations = { @Authorization(value = "basicAuth") })
public class DEModelResource extends BaseModelResource{
	  @Autowired
	  protected ModelService modelService;  
	
	@RequestMapping(value = "/repository/demodels", method = RequestMethod.GET, produces = "application/json")
	  public List<AbstractModel> getDEModels(@ApiParam(hidden = true) @RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {
	    ModelQuery modelQuery = repositoryService.createModelQuery();

	    List<AbstractModel> mlist0 = modelService.getModelsByModelType(0);
	    //List<AbstractModel> mlist3 = modelService.getModelsByModelType(3);
	    //mlist0.addAll(mlist3);
	    return mlist0;
	  }
}
