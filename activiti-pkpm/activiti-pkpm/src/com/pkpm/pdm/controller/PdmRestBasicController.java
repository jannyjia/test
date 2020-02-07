package com.pkpm.pdm.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.app.domain.editor.AbstractModel;
import org.activiti.app.domain.editor.Model;
import org.activiti.app.domain.editor.ModelRelation;
import org.activiti.app.domain.editor.ModelRelationTypes;
import org.activiti.app.model.editor.ModelKeyRepresentation;
import org.activiti.app.model.editor.ModelRepresentation;
import org.activiti.app.repository.editor.ModelRelationRepository;
import org.activiti.app.repository.editor.ModelRepository;
import org.activiti.app.security.SecurityUtils;
import org.activiti.app.service.api.ModelService;
import org.activiti.app.service.editor.ModelImageService;
import org.activiti.app.service.exception.BadRequestException;
import org.activiti.app.service.exception.InternalServerErrorException;
import org.activiti.editor.language.json.converter.util.JsonConverterUtil;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/***
 * 主要定义登录、页面在第三方系统中显示及跳转
 * @author wangjia
 *
 */
@RestController
@RequestMapping("/rest/")
public class PdmRestBasicController {
	private Logger logger = LoggerFactory.getLogger(PdmRestBasicController.class);

	@Autowired
	protected ModelImageService modelImageService;

	@Autowired
	protected ModelRepository modelRepository;
	
	@Autowired
	protected ModelRelationRepository modelRelationRepository;
	
	@Autowired
	@Qualifier("customPersistentRememberMeServices")
	private RememberMeServices tokenBasedRememberMeServices;
	
	@Inject
	protected ModelService modelService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	@Qualifier("userDetailsService")
	UserDetailsService userDetailsService;

