var app = angular.module('application', [])
app.controller('controller', function($scope, $http, $interval) {
    $scope.refresh = function() {
        $http.post("/api/flights/?page=" + ($scope.currentPage - 1), $scope.example)
        .then(function(response) {
            $scope.flights = response.data.content

            $scope.currentPage = response.data.number + 1
            $scope.totalPages = response.data.totalPages
            $scope.totalFlights = response.data.totalElements
            $scope.pages = []
            for (var i = 1; i <= $scope.totalPages; ++i) {
                $scope.pages.push(i)
            }

            $scope.pageDuration = response.data.pageDuration
            $scope.otherDuration = response.data.otherDuration
            $scope.totalDuration = response.data.totalDuration
        })
    }
    $scope.delete = function(flight) {
        if (confirm("Delete " + description(flight) + "?")) {
            $http.delete("/api/flights/flight/" + flight.id)
            .then($scope.refresh)
        }
    }
    $scope.addTerm = function(term, value) {
        $scope.form[term] = value
        $scope.search()
    }
    $scope.search = function() {
        $scope.example.aircraft = $scope.form.aircraft
        $scope.example.callsign = $scope.form.callsign
        $scope.example.destination = $scope.form.destination
        $scope.example.origin = $scope.form.origin
        $scope.refresh()
    }
    $scope.clear = function() {
        $scope.form = {}
        $scope.example = {}
        $scope.refresh()
    }
    $scope.gotoPage = function(page) {
        $scope.currentPage = page
        $scope.refresh()
    }
    $scope.clear()
    $scope.refresh()
    $interval($scope.refresh, 1000)
})

app.filter('duration', function() {
    return function(minutes) {
        if (minutes == null) {
            return ""
        }
        var hours = Math.floor(minutes / 60)
        var m = Math.floor(minutes % 60)
        var mins = m.toString()
        if (m < 10) {
            mins= '0' + mins
        }
        return hours + ":" + mins
    }
})

app.filter('capitalize', function() {
    return capitalize
})

function capitalize(s) {
    if (s.length == 0) {
        return s
    }
    return s[0].toUpperCase() + s.substring(1)
}

function description(flight) {
    return capitalize(flight.aircraft) + " flight from " + flight.origin + " to " + flight.destination
}