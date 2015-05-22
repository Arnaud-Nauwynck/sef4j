'use strict';
testwebapp.controller('PendingCountTreeController', function ($scope, $filter, $http, $interval, ngTableParams, StatsAsyncService) {
	var vm = this;

	vm.message = "";
	vm.depthTreeTableData = 6;

	vm.pendingTaskCount = 1;
	vm.pendingTaskDepth = 2;
	
	vm.pendingCountData = {
		// treeData: null,
		tableData: [],
		serverTimeMillis: 0,
		clientTimeMillis: 0,
		
		lastUpdateTime: 0
	};

	vm.showRealTimeColumn = false;
	vm.enableUpdateTime = false;
	vm.autoRefreshFrequency = 0;
	vm.autoRefreshRemainCount = 0;
	vm.stopTime = null;
	
	
	vm.dummyPendingCountTableData =
		[
	    {
	    	treePath: "a.b.Class1:method1 / a.b.Class2:method2 / a.b.Class3:method3",
	    	shortTreePath: "method1 / method2 / method3",
	    	rootClassName: "a.b.Class1",
	    	rootMethodName: "method1",
	    	parentClassName: "a.b.Class2",
	    	parentMethodName: "method2",
	    	className: "a.b.Class3",
	    	methodName: "method3",

			pendingCount: 0,
			pendingSumStartTime: 0, // in nanos, using server-side clock time
			
			pendingAverageMillisOnLoad: 0, // pending elapsed time in millis, when receiving data from server
			pendingAverageMillisUntilNow: 0 // time dependent, update on client-side
	    }	    
	    ];
	
	vm.startPendingTask = function() {
		vm.message = "...";
		var req = {
				params: {
					count: vm.pendingTaskCount,
					depth: vm.pendingTaskDepth
				}
		};
		$http.get('app/rest/dummyPendingTask/startPendingTask', req)
        .success(function(response) {
        	vm.message = "";
        })
        .error(function(response) {
        	vm.message = "ERROR:" + response.message;
        });
	};
	
	vm.stopPendingTask = function(depth) {
		vm.message = "...";
		var req = {
				params: {
					count: vm.pendingTaskCount
				}
		};
		$http.get('app/rest/dummyPendingTask/stopPendingTask', req)
        .success(function(response) {
        	vm.message = "";
        })
        .error(function(response) {
        	vm.message = "ERROR:" + response.message;
        });
	};
	

	
	
	var recursivePendingCountTreeToTableData = function(res, tree, 
			rootClassName, rootMethodName,
			parentPath, parentShortPath, parentClassName, parentMethodName, 
			remainDepth) {
		var name = tree.name || '';
		var indexSep = name.indexOf(':');
		var className = (indexSep != -1)? name.substring(0, indexSep) : '';
		var methodName = (indexSep != -1)? name.substring(indexSep+1, name.length) : '';
		var currPath = (parentPath != '' && name != '')? parentPath + " / " + name : name;
		var currShortPath = (parentShortPath != '' && methodName != '')? parentShortPath + " / " + methodName : methodName;
		
		if (rootClassName == null || rootClassName === '') {
			rootClassName = className;
			rootMethodName = methodName;
		}
		
		var pending = (tree.propsMap != null)? tree.propsMap['pending'] : null;
		if (pending) {
			var resElt = {
		    	treePath: currPath,
		    	shortTreePath: currShortPath,
		    	rootClassName: rootClassName,
		    	rootMethodName: rootMethodName,
		    	parentClassName: parentClassName,
		    	parentMethodName: parentMethodName,
		    	className: className,
		    	methodName: methodName,

		    	pendingCount: pending.pendingCount,
		    	pendingSumStartTime: pending.pendingSumStartTime,
				pendingAverageMillisUntilNow: 0, // cf updateTime() : time dependent, update on client-side
			};
			res.push(resElt);
		}
		
		if (remainDepth == -1 || remainDepth > 0) {
			// recurse child
			var remainDepth = (remainDepth == -1)? -1 : remainDepth - 1;
			jQuery.each(tree.childMap, function(childKey, childValue) {
				// *** recurse ***
				recursivePendingCountTreeToTableData(res, childValue, 
						rootClassName, rootMethodName,
						currPath, currShortPath, 
						className, methodName, 
						remainDepth);
			});
		}
		
	}

	var pendingCountTreeToTableData = function(src, resData) {
		resData.tableData = [];
		var res = resData.tableData;
		
		var timeNow = new Date().getTime();
		var serverClockNanos = src.propsMap["clockNanos"];
		var serverTimeMillis = src.propsMap["timeNowMillis"];

		// res.serverClockNanos = serverClockNanos;
		res.serverTimeMillis = serverTimeMillis;
		res.clientTimeMillis = timeNow;
		res.lastUpdateTime = timeNow; 
		
		recursivePendingCountTreeToTableData(res, src, 
				'', '', 
				'', '',
				'', '',
				vm.depthTreeTableData);
		
		
		res.forEach(function(e) {
			if (e.pendingCount != 0) {
				var avgOnLoad = (e.pendingCount * serverClockNanos - e.pendingSumStartTime ) / e.pendingCount;
				e.pendingAverageMillisOnLoad = avgOnLoad / 1000000;
				e.pendingAverageStartTimeMillis = timeNow - e.pendingAverageMillisOnLoad; //??
				e.pendingAverageMillisUntilNow = e.pendingAverageMillisOnLoad; 
			}
		});
		
    	vm.updateTime();
	}


	vm.updateTime = function() {
		var timeNow = new Date().getTime();
		var incrMillis = (timeNow - vm.pendingCountData.lastUpdateTime); // / 1000; 
		vm.pendingCountData.tableData.forEach(function(e) {
			e.pendingAverageMillisUntilNow += e.pendingCount * incrMillis;
		});
		vm.pendingCountData.lastUpdateTime = timeNow;
	}
	
	

	vm.showTreePath = function(e) {
		var treePath = e.treePath;
		alert(treePath);
	};
	
	vm.loadPendingCount = function() {
		if (vm.message === "Loading...") {
			return;
		}
		vm.message = "Loading...";
		$http.get('app/rest/metricsStatsTree/pendingCount')
        .success(function(response) {
        	pendingCountTreeToTableData(response, vm.pendingCountData);
        	
        	vm.pendingCountTableParams.reload();
    		vm.message = "";
        })
        .error(function(response) {
        	vm.message = "Failed";
        });
	};

	
    
	vm.pendingCountTableParams = new ngTableParams({
        page: 1,            // show first page
        count: 25,          // count per page
        filter: {
            // name: ''       // initial filter
        },
        sorting: {
            //name: 'asc'   // initial sorting
        }
    }, {
        total: vm.pendingCountData.tableData.length, // length of data
        getData: function ($defer, params) {
        	var inputData = vm.pendingCountData.tableData;
            var filteredData = params.filter() ?
                    $filter('filter')(inputData, params.filter()) :
                    data;
            var orderedData = params.sorting() ?
                    $filter('orderBy')(filteredData, params.orderBy()) :
                    data;
            params.total(orderedData.length); // set total for recalc pagination
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

	
	
	// Client-side polling timer
	// ---------------------------------------------------------------
	
	vm.startUpdateTimer = function() {
		if (vm.stopTime != null) {
			return;
		}
		var updateTimeCallback = function() {
			if (vm.autoRefreshFrequency > 0) {
				vm.autoRefreshRemainCount--;
				if (vm.autoRefreshRemainCount < 0) {
					vm.autoRefreshRemainCount = vm.autoRefreshFrequency;
					vm.loadPendingCount();
					return;
				}
			}		
			vm.updateTime();
		};
		vm.stopTime = $interval(updateTimeCallback, 1000);

	};

	$scope.$on('$destroy', function() {
    	if (vm.stopTime != null) {
    		$interval.cancel(vm.stopTime);
    		vm.stopTime = null;
    	}
    });

	vm.stopUpdateTimer = function() {
		var toStop = vm.stopTime;
		vm.stopTime = null;
		if (toStop == null) {
			return;
		}
		$interval.cancel(toStop);
	}

	vm.onChangeEnableUpdateTime = function() {
		if (vm.enableUpdateTime || vm.autoRefreshFrequency > 0) {
			vm.startUpdateTimer();
		} else {
			vm.stopUpdateTimer();
		}
	}

	vm.onChangeAutoRefreshFrequency = function() {
		if (vm.enableUpdateTime || v.autoRefreshFrequency > 0) {
			vm.startUpdateTimer();
		} else {
			vm.stopUpdateTimer();
		}
	}

	
	// WebSocket for server-push
	// ------------------------------------------------------------------------

	vm.startPendingCountPublisherPeriodicTask = function() {
		$http.post('app/rest/metricsStatsTree/startPendingCountPublisherPeriodicTask');
	}
	vm.stopPendingCountPublisherPeriodicTask = function() {
		$http.post('app/rest/metricsStatsTree/stopPendingCountPublisherPeriodicTask');
	}
	
	vm.pendingCountListener = function(data) {
		vm.message = "last received msg at " + new Date();
		
		pendingCountTreeToTableData(data, vm.pendingCountData);
    	
    	vm.pendingCountTableParams.reload();
		vm.message = "";
	};
	
	StatsAsyncService.addListener(vm.pendingCountListener);
	
	$scope.$on('$destroy', function() {
		StatsAsyncService.removeListener(pendingCountListener);
	});
	
	

	
	// init
	// vm.loadPendingCount();
});
