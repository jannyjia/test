/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

angular.module('activitiApp').controller('ListOperateHistoryController', 
	['$rootScope', '$scope', '$translate', '$http', '$timeout', '$location', '$modal', '$routeParams', 'AppDefinitionService','ResourceService', 'appResourceRoot','TaskService',
	function ($rootScope, $scope, $translate, $http, $timeout, $location, $modal, $routeParams, AppDefinitionService, ResourceService, appResourceRoot, TaskService) {
	
	//初始model
	$scope.model = {
		queryParams: undefined,
        param: {
        	//no sort
        	page: 0,
        	size: 25
        },
        //是否有下一页
        hasNextPage: false
    };
		
	var queryParams = decodeURI($routeParams.quotedQueryParam);
	queryParams = JSON.parse(queryParams);

	/*
	 * {
	 * 	"assignment": "", //默认没有assignment 会使用当前用户
	 * 	"state": "completed", //设为completed才会去查记录表，否则只查当前运行表
	 * 	"deploymentKey": "", //按发布Key查
	 * 	"processInstanceId": "", //按流程实例Id查
	 * 	"text": "",
	 * 	"processDefinitionId": "", //按流程定义Id查
	 * 	"dueBefore": "",
	 * 	"dueAfter": "",
	 * 	"sort": "",	//不予使用
	 * 	"page": "",	//不予使用
	 * 	"size": "",	//不予使用
	 * }
	 * 
	 * 
	 * */
	
	//保留原始queryParams
	$scope.model.queryParams = queryParams;
	
    var data = {};
    if(queryParams.assignment) {
    	data.assignment = queryParams.assignment;
    }
    
    if(queryParams.state && queryParams.state == "completed") {
    	data.state = queryParams.state;
    }
    
    if(queryParams.deploymentKey) {
    	data.deploymentKey = queryParams.deploymentKey;
    }
    
    if(queryParams.processInstanceId) {
    	data.processInstanceId = queryParams.processInstanceId;
    }
    
    if(queryParams.text) {
    	data.text = queryParams.text;
    }
    
    if(queryParams.processDefinitionId) {
    	data.processDefinitionId = queryParams.processDefinitionId;
    }
    
    if(queryParams.dueBefore) {
    	data.dueBefore = queryParams.dueBefore;
    }
    
    if(queryParams.dueAfter) {
    	data.dueAfter = queryParams.dueAfter;
    }
    
    //加载查询参数
    $scope.model.param = Object.assign($scope.model.param, data);
    
    
    //表格数据刷新
    $scope.refreshFilter = function() {
    	var params = $scope.model.param;
    	
    	var data = params;
    	TaskService.queryTasks(data).then(function (response) {
    		$scope.model.tasks = response.data;
            
            $scope.gridOptions.data = $scope.model.tasks;
            
            $scope.gridOptions.totalItems = response.total;
            $scope.model.hasNextPage = (response.start + response.size < response.total);
        });
    };
	
    //表格日期时间格式化
    $scope.timeFormat = function(isoTime) {
    	var date = new Date(isoTime);
    	if(isNaN(date.getTime())) {
    		return "";
    	}
    	
    	return date.toLocaleDateString() + " " + date.toLocaleTimeString();
    };
    
    //表格ui-gird 定义
    $scope.gridOptions = {
    	columnDefs: [
    		{field: 'id', displayName:'ID'},
            {field: 'name', displayName:'名字'},
            {field: 'created', displayName:'创建时间', 
            	cellTemplate: `<div class="ui-grid-cell-contents">
            		<span ng-bind="grid.appScope.timeFormat(row.entity.created)">
            	</div>`},
            {field: 'endDate', displayName:'完成时间'},
            {field: 'assignee.id', displayName:'操作人'},
    	],
    	enableSorting: false,  //是否sort
    	
    	paginationPageSizes: [15, 25],
    	paginationPageSize: 15,
    	
    	enablePagination: true,
    	enablePaginationControls: true,
    	paginationCurrentPage:1,
    	totalItems : 0,
    	useExternalPagination:true,
    	enableHorizontalScrollbar :  0,
    	onRegisterApi:(gridApi) => {
    		$scope.model.param.page = $scope.gridOptions.paginationCurrentPage - 1;
    		$scope.model.param.size = $scope.gridOptions.paginationPageSize;
    		$scope.refreshFilter();
    		
    		
    		this.gridApi = gridApi;
    		this.gridApi.pagination.on.paginationChanged($scope, (newPage, pageSize) => {
    			//后端page是从0开始索引
    			$scope.model.param.page = newPage - 1;
	        	$scope.model.param.size = pageSize;
	        	
	        	$scope.refreshFilter();
	        });
	        
	    }
    };
    
}]);