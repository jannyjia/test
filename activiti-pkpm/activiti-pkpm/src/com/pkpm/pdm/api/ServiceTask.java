package com.pkpm.pdm.api;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

public class ServiceTask implements JavaDelegate{
 
 //流程变量
 
 private Expression text1;
 
  
 
 //重写委托的提交方法
 
 @Override
 
 public void execute(DelegateExecution execution) {
 
	 System.out.println("serviceTask已经执行已经执行！");	 
	 String value1 = (String) text1.getValue(execution); 	
	 System.out.println(value1); 
     execution.setVariable("var1", new StringBuffer(value1).reverse().toString());
 
 }
 
  
 
}