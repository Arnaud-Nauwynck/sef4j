'use strict';

/* App Module */

var testwebapp = angular.module('testwebapp', 
		['ngResource', 'ngRoute', 'ngTable', 'ngTableResizableColumns'
	    ]);

testwebapp.config(function ($routeProvider, $httpProvider) {

    $routeProvider
        .when('/products', {
            templateUrl: 'features/product/products.html'
            //, controller: 'ProductsController as products'
        })
        .when('/home', {
            templateUrl: 'features/home/home.html',
            controller: 'HomeController'
        })
        .when('/pendingCountTree', {
        	templateUrl: 'features/statsTree/pendingCountTree.html'
        })
        .when('/statsTreeMetrics', {
        	templateUrl: 'features/statsTree/statsTree.html'
        })
        .otherwise({
            templateUrl: 'features/home/home.html'
            // controller: 'HomeController'
        })
        ;
});