'use strict';

/* App Module */

var testwebapp = angular.module('testwebapp', 
		['ngResource', 'ngRoute' 
	    ]);

testwebapp.config(function ($routeProvider, $httpProvider) {

    $routeProvider
        .when('/products', {
            templateUrl: 'features/product/products.html',
            controller: 'ProductsController'
        })
        .when('/home', {
            templateUrl: 'features/home/home.html',
            controller: 'HomeController'
        })
        .otherwise({
            templateUrl: 'features/home/home.html'
            // controller: 'HomeController'
        })
        ;
});