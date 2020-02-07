package com.pkpm.bpm.api;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 前端分页
 * @author wangjia
 * @date 2019-11-17
 *
 */
public class PagerInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	public class Page implements Serializable{
		private static final long serialVersionUID = 1L;
		private int pageSize=15;
		private int currentPage = 1;
		private int total = 0;
		
		public Page() {
			super();
		}
		public Page(int pageSize, int currentPage, int total) {
			super();
			this.pageSize = pageSize;
			this.currentPage = currentPage;
			this.total = total;
		}
		public int getPageSize() {
			return pageSize;
		}
		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}
		public int getCurrentPage() {
			return currentPage;
		}
		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}
		public int getTotal() {
			return total;
		}
		public void setTotalResult(int total) {
			this.total = total;
		}
		
	}
	private String sort;
	private String order;
	private Page page;
	private  Map<String, String> search;
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public void setPageObject(JSONObject page) {
		this.page = new Page(
				page.getIntValue("pageSize"),
				page.getIntValue("currentPage"), 
				page.getIntValue("total"));
	}
	public Map<String, String> getSearch() {
		return search;
	}
	public void setSearch(Map<String, String> search) {
		this.search = search;
	}
	
}
