'use strict';

testwebapp.controller('StatsTreeController', function ($scope, $filter, $http, ngTableParams) {
	var vm = this;
	
	vm.depthTreeTableData = 6;
	vm.statsTreeMetrics = {};

	vm.statsTreeMetricsTableData = 
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
			pendingSum: 0,
			countTotal: 10,
			sumTotal: 2090,
			countFast01: 9,
			sumFast01: 90
	    },
	    {
	    	treePath: "a.b.Class1:method1 / a.b.Class2:method2 / a.b.Class3:method4",
	    	shortTreePath: "method1 / method2 / method4",
	    	rootClassName: "a.b.Class1",
	    	rootMethodName: "method1",
	    	parentClassName: "a.b.Class2",
	    	parentMethodName: "method2",
	    	className: "a.b.Class4",
	    	methodName: "method4",

			pendingCount: 0,
			pendingSum: 0,
			countTotal: 6,
			sumTotal: 1050,
			countFast01: 5,
			sumFast01: 50
	    },
	    {
	    	treePath: "a.b.Class1:method1 / a.b.Class2:method2",
	    	shortTreePath: "method1 / method2",
	    	rootClassName: "a.b.Class1",
	    	rootMethodName: "method1",
	    	parentClassName: "a.b.Class1",
	    	parentMethodName: "method1",
	    	className: "a.b.Class2",
	    	methodName: "method2",

			pendingCount: 0,
			pendingSum: 0,
			countTotal: 4,
			sumTotal: 1030,
			countFast01: 3,
			sumFast01: 30
	    },
	    
	    ];

	/*
			<td data-title="'TreePath'" sortable="'treePath'" filter="{ 'treePath': 'text' }">{{e.path}}</td>
			<td data-title="'ShortTreePath'" sortable="'shortTreePath'" filter="{ 'shortTreePath': 'text' }">{{e.shortTreePath}}</td>

			<td data-title="'rootClassName'" sortable="'rootClassName'" filter="{ 'rootClassName': 'text' }">{{e.rootClassName}}</td>
			<td data-title="'rootMethodName'" sortable="'rootMethodName'" filter="{ 'rootMethodName': 'text' }">{{e.rootMethodName}}</td>

			<td data-title="'parentClassName'" sortable="'parentClassName'" filter="{ 'parentClassName': 'text' }">{{e.parentClassName}}</td>
			<td data-title="'parentMethodName'" sortable="'parentMethodName'" filter="{ 'parentMethodName': 'text' }">{{e.parentMethodName}}</td>

			<td data-title="'className'" sortable="'className'" filter="{ 'className': 'text' }">{{e.className}}</td>
			<td data-title="'methodName'" sortable="'methodName'" filter="{ 'methodName': 'text' }">{{e.methodName}}</td>


			<td data-title="'PendingCount'" sortable="'pendingCount'" filter="{ 'pendingCount': 'text' }">{{e.pendingCount}}</td>
			<td data-title="'PendingSum'" sortable="'pendingSum'" filter="{ 'pendingSum': 'text' }">{{e.pendingSum}}</td>

			<td data-title="'CountTotal'" sortable="'countTotal'" filter="{ 'countTotal': 'text' }">{{e.countTotal}}</td>
			<td data-title="'SumTotal'" sortable="'sumTotal'" filter="{ 'sumTotal': 'text' }">{{e.sumTotal}}</td>

			<td data-title="'CountFast'" sortable="'countFast01'" filter="{ 'countFast01': 'text' }">{{e.countFast01}}</td>
			<td data-title="'SumFast'" sortable="'sumFast01'" filter="{ 'sumFast01': 'text' }">{{e.sumFast01}}</td>

	 */
	var recursiveStatsTreeToStatsTableData = function(res, tree, 
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
		var perfStats = tree.propsMap['stats'];
		if (perfStats) {
			var elapsed = perfStats.elapsedTimeStats;
			var elapsedSlots = elapsed.slotInfoCopy;
			var resElt = {
		    	treePath: currPath,
		    	shortTreePath: currShortPath,
		    	rootClassName: rootClassName,
		    	rootMethodName: rootMethodName,
		    	parentClassName: parentClassName,
		    	parentMethodName: parentMethodName,
		    	className: className,
		    	methodName: methodName,

		    	pendingCount: perfStats.pendingCounts.pendingCount,
				pendingSum: perfStats.pendingCounts.pendingSum,
				countTotal: elapsedSlots.reduce(function(acc,e) { return e.count + acc; }, 0),
				sumTotal: elapsedSlots.reduce(function(acc,e) { return e.sum + acc; }, 0),
				countFast01: elapsedSlots[0].count + elapsedSlots[1].count,
				sumFast01: elapsedSlots[0].sum + elapsedSlots[1].sum
			};
			res.push(resElt);
		}
		
		if (remainDepth == -1 || remainDepth > 0) {
			// recurse child
			var remainDepth = (remainDepth == -1)? -1 : remainDepth - 1;
			jQuery.each(tree.childMap, function(childKey, childValue) {
				// *** recurse ***
				recursiveStatsTreeToStatsTableData(res, childValue, 
						rootClassName, rootMethodName,
						currPath, currShortPath, 
						className, methodName, 
						remainDepth);
			});
		}
		
	}

	vm.loadStats = function() {
		$http.get('app/rest/metricsStatsTree/all')
        .success(function(response, status) {
        	vm.statsTreeMetrics = response;
        	// transform Tree into flat list
        	var resTableData = [];
        	
        	recursiveStatsTreeToStatsTableData(resTableData, response, 
        			'', '', 
        			'', '',
        			'', '',
        			vm.depthTreeTableData);
        	
        	vm.statsTreeMetricsTableData = resTableData;
        	vm.statsTreeTableParams.reload();
        });
	};

	
    
	vm.statsTreeTableParams = new ngTableParams({
        page: 1,            // show first page
        count: 25,          // count per page
        filter: {
            // name: ''       // initial filter
        },
        sorting: {
            //name: 'asc'   // initial sorting
        }
    }, {
        total: vm.statsTreeMetricsTableData.length, // length of data
        getData: function ($defer, params) {
            // use build-in angular filter
        	var inputData = vm.statsTreeMetricsTableData;
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

	
});

