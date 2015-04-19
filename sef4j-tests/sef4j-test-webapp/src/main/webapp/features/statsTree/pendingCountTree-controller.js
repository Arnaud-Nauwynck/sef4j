'use strict';

testwebapp.controller('PendingCountTreeController', function ($scope, $filter, $http, ngTableParams) {
	var vm = this;

	vm.message = "";
	vm.depthTreeTableData = 6;
	vm.pendingCountTreeMetrics = {};

	vm.pendingTaskCount = 1;
	vm.pendingTaskDepth = 2;
	
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
	
	
	vm.pendingCountTableData = 
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
			pendingSum: 0
	    }	    
	    ];


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
				pendingSum: pending.pendingSum,
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

	var pendingCountTreeToTableData = function(src) {
		var res = [];
		recursivePendingCountTreeToTableData(res, src, 
				'', '', 
				'', '',
				'', '',
				vm.depthTreeTableData);
		return res;
	}


	vm.loadPendingCount = function() {
		if (vm.message === "Loading...") {
			return;
		}
		vm.message = "Loading...";
		$http.get('app/rest/metricsStatsTree/pendingCount')
        .success(function(response) {
        	vm.pendingCountTreeMetrics = response;
        	vm.pendingCountTableData = pendingCountTreeToTableData(response);
        	
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
        total: vm.pendingCountTableData.length, // length of data
        getData: function ($defer, params) {
        	var inputData = vm.pendingCountTableData;
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

	
	// init
	// vm.loadPendingCount();
});
