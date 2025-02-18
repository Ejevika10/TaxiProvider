Feature: Passenger API
  Scenario: Create passenger
    Given Access token
    And Passenger create request dto
    """
        {
        "id": "00000000-0000-0001-0000-000000000001",
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
          "id": "00000000-0000-0001-0000-000000000001",
          "name": "passenger",
          "email": "passenger@mail.ru",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Get page of passengers
    Given Access token
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
                "id": "00000000-0000-0001-0000-000000000001",
                "name": "passenger",
                "email": "passenger@mail.ru",
                "phone": "71234567890",
                "rating": 0.0
            }
          ]
        }
    """
  Scenario: Get passenger by id
    Given Access token
    When Get passenger by id "00000000-0000-0001-0000-000000000001"
    Then Response status is 200
    And Response body contains Passenger response dto
    """
        {
            "id": "00000000-0000-0001-0000-000000000001",
            "name": "passenger",
            "email": "passenger@mail.ru",
            "phone": "71234567890",
            "rating": 0.0
        }
    """
  Scenario: Update passenger
    Given Access token
    Given Passenger update request dto
    """
        {
        "name": "new_passenger",
        "email": "new_passenger@mail.ru",
        "phone": "71234567890"
        }
    """
    When Update passenger with id "00000000-0000-0001-0000-000000000001"
    Then Response status is 200
    And Response body contains Passenger response dto
    """
        {
          "id": "00000000-0000-0001-0000-000000000001",
          "name": "new_passenger",
          "email": "new_passenger@mail.ru",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Delete passenger
    Given Access token
    When Delete passenger with id "00000000-0000-0001-0000-000000000001"
    Then Response status is 204
