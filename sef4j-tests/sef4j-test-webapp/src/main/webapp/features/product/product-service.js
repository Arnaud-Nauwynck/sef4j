'use strict';

testwebapp.factory('ProductService', function ($http) {
    return {
        asyncFindAll: function() {
            var promise = $http.get('app/rest/products/all')
            .then(function (response, status) {
                return response.data;
            });
            return promise;
        }
    }
});


