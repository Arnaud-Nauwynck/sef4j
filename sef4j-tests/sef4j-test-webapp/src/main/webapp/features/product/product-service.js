'use strict';

testwebapp.factory('ProductService', function ($http) {
    return {
        asyncFindAll: function() {
            var promise = $http.get('app/rest/products/all')
            .then(function (response) {
                return response.data;
            });
            return promise;
        },
        launchTasks: function() {
            var promise = $http.get('app/rest/products/launchTasks')
            .then(function (response) {
                return response.data;
            });
            return promise;
        }
    }
});


