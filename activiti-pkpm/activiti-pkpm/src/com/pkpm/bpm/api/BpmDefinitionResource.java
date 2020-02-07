package com.pkpm.bpm.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.app.domain.editor.AbstractModel;
import org.activiti.app.domain.editor.AppDefinition;
import org.activiti.app.domain.editor.AppModelDefinition;
import org.activiti.app.domain.editor.Model;
import org.activiti.app.model.editor.AppDefinitionPublishRepresentation;
import org.activiti.app.model.editor.AppDefinitionRepresentation;
import org.activiti.app.model.editor.AppDefinitionUpdateResultRepresentation;
import org.activiti.app.model.editor.ModelKeyRepresentation;
import org.activiti.app.model.editor.ModelRepresentation;
import org.activiti.app.repository.editor.ModelRepository;
import org.activiti.app.security.SecurityUtils;
import org.activiti.app.service.api.DeploymentService;
import org.activiti.app.service.api.ModelService;
import org.activiti.app.service.editor.AppDefinitionImportService;
import org.activiti.app.service.exception.InternalServerErrorException;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.repository.Deployment;
import org.activiti.rest.service.api.repository.DeploymentRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pkpm.bpm.identity.Department;
import com.pkpm.bpm.identity.User;
import com.pkpm.bpm.model.BpmDefinition;
import com.pkpm.bpm.model.BpmMethods;
import com.pkpm.bpm.model.BpmNode;
import com.pkpm.bpm.model.BpmVariables;
import com.pkpm.bpm.respository.BpmDefinitionRepository;
import com.pkpm.bpm.respository.BpmMethodsRepository;
import com.pkpm.bpm.respository.BpmNodeRepository;
import com.pkpm.bpm.respository.BpmVariablesRepository;
import com.pkpm.bpm.service.BpmDefinitionService;
import com.pkpm.bpm.service.BpmIdentityDepartmentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = { "BPM" }, description = "BPM Definition", authorizations = { @Authorization(value = "basicAuth") })
public class BpmDefinitionResource {
	private Logger logger = LoggerFactory.getLogger(BpmDefinitionService.class);
	@Autowired
	protected BpmDefinitionRepository bpmDefinitionRepository;

	@Autowired
	protected BpmMethodsRepository bpmMethodsRepository;

	@Autowired
	protected BpmNodeRepository bpmNodeRepository;

	@Autowired
	protected BpmVariablesRepository bpmVariablesRepository;

	@Autowired
	protected BpmDefinitionService bpmDefinitionService;

	@Inject
	protected ModelService modelService;



	@Inject
	protected ObjectMapper objectMapper;

	@Autowired
	protected ModelRepository modelRepository;

	@Autowired
	protected AppDefinitionImportService appDefinitionImportService;

	@Autowired
	@Qualifier("userDetailsService")
	UserDetailsService userDetailsService;
	
