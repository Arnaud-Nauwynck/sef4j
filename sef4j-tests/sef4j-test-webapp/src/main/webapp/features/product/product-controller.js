'use strict';

testwebapp.controller('ProductsController', function ($scope, $filter, ngTableParams, ProductService) {
	var vm = this;
	
	vm.message = "";
	vm.products = [];
	
	vm.load = function() {
		ProductService.asyncFindAll().then(function(data){
			vm.products = data;
		});
	}
	vm.launchTasks = function() {
		ProductService.launchTasks();
	};
	
    
	vm.productTableParams = new ngTableParams({
        page: 1,            // show first page
        count: 25,          // count per page
        filter: {
            name: 'b'       // initial filter
        },
        sorting: {
            //name: 'asc'   // initial sorting
        }
    }, {
        total: vm.products.length, // length of data
        getData: function ($defer, params) {
            // use build-in angular filter
        	var inputData = vm.products;
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

	vm.selectedProducts = [];

	vm.changeSelection = function(product) {
        // console.info(product);
		if (product.$selected) {
			vm.selectedProducts.push(product);
		} else {
			// $scope.selectedUsers.remove(user);
			var index = vm.selectedProducts.indexOf(product);
			vm.selectedProducts.splice(index, 1);
		}
		
		vm.selectedProducts2 = $filter('filter')(vm.products, function(data) { return data.$selected === true; });
    } 

	vm.refreshSelectedList = function() {
		vm.selectedProducts = $filter('filter')(vm.products, function(data) { return data.$selected === true; });
		vm.selectedProducts2 = $filter('filter')(vm.products, function(data) { return data.$selected === true; });
	};
	

	vm.match = {
		name : '',
		shortDescr: ''
	}
	
	var matchFunc = function(data) {
		return (vm.match === '' || data.name.indexOf(vm.match.name)!=-1)
		&& (vm.shortDescr === '' || data.shortDescr.indexOf(vm.match.shortDescr)!=-1); 
	};

	var updateMatch = function(fieldChange) {
		// console.info("$watch vm.match." + fieldChange);
		vm.products.forEach(function(e) {
			var m = matchFunc(e);
			e.match = m;
		});
	};
	$scope.$watch(function() { return vm.match.name; }, function(value) {
		updateMatch("name"); 
	});
	$scope.$watch(function() { return vm.match.shortDescr; }, function(value) {
		updateMatch("shortDescr"); 
	});
	
	vm.matchAddOrRemoveSelect = function(addOrRemove) {
		var chg = false;
		vm.products.forEach(function(e) {
			var m = e.match; // matchFunc(e);
			if (addOrRemove && m && !e.$selected) {
				e.$selected = true;
				chg=true;
			}
			if (! addOrRemove && m && e.$selected) {
				e.$selected = false;
				chg=true;
			}
		});
		if (chg) {
			vm.refreshSelectedList();
		}
	};
	
	
	// init
//	if (vm.products.length == 0) {
//		vm.load();
//	}
	
});

