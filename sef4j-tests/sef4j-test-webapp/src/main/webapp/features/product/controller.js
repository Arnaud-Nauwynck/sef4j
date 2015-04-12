'use strict';

testwebapp.controller('ProductsController', function ($scope, ProductService) {

	$scope.products = [];
	
    ProductService.findAll().then(function(data){
        $scope.products = data;
    });
	
});