	@Autowired
	@Qualifier("customPersistentRememberMeServices")
	private RememberMeServices tokenBasedRememberMeServices;
	/**
	 * 查询
	 * 
	 * @param allRequestParams
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/bpmdefinition/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public String getList(@ApiParam(hidden = true) @RequestParam Map<String, String> params,
			HttpServletRequest request) {
		PagerInfo pagerInfo = JSONObject.parseObject(params.get("pagerInfo"), PagerInfo.class);
		Specification<BpmDefinition> specification = new Specification<BpmDefinition>() {
			@Override
			public Predicate toPredicate(Root<BpmDefinition> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>(); // 所有的断言
				if (pagerInfo.getSearch() != null)
					for (String searchKey : pagerInfo.getSearch().keySet()) {
						String searchVal = pagerInfo.getSearch().get(searchKey);
						if (StringUtils.isNotBlank(searchVal)) { // 添加断言
							Predicate search = cb.like(root.get(searchKey).as(String.class), "%" + searchVal + "%");
							predicates.add(search);
						}
					}
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		Pageable pageable = new PageRequest(pagerInfo.getPage().getCurrentPage() - 1,
				pagerInfo.getPage().getPageSize());
		// 分页信息
		Page<BpmDefinition> mlist0 = this.bpmDefinitionRepository.findAll(specification, pageable);
		JSONObject data = new JSONObject();
		JSONObject page = new JSONObject();
		page.put("total", mlist0.getTotalElements());
		page.put("pageSize", mlist0.getSize());
		page.put("currentPage", pagerInfo.getPage().getCurrentPage());
		page.put("totalResult", mlist0.getTotalElements());
		data.put("page", page);
		data.put("result", mlist0.getContent());

		return data.toString();
	}

	/**
	 * 新建流程
	 * 
	 * @param bpmDefinition
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/bpmdefinition/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public String create(@RequestBody BpmDefinition bpmDefinition, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// BpmDefinition bpmDefinition = new BpmDefinition();
			String key = bpmDefinition.getDefKey();
			String name = bpmDefinition.getDefName();
			String description = bpmDefinition.getDescription();
			String userId = bpmDefinition.getCreateBy();
			User creator = new User();// 临时构建一个user
			creator.setId(userId);
			String modelId = bpmDefinitionService.create(name, key, description, creator);
			if (modelId != null) {
				bpmDefinition.setId(0);
				bpmDefinition.setDeModelId(modelId);
				bpmDefinition.setStatus(0);
				bpmDefinition.setCreateDate(Calendar.getInstance().getTime());
				bpmDefinition.setCreateBy(userId);
				bpmDefinition.setVersion(1);
				bpmDefinition.setOtherSettings("{}");
				bpmDefinitionRepository.save(bpmDefinition);
				return new BpmResponse(1, JSONObject.toJSONString(bpmDefinition)).toString();
			}
			throw new Exception("创建失败！");

		} catch (Exception e) {
			logger.error("创建模型失败：", e);
			return new BpmResponse(0, e.getMessage()).toString();
		}
	}

	/**
	 * 保存
	 * 
	 * @param bpmDefinition
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/bpmdefinition/save", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public String save(@RequestBody BpmDefinition bpmDefinition, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (bpmDefinition.getId() != 0) {
				// 1. 保存流程定义
				bpmDefinition.setUpdateDate(Calendar.getInstance().getTime());
				bpmDefinitionRepository.save(bpmDefinition);

				// 2. 更新模型				
				Model model = modelService.getModel(bpmDefinition.getDeModelId());	
				model.setModelEditorJson(bpmDefinition.getBpmnXml());
				model.setKey(bpmDefinition.getDefKey());
				model.setName(bpmDefinition.getDefName());
				modelService.saveModel(model);
				
				// 3. 保存节点，添加监听器
				BpmnModel bpmnModel = modelService.getBpmnModel(model);
				org.activiti.bpmn.model.Process process = bpmnModel.getMainProcess();

				// 新增或 更新节点
				for (FlowElement e : process.getFlowElements()) {
					if (e instanceof org.activiti.bpmn.model.UserTask) {
						BpmNode n = new BpmNode();
						List<BpmNode> ns = bpmNodeRepository.findByNodeKey(e.getId());
						n = ns.size() > 0 ? ns.get(0) : new BpmNode();
						for (BpmNode updateNode : bpmDefinition.getBpmNodes()) {
							if (updateNode.getNodeKey().equals(e.getId())) {
								n.setAssignSettings(updateNode.getAssignSettings());
								n.setFormSettings(updateNode.getFormSettings());
								n.setButtonSettings(updateNode.getButtonSettings());
								// 删掉节点对应的流程变量
								bpmVariablesRepository.deleteByNodeId(n.getId());
								// 保存流程变量
								for (BpmVariables var : updateNode.getBpmVariables()) {
									var.setId(0);
									var.setNodeId(updateNode.getId());
									bpmVariablesRepository.save(var);
								}
							}
						}
						n.setNodeKey(e.getId());
						n.setNodeName(e.getName());
						n.setDefinitionId(bpmDefinition.getId());

						// 保存节点人员
						bpmNodeRepository.save(n);
						
						UserTask userTask = (UserTask) e;
						List<ActivitiListener> activitiListeners = new ArrayList<ActivitiListener>();
						ActivitiListener activitiListener = new ActivitiListener();
						activitiListener.setEvent("create");
						activitiListener.setImplementationType("class");
						activitiListener.setImplementation("com.pkpm.bpm.listener.TaskCreateListener");
						activitiListeners.add(activitiListener);
			            userTask.setTaskListeners(activitiListeners);
			            userTask.setExecutionListeners(new ArrayList<ActivitiListener>());
			            //userTask.getCandidateGroups().add("${taskServiceListener.getGroup()}");
			            userTask.setCandidateUsers(new ArrayList<String>());
			            userTask.setCandidateGroups(new ArrayList<String>());
					}
				}
				// 没有的节点要删除
				List<BpmNode> nexists = bpmNodeRepository.findByDefinitionId(bpmDefinition.getId());
				for (BpmNode existNode : nexists) {
					boolean ifdelete = true;
					for (FlowElement e : process.getFlowElements()) {
						if (e.getId().equals(existNode.getNodeKey())) {
							ifdelete = false;
							break;
						}
					}
					// 删除多余的节点
					if (ifdelete) {
						bpmVariablesRepository.deleteByNodeId(existNode.getId());
						bpmNodeRepository.delete(existNode.getId());
					}
				}
				//更新model
				BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
				BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
				ObjectNode bpmnModelbytes = bpmnJsonConverter.convertToJson(bpmnModel);
				model.setModelEditorJson(bpmnModelbytes.toString());
				modelService.saveModel(model);
				
				// 重新数据库赋值
				bpmDefinition = bpmDefinitionService.getOne(bpmDefinition.getId());
				return new BpmResponse(1, JSONObject.toJSONString(bpmDefinition)).toString();
			}
			throw new Exception("保存失败！");

		} catch (Exception e) {
			logger.error("保存失败：", e);
			return new BpmResponse(0, e.getMessage()).toString();
		}
	}

	/**
	 * 删除
	 * 
	 * @param ids
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/bpmdefinition/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public String delete(@RequestBody String ids, HttpServletRequest request, HttpServletResponse response) {
		try {
			String[] idArray = ids.split(",");
			for (String id : idArray) {
				BpmDefinition bpmDefinition = bpmDefinitionRepository.findOne(Integer.parseInt(id));
				try {
					modelService.deleteModel(bpmDefinition.getDeModelId(), true, true);
				} catch (Exception e) {
				}
				// 删除参数
				List<BpmNode> nexists = bpmNodeRepository.findByDefinitionId(bpmDefinition.getId());
				for (BpmNode existNode : nexists) {
					bpmVariablesRepository.deleteByNodeId(existNode.getId());
				}
				// 删除节点
				bpmNodeRepository.deleteByDefinitionId(bpmDefinition.getId());
				// 删除定义
				bpmDefinitionRepository.delete(Integer.parseInt(id));
			}
			return new BpmResponse(1, "删除成功！").toString();

		} catch (Exception e) {
			logger.error("删除模型失败：", e);
			return new BpmResponse(0, e.getMessage()).toString();
		}
	}

	/**
	 * 获取模型
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/bpmdefinition/getById/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public String get(@PathVariable(value = "id") Integer id, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			BpmDefinition bpmDefinition = bpmDefinitionRepository.findOne(id);
			List<BpmNode> nexists = bpmNodeRepository.findByDefinitionId(id);
			for (BpmNode existNode : nexists) {
				existNode.setBpmVariables(bpmVariablesRepository.findByNodeId(existNode.getId()));
			}
			bpmDefinition.setBpmNodes(nexists);
			return new BpmResponse(1, JSONObject.toJSONString(bpmDefinition)).toString();
		} catch (Exception e) {
			logger.error("获取模型失败：", e);
			return new BpmResponse(0, e.getMessage()).toString();
		}
	}

	@RequestMapping(value = "/bpmdefinition/deployment/{defId}", method = RequestMethod.POST, produces = "application/json")
	public String deploymentProcess(@ApiParam(name = "defId") @PathVariable(value = "defId") String defId, HttpServletRequest request, HttpServletResponse response) {

		try {
			BpmDefinition bpmDefinition = bpmDefinitionRepository.findOne(Integer.parseInt(defId));
			if(bpmDefinition == null) {
				throw new org.activiti.engine.ActivitiException("找不到该流程");
			}
			UserDetails user = userDetailsService.loadUserByUsername("24");
			if (user == null) {
				throw new org.activiti.engine.ActivitiException("不存在该用户");
			} else {
				org.springframework.security.core.Authentication auth = new UsernamePasswordAuthenticationToken(user,
						user.getPassword(), user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				tokenBasedRememberMeServices.loginSuccess(request, response, auth); // 使用 remember me
			}
			//发布
			bpmDefinitionService.publish(bpmDefinition);
			bpmDefinition.setStatus(2);
			bpmDefinitionRepository.save(bpmDefinition);
			//更新流程定义状态
			return new BpmResponse(1, "success").toString();
		} catch (Exception e) {
			logger.error("流程发布失败：", e);
			return new BpmResponse(0, e.getMessage()).toString();
		}
	}

}
