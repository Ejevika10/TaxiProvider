Feature: Ride API
  Scenario: Create ride
    Given Access token
    And Ride create request dto
    """
        {
        "passengerId": "00000000-0000-0001-0000-000000000002",
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
        "driverId": null,
        "passengerId": "00000000-0000-0001-0000-000000000002",
        "sourceAddress": "Source address",
        "destinationAddress": "Destination address",
        "rideState": "Created",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """

  Scenario: Accept ride
    Given Access token
    And Ride accept request dto
    """
        {
        "driverId": "00000000-0000-0001-0000-000000000001"
        }
    """
    When Accept ride with id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": "00000000-0000-0001-0000-000000000001",
        "passengerId": "00000000-0000-0001-0000-000000000002",
        "sourceAddress": "Source address",
        "destinationAddress": "Destination address",
        "rideState": "Accepted",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """

  Scenario: Get page of rides
    Given Access token
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
                  "driverId": "00000000-0000-0001-0000-000000000001",
                  "passengerId": "00000000-0000-0001-0000-000000000002",
                  "sourceAddress": "Source address",
                  "destinationAddress": "Destination address",
                  "rideState": "Accepted",
                  "rideDateTime": "2024-11-26T00:30:13.84088",
                  "rideCost": 1894
              }
          ]
        }
    """
  Scenario: Get page of ride by driver id
    Given Access token
    When Get page of rides by driver id "00000000-0000-0001-0000-000000000001"
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
                  "driverId": "00000000-0000-0001-0000-000000000001",
                  "passengerId": "00000000-0000-0001-0000-000000000002",
                  "sourceAddress": "Source address",
                  "destinationAddress": "Destination address",
                  "rideState": "Accepted",
                  "rideDateTime": "2024-11-26T00:30:13.84088",
                  "rideCost": 1894
              }
          ]
        }
    """

  Scenario: Get page of ride by passenger id
    Given Access token
    When Get page of rides by passenger id "00000000-0000-0001-0000-000000000002"
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
                  "driverId": "00000000-0000-0001-0000-000000000001",
                  "passengerId": "00000000-0000-0001-0000-000000000002",
                  "sourceAddress": "Source address",
                  "destinationAddress": "Destination address",
                  "rideState": "Accepted",
                  "rideDateTime": "2024-11-26T00:30:13.84088",
                  "rideCost": 1894
              }
          ]
        }
    """

  Scenario: Get ride by id
    Given Access token
    When Get ride by id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": "00000000-0000-0001-0000-000000000001",
        "passengerId": "00000000-0000-0001-0000-000000000002",
        "sourceAddress": "Source address",
        "destinationAddress": "Destination address",
        "rideState": "Accepted",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 1894
        }
    """
  Scenario: Update ride
    Given Access token
    And Ride request dto
    """
        {
        "driverId": "00000000-0000-0001-0000-000000000001",
        "passengerId": "00000000-0000-0001-0000-000000000002",
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
        "driverId": "00000000-0000-0001-0000-000000000001",
        "passengerId": "00000000-0000-0001-0000-000000000002",
        "sourceAddress": "New source address",
        "destinationAddress": "New destination address",
        "rideState": "Accepted",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """
  Scenario: Update ride state
    Given Access token
    And Ride state request dto
    """
        {
        "rideState": "On the way to pick up the passenger"
        }
    """
    When Update ride state with id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": "00000000-0000-0001-0000-000000000001",
        "passengerId": "00000000-0000-0001-0000-000000000002",
        "sourceAddress": "New source address",
        "destinationAddress": "New destination address",
        "rideState": "On the way to pick up the passenger",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """

  Scenario: Cancel ride
    Given Access token
    When Cancel ride with id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
        "id": 1,
        "driverId": "00000000-0000-0001-0000-000000000001",
        "passengerId": "00000000-0000-0001-0000-000000000002",
        "sourceAddress": "New source address",
        "destinationAddress": "New destination address",
        "rideState": "Cancelled",
        "rideDateTime": "2024-11-26T00:30:13.84088",
        "rideCost": 9884
        }
    """
