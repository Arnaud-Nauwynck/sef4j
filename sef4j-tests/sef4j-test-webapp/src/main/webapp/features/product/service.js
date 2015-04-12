'use strict';

testwebapp.factory('ProductService', function ($http) {
    return {
        findAll: function() {
            var promise = $http.get('app/rest/products/all')
            	.then(function (response) {
                return response.data;
            });
            return promise;
        }
    }
});


