Feature: Passenger API
  Scenario: Create passenger
    Given Passenger request dto
    """
        {
        "name": "passenger",
        "email": "passenger@mail.ru",
        "phone": "71234567890"
        }
    """
    When Create passenger
    Then Response status is 201
    And Response body contains Passenger response dto
    """
        {
          "id": "1",
          "name": "passenger",
          "email": "passenger@mail.ru",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Get page of passengers
    When Get page of passengers
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
                "name": "passenger",
                "email": "passenger@mail.ru",
                "phone": "71234567890",
                "rating": 0.0
            }
          ]
        }
    """
  Scenario: Get passenger by id
    When Get passenger by id 1
    Then Response status is 200
    And Response body contains Passenger response dto
    """
        {
            "id": 1,
            "name": "passenger",
            "email": "passenger@mail.ru",
            "phone": "71234567890",
            "rating": 0.0
        }
    """
  Scenario: Update passenger
    Given Passenger request dto
    """
        {
        "name": "new_passenger",
        "email": "new_passenger@mail.ru",
        "phone": "71234567890"
        }
    """
    When Update passenger with id 1
    Then Response status is 200
    And Response body contains Passenger response dto
    """
        {
          "id": "1",
          "name": "new_passenger",
          "email": "new_passenger@mail.ru",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Delete passenger
    When Delete passenger with id 1
    Then Response status is 204
