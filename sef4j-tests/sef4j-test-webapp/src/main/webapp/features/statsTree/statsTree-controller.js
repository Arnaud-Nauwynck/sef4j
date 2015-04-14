'use strict';

testwebapp.controller('StatsTreeController', function ($scope, $filter, $http, ngTableParams) {
	var vm = this;
	
	vm.statsTreeMetrics = [];
	
	vm.loadStats = function() {
		$http.get('app/rest/metricsStatsTree/all')
        .success(function(response, status) {
        	vm.statsTreeMetrics = response;
        	// TODO transform Tree into flat list
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
        total: vm.statsTreeMetrics.length, // length of data
        getData: function ($defer, params) {
            // use build-in angular filter
        	var inputData = vm.statsTreeMetrics;
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

