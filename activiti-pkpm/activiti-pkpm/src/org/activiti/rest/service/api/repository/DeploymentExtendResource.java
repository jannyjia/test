package org.activiti.rest.service.api.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.app.domain.editor.AbstractModel;
import org.activiti.app.domain.editor.AppDefinition;
import org.activiti.app.domain.editor.AppModelDefinition;
import org.activiti.app.domain.editor.Model;
import org.activiti.app.model.editor.ModelKeyRepresentation;
import org.activiti.app.model.editor.ModelRepresentation;
import org.activiti.app.repository.editor.ModelRepository;
import org.activiti.app.model.editor.AppDefinitionPublishRepresentation;
import org.activiti.app.model.editor.AppDefinitionRepresentation;
import org.activiti.app.model.editor.AppDefinitionSaveRepresentation;
import org.activiti.app.model.editor.AppDefinitionUpdateResultRepresentation;
import org.activiti.app.security.SecurityUtils;
import org.activiti.app.service.api.DeploymentService;
import org.activiti.app.service.api.ModelService;
import org.activiti.app.service.editor.AppDefinitionImportService;
import org.activiti.app.service.editor.AppDefinitionPublishService;
import org.activiti.app.service.exception.BadRequestException;
import org.activiti.app.service.exception.InternalServerErrorException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.rest.service.api.RestResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import antlr.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = { "Deployment" }, description = "Manage Deployment", authorizations = {
		@Authorization(value = "basicAuth") })
public class DeploymentExtendResource {
	private final Logger logger = LoggerFactory.getLogger(DeploymentExtendResource.class);
	@Autowired
	protected RestResponseFactory restResponseFactory;

	@Autowired
	protected RepositoryService repositoryService;
	@Autowired
	protected ModelRepository modelRepository;
	@Inject
	protected ModelService modelService;

	@Inject
	protected ObjectMapper objectMapper;

	@Autowired
	protected AppDefinitionPublishService appDefinitionPublishService;
	@Autowired
	protected AppDefinitionImportService appDefinitionImportService;
	@Autowired
	DeploymentService deploymentService;
	@Autowired
	@Qualifier("userDetailsService")
	UserDetailsService userDetailsService;
	
	@Autowired
	@Qualifier("customPersistentRememberMeServices")
	private RememberMeServices tokenBasedRememberMeServices;
	@ApiOperation(value = "发布新流程", tags = {
			"Deployment" }, produces = "application/json", notes = "传入DEModelID（如：a3b1f1e3-d530-4e51-9906-808354890439），发布名称、Key")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "发布成功") })
	@RequestMapping(value = "/repository/deployment/{DEModelID}/{userid}", method = RequestMethod.POST, produces = "application/json")
	public AppDefinitionUpdateResultRepresentation deploymentProcess(
			@ApiParam(name = "DEModelID") @PathVariable(value = "DEModelID") String DEModelID,
			@ApiParam(name = "userid") @PathVariable(value = "userid") String userid,
			@RequestBody DeploymentRequest deployeeRequest,
			HttpServletRequest request, HttpServletResponse response) {
		 if (deployeeRequest == null) {
			 throw new org.activiti.engine.ActivitiException("request body必填");
		 }

		Model includeModel = modelService.getModel(DEModelID);
		if (includeModel == null) {
			throw new org.activiti.engine.ActivitiException("不存在该Model" + includeModel);
		}
		UserDetails user = userDetailsService.loadUserByUsername(userid);
		if (user == null) {
			throw new org.activiti.engine.ActivitiException("不存在该用户" +userid);
		}else {
			org.springframework.security.core.Authentication auth = new UsernamePasswordAuthenticationToken(
					user, user.getPassword(), user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
			tokenBasedRememberMeServices.loginSuccess(request, response, auth); // 使用 remember me
		}
		String key = org.apache.commons.lang3.StringUtils.isEmpty(deployeeRequest.getKey())?includeModel.getKey():deployeeRequest.getKey().replaceAll(" ", "");
		String name = org.apache.commons.lang3.StringUtils.isEmpty(deployeeRequest.getName())?includeModel.getName():deployeeRequest.getName().replaceAll(" ", "");
		ModelRepresentation modelRepresentation = new ModelRepresentation();
		modelRepresentation.setKey(key);
		modelRepresentation.setName(name);
		modelRepresentation.setModelType(AbstractModel.MODEL_TYPE_APP);

		ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(null, modelRepresentation.getModelType(),
				modelRepresentation.getKey());
		Model newModel;
		boolean isCreate = true;
		if (modelKeyInfo.isKeyAlreadyExists()) {
			newModel = modelRepository.findModelsByKeyAndType(key, AbstractModel.MODEL_TYPE_APP).get(0);
			isCreate = false;
		}else {
			String json = null;
			try {
				json = objectMapper.writeValueAsString(new AppDefinition());
			} catch (Exception e) {
				logger.error("Error creating app definition", e);
				throw new InternalServerErrorException("Error creating app definition");
			}
			newModel = modelService.createModel(modelRepresentation, json, SecurityUtils.getCurrentUserObject());
		}
		
		List<AppModelDefinition> includeModels = new ArrayList<AppModelDefinition>();
		AppModelDefinition item = new AppModelDefinition();
		item.setCreatedBy(includeModel.getCreatedBy());
		item.setDescription(includeModel.getDescription());
		item.setId(includeModel.getId());
		item.setLastUpdated(includeModel.getLastUpdated());
		item.setLastUpdatedBy(includeModel.getLastUpdatedBy());
		item.setModelType(includeModel.getModelType());
		item.setName(includeModel.getName());
		item.setVersion(includeModel.getVersion());;
		includeModels.add(item);
		AppDefinition appDefinition = new AppDefinition();
		appDefinition.setIcon("glyphicon-asterisk");
		appDefinition.setTheme("theme-1");
		appDefinition.setModels(includeModels);
		String editorJson = null;
		try {
			editorJson = objectMapper.writeValueAsString(appDefinition);
			//newModel.setModelEditorJson(editorJson);
		} catch (Exception e) {
			throw new InternalServerErrorException("App definition could not be published " + DEModelID);
		}
		newModel = modelService.saveModel(newModel, editorJson, null, false, null, SecurityUtils.getCurrentUserObject());
		// 发布新版本
		if (!isCreate) {
		      return appDefinitionImportService.publishAppDefinition(newModel.getId(), new AppDefinitionPublishRepresentation(null, null));
		
	    } else {//覆盖新版本
	    	Deployment deployment = deploymentService.updateAppDefinition(newModel, SecurityUtils.getCurrentUserObject());
	    	AppDefinitionRepresentation appDefinitionRepresentation = new AppDefinitionRepresentation(newModel);
	    	appDefinitionRepresentation.setDefinition(appDefinition);
	        AppDefinitionUpdateResultRepresentation result = new AppDefinitionUpdateResultRepresentation();
	        result.setAppDefinition(appDefinitionRepresentation);
	        return result;
	    }
		
		//return appDefinitionPublishService.publishAppDefinition("publish new version", newModel, SecurityUtils.getCurrentUserObject());
		//Deployment deployment = deploymentService.updateAppDefinition(newModel, SecurityUtils.getCurrentUserObject());
		//return deployment;
	}
}
