Feature: Ride API
  Scenario: Create ride
    Given Ride request dto
    """
        {
        "driverId": 1,
        "passengerId": 1,
        "sourceAddress": "Source address",
        "destinationAddress": "Destination address"
        }
    """
    When Create ride
    Then Response status is 201
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": 1,
        "passengerId": 1,
        "sourceAddress": "Source address",
        "destinationAddress": "Destination address",
        "rideState": "Created",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """
  Scenario: Get page of rides
    When Get page of rides
    Then Response status is 200
    And Response body contains Page dto
    """
        {
          "pageNumber": 0,
          "pageSize": 5,
          "totalPages": 1,
          "totalElements": 1,
          "content": [
              {
                  "id": 1,
                  "driverId": 1,
                  "passengerId": 1,
                  "sourceAddress": "Source address",
                  "destinationAddress": "Destination address",
                  "rideState": "Created",
                  "rideDateTime": "2024-11-26T00:30:13.84088",
                  "rideCost": 1894
              }
          ]
        }
    """
  Scenario: Get page of ride by driver id
    When Get page of rides by driver id 1
    Then Response status is 200
    And Response body contains Page dto
    """
        {
          "pageNumber": 0,
          "pageSize": 5,
          "totalPages": 1,
          "totalElements": 1,
          "content": [
              {
                  "id": 1,
                  "driverId": 1,
                  "passengerId": 1,
                  "sourceAddress": "Source address",
                  "destinationAddress": "Destination address",
                  "rideState": "Created",
                  "rideDateTime": "2024-11-26T00:30:13.84088",
                  "rideCost": 1894
              }
          ]
        }
    """

  Scenario: Get page of ride by passenger id
    When Get page of rides by passenger id 1
    Then Response status is 200
    And Response body contains Page dto
    """
        {
          "pageNumber": 0,
          "pageSize": 5,
          "totalPages": 1,
          "totalElements": 1,
          "content": [
              {
                  "id": 1,
                  "driverId": 1,
                  "passengerId": 1,
                  "sourceAddress": "Source address",
                  "destinationAddress": "Destination address",
                  "rideState": "Created",
                  "rideDateTime": "2024-11-26T00:30:13.84088",
                  "rideCost": 1894
              }
          ]
        }
    """

  Scenario: Get ride by id
    When Get ride by id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": 1,
        "passengerId": 1,
        "sourceAddress": "Source address",
        "destinationAddress": "Destination address",
        "rideState": "Created",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 1894
        }
    """
  Scenario: Update ride
    Given Ride request dto
    """
        {
        "driverId": 1,
        "passengerId": 1,
        "sourceAddress": "New source address",
        "destinationAddress": "New destination address"
        }
    """
    When Update ride with id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": 1,
        "passengerId": 1,
        "sourceAddress": "New source address",
        "destinationAddress": "New destination address",
        "rideState": "Created",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """
  Scenario: Update ride state
    Given Ride state request dto
    """
        {
        "rideState": "Cancelled"
        }
    """
    When Update ride state with id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": 1,
        "passengerId": 1,
        "sourceAddress": "New source address",
        "destinationAddress": "New destination address",
        "rideState": "Cancelled",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """
