package com.pkpm.bpm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.app.domain.editor.AbstractModel;
import org.activiti.app.domain.editor.AppDefinition;
import org.activiti.app.domain.editor.AppModelDefinition;
import org.activiti.app.domain.editor.Model;
import org.activiti.app.model.editor.AppDefinitionPublishRepresentation;
import org.activiti.app.model.editor.ModelKeyRepresentation;
import org.activiti.app.model.editor.ModelRepresentation;
import org.activiti.app.repository.editor.ModelRepository;
import org.activiti.app.security.SecurityUtils;
import org.activiti.app.service.api.DeploymentService;
import org.activiti.app.service.api.ModelService;
import org.activiti.app.service.editor.AppDefinitionImportService;
import org.activiti.app.service.exception.BadRequestException;
import org.activiti.app.service.exception.InternalServerErrorException;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pkpm.bpm.model.BpmDefinition;
import com.pkpm.bpm.model.BpmNode;
import com.pkpm.bpm.respository.BpmDefinitionRepository;
import com.pkpm.bpm.respository.BpmMethodsRepository;
import com.pkpm.bpm.respository.BpmNodeRepository;
import com.pkpm.bpm.respository.BpmVariablesRepository;
import com.pkpm.pdm.controller.PdmRestBasicController;

@Service
public class BpmDefinitionService {
	private Logger logger = LoggerFactory.getLogger(BpmDefinitionService.class);
	@Inject
	protected ModelService modelService;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	protected BpmDefinitionRepository bpmDefinitionRepository;

	@Autowired
	protected BpmMethodsRepository bpmMethodsRepository;

	@Autowired
	protected BpmNodeRepository bpmNodeRepository;

	@Autowired
	protected BpmVariablesRepository bpmVariablesRepository;

	@Autowired
	DeploymentService deploymentService;

	@Autowired
	protected ModelRepository modelRepository;

	@Autowired
	protected AppDefinitionImportService appDefinitionImportService;
	@Autowired
	@Qualifier("userDetailsService")
	UserDetailsService userDetailsService;

	/**
	 * 新建流程模型
	 * 
	 * @param name
	 * @param key
	 * @param description
	 * @return 模型GUID
	 */
	public String create(String name, String key, String description, User creator) {

		try {
			ModelRepresentation modelRepresentation = new ModelRepresentation();
			modelRepresentation.setKey(key);
			modelRepresentation.setName(name);
			modelRepresentation.setDescription(description);
			modelRepresentation.setModelType(0);
			ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(null, 0, key);
			if (modelKeyInfo.isKeyAlreadyExists()) {
				throw new BadRequestException("流程Key已存在: " + key);
			}

			String json = null;
			ObjectNode editorNode = objectMapper.createObjectNode();
			editorNode.put("id", "canvas");
			editorNode.put("resourceId", "canvas");
			ObjectNode stencilSetNode = objectMapper.createObjectNode();
			stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			editorNode.put("stencilset", stencilSetNode);
			ObjectNode propertiesNode = objectMapper.createObjectNode();
			propertiesNode.put("process_id", key);
			propertiesNode.put("name", name);
			if (StringUtils.isNotEmpty(description)) {
				propertiesNode.put("documentation", description);
			}
			editorNode.put("properties", propertiesNode);

			ArrayNode childShapeArray = objectMapper.createArrayNode();
			editorNode.put("childShapes", childShapeArray);
			ObjectNode childNode = objectMapper.createObjectNode();
			childShapeArray.add(childNode);
			ObjectNode boundsNode = objectMapper.createObjectNode();
			childNode.put("bounds", boundsNode);
			ObjectNode lowerRightNode = objectMapper.createObjectNode();
			boundsNode.put("lowerRight", lowerRightNode);
			lowerRightNode.put("x", 130);
			lowerRightNode.put("y", 193);
			ObjectNode upperLeftNode = objectMapper.createObjectNode();
			boundsNode.put("upperLeft", upperLeftNode);
			upperLeftNode.put("x", 100);
			upperLeftNode.put("y", 163);
			childNode.put("childShapes", objectMapper.createArrayNode());
			childNode.put("dockers", objectMapper.createArrayNode());
			childNode.put("outgoing", objectMapper.createArrayNode());
			childNode.put("resourceId", "startEvent1");
			ObjectNode stencilNode = objectMapper.createObjectNode();
			childNode.put("stencil", stencilNode);
			stencilNode.put("id", "StartNoneEvent");
			json = editorNode.toString();
			Model newModel = modelService.createModel(modelRepresentation, json, creator);
			return newModel.getId();

		} catch (Exception e) {
			logger.error("创建模型失败：", e);
			throw e;
		}
	}

	public void publish(BpmDefinition bpmDefinition) {
		Model includeModel = modelService.getModel(bpmDefinition.getDeModelId());
		if (includeModel == null) {
			throw new org.activiti.engine.ActivitiException("不存在该Model" + includeModel);
		}
		String key = org.apache.commons.lang3.StringUtils.isEmpty(bpmDefinition.getDefKey()) ? includeModel.getKey()
				: bpmDefinition.getDefKey().replaceAll(" ", "");
		String name = org.apache.commons.lang3.StringUtils.isEmpty(bpmDefinition.getDefName()) ? includeModel.getName()
				: bpmDefinition.getDefName().replaceAll(" ", "");
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
		} else {
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
		item.setVersion(includeModel.getVersion());

		includeModels.add(item);
		AppDefinition appDefinition = new AppDefinition();
		appDefinition.setIcon("glyphicon-asterisk");
		appDefinition.setTheme("theme-1");
		appDefinition.setModels(includeModels);
		String editorJson = null;
		try {
			editorJson = objectMapper.writeValueAsString(appDefinition);
		} catch (Exception e) {
			throw new InternalServerErrorException(
					"App definition could not be published " + bpmDefinition.getDeModelId());
		}
		newModel = modelService.saveModel(newModel, editorJson, null, false, null, SecurityUtils.getCurrentUserObject());
		// 发布新版本
		if (!isCreate) {
			appDefinitionImportService.publishAppDefinition(newModel.getId(),
					new AppDefinitionPublishRepresentation(null, null));

		} else {// 覆盖新版本
			deploymentService.updateAppDefinition(newModel, SecurityUtils.getCurrentUserObject());
		}
	}

	public BpmDefinition getOne(int id) {
		BpmDefinition bpmDefinition = bpmDefinitionRepository.findOne(id);
		List<BpmNode> nexists = bpmNodeRepository.findByDefinitionId(id);
		for (BpmNode existNode : nexists) {
			existNode.setBpmVariables(bpmVariablesRepository.findByNodeId(existNode.getId()));
		}
		bpmDefinition.setBpmNodes(nexists);
		return bpmDefinition;
	}

}
