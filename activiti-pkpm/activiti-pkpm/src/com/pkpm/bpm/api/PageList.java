package com.pkpm.bpm.api;

import java.util.ArrayList;

public class PageList<E>
extends ArrayList<E>
{
	private PagerInfo pageBean = new PagerInfo();
	
	public PagerInfo getPageBean()
	{
	  return this.pageBean;
	}
	
	public void setPageBean(PagerInfo pageBean)
	{
	  this.pageBean = pageBean;
	}
	
}