	/**
	 * 自动登录
	 * @param username
	 * @param password
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "autologin/{username}/{password}")
	public void autologin(@PathVariable(value = "username") String username,@PathVariable(value = "password") String password, HttpServletRequest request,
			HttpServletResponse response) throws IOException  {
		try {
			UserDetails userFromDatabase = userDetailsService.loadUserByUsername(username);
			org.springframework.security.core.Authentication auth = new UsernamePasswordAuthenticationToken(
					userFromDatabase, userFromDatabase.getPassword(), userFromDatabase.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
			tokenBasedRememberMeServices.loginSuccess(request, response, auth); // 使用 remember me
			response.getWriter().write("{status:1,success:true}");
		}catch(Exception ex) {
			response.getWriter().write("{status:0,success:false}");
		}
	}
	
	/**
	 * 设计器
	 * @param modelkey
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "designer/{modelkey}")
	public void designer(@PathVariable(value = "modelkey") String modelkey, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		List<Model> newModel = modelRepository.findModelsByKeyAndType(modelkey, 0);
		response.sendRedirect(request.getContextPath() + "/editor/index.html#/editor/" + newModel.get(0).getId());
	}
	
	@RequestMapping(value = "create")
	public void create(@RequestParam("name") String name, @RequestParam("key") String key,
			@RequestParam("description") String description, HttpServletRequest request, HttpServletResponse response) {

		try {
			ModelRepresentation modelRepresentation = new ModelRepresentation();
			modelRepresentation.setKey(key);
			modelRepresentation.setName(name);
			modelRepresentation.setDescription(description);
			modelRepresentation.setModelType(0);
			ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(null, 0, key);
			if (modelKeyInfo.isKeyAlreadyExists()) {
				throw new BadRequestException("Provided model key already exists: " + key);
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
			Model newModel = modelService.createModel(modelRepresentation, json, SecurityUtils.getCurrentUserObject());
			response.sendRedirect(request.getContextPath() + "/editor/index.html#/editor/" + newModel.getId());

		} catch (Exception e) {

			logger.error("创建模型失败：", e);

		}
	}
	

	protected Model persistModel(Model model) {

		model = modelRepository.save((Model) model);

		if (StringUtils.isNotEmpty(model.getModelEditorJson())) {

			// Parse json to java
			ObjectNode jsonNode = null;
			try {
				jsonNode = (ObjectNode) objectMapper.readTree(model.getModelEditorJson());
			} catch (Exception e) {
				logger.error("Could not deserialize json model", e);
				throw new InternalServerErrorException("Could not deserialize json model");
			}

			if ((model.getModelType() == null || model.getModelType().intValue() == Model.MODEL_TYPE_BPMN)) {

				// Thumbnail
				modelImageService.generateThumbnailImage(model, jsonNode);

				// Relations
				handleBpmnProcessFormModelRelations(model, jsonNode);
				handleBpmnProcessDecisionTaskModelRelations(model, jsonNode);

			} else if (model.getModelType().intValue() == Model.MODEL_TYPE_FORM
					|| model.getModelType().intValue() == Model.MODEL_TYPE_DECISION_TABLE) {

				jsonNode.put("name", model.getName());
				jsonNode.put("key", model.getKey());

			} else if (model.getModelType().intValue() == Model.MODEL_TYPE_APP) {

				handleAppModelProcessRelations(model, jsonNode);
			}
		}

		return model;
	}

	protected void handleBpmnProcessFormModelRelations(AbstractModel bpmnProcessModel, ObjectNode editorJsonNode) {
		List<JsonNode> formReferenceNodes = JsonConverterUtil
				.filterOutJsonNodes(JsonConverterUtil.getBpmnProcessModelFormReferences(editorJsonNode));
		Set<String> formIds = JsonConverterUtil.gatherStringPropertyFromJsonNodes(formReferenceNodes, "id");

		handleModelRelations(bpmnProcessModel, formIds, ModelRelationTypes.TYPE_FORM_MODEL_CHILD);
	}

	protected void handleBpmnProcessDecisionTaskModelRelations(AbstractModel bpmnProcessModel,
			ObjectNode editorJsonNode) {
		List<JsonNode> decisionTableNodes = JsonConverterUtil
				.filterOutJsonNodes(JsonConverterUtil.getBpmnProcessModelDecisionTableReferences(editorJsonNode));
		Set<String> decisionTableIds = JsonConverterUtil.gatherStringPropertyFromJsonNodes(decisionTableNodes, "id");

		handleModelRelations(bpmnProcessModel, decisionTableIds, ModelRelationTypes.TYPE_DECISION_TABLE_MODEL_CHILD);
	}

	protected void handleAppModelProcessRelations(AbstractModel appModel, ObjectNode appModelJsonNode) {
		Set<String> processModelIds = JsonConverterUtil.getAppModelReferencedModelIds(appModelJsonNode);
		handleModelRelations(appModel, processModelIds, ModelRelationTypes.TYPE_PROCESS_MODEL);
	}

	/**
	 * Generic handling of model relations: deleting/adding where needed.
	 */
	protected void handleModelRelations(AbstractModel bpmnProcessModel, Set<String> idsReferencedInJson,
			String relationshipType) {

		// Find existing persisted relations
		List<ModelRelation> persistedModelRelations = modelRelationRepository
				.findByParentModelIdAndType(bpmnProcessModel.getId(), relationshipType);

		// if no ids referenced now, just delete them all
		if (idsReferencedInJson == null || idsReferencedInJson.size() == 0) {
			modelRelationRepository.delete(persistedModelRelations);
			return;
		}

		Set<String> alreadyPersistedModelIds = new HashSet<String>(persistedModelRelations.size());
		for (ModelRelation persistedModelRelation : persistedModelRelations) {
			if (!idsReferencedInJson.contains(persistedModelRelation.getModelId())) {
				// model used to be referenced, but not anymore. Delete it.
				modelRelationRepository.delete((ModelRelation) persistedModelRelation);
			} else {
				alreadyPersistedModelIds.add(persistedModelRelation.getModelId());
			}
		}

		// Loop over all referenced ids and see which one are new
		for (String idReferencedInJson : idsReferencedInJson) {

			// if model is referenced, but it is not yet persisted = create it
			if (!alreadyPersistedModelIds.contains(idReferencedInJson)) {

				// Check if model actually still exists. Don't create the relationship if it
				// doesn't exist. The client UI will have cope with this too.
				if (modelRepository.exists(idReferencedInJson)) {
					modelRelationRepository
							.save(new ModelRelation(bpmnProcessModel.getId(), idReferencedInJson, relationshipType));
				}
			}
		}
	}

}
