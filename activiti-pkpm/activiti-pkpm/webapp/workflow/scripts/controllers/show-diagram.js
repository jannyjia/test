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

angular.module('activitiApp').controller('ShowDiagramController', 
	['$rootScope', '$scope', '$translate', '$http', '$timeout', '$location', '$modal', '$routeParams', 'AppDefinitionService','ResourceService', 'appResourceRoot',
	function ($rootScope, $scope, $translate, $http, $timeout, $location, $modal, $routeParams, AppDefinitionService, ResourceService, appResourceRoot) {
	  $scope.processId = $routeParams.processId;
    
    $timeout(function(){
      //获取并显示
      jQuery("#bpmnModel").attr('data-model-id', $scope.processId);
      jQuery("#bpmnModel").attr('data-model-type', 'runtime');
      
      var viewerUrl = appResourceRoot + "../display/displaymodel.html?version=" + Date.now();

      // If Activiti has been deployed inside an AMD environment Raphael will fail to register
      // itself globally until displaymodel.js (which depends ona global Raphale variable) is runned,
      // therefor remove AMD's define method until we have loaded in Raphael and displaymodel.js
      // and assume/hope its not used during.
      var amdDefine = window.define;
      window.define = undefined;
      ResourceService.loadFromHtml(viewerUrl, function(){
          // Restore AMD's define method again
          window.define = amdDefine;
      });
      
    }, 100);
	  
}]);
