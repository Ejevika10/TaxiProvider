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
        "rideDateTime": "2024-11-24 19:00",
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
  Scenario: Get ride by id
    When Get ride by id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
            "id": 1,
            "name": "passenger",
            "email": "passenger@mail.ru",
            "phone": "71234567890",
            "rating": 0.0
        }
    """
  Scenario: Update ride
    Given Ride request dto
    """
        {
        "name": "new_passenger",
        "email": "new_passenger@mail.ru",
        "phone": "71234567890"
        }
    """
    When Update ride with id 1
    Then Response status is 200
    And Response body contains Ride response dto
    """
        {
          "id": "1",
          "name": "new_passenger",
          "email": "new_passenger@mail.ru",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Delete ride
    When Delete ride with id 1
    Then Response status is 204