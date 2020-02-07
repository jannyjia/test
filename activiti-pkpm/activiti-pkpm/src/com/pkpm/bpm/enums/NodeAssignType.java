package com.pkpm.bpm.enums;
/**
 * 节点指派人类型
 * @author wangjia
 *
 */
public enum NodeAssignType {
	starter("starter","QueryAssignStarter"), // 发起人
	user("user","QueryAssignUser"), // 指定人
	dep("dep","QueryAssignDep"), // 指定公司
	role("role","QueryAssignRole"), // 指定角色
	area("area","QueryAssignArea"), // 指定省市州
	sameDep("sameDep","QueryAssignSameDep"), // 与发起人和指定人相同公司
	sameArea("sameArea","QueryAssignSameArea"); // 与发起人和指定人省市州
	private String assignType;
    private String beanName;

    public String getAssignType() {
    	return assignType;
    }
    public String getBeanName() {
    	return beanName;
    }
    NodeAssignType(String assignType, String beanName) {
        this.assignType = assignType;
        this.beanName = beanName;
    }
}
