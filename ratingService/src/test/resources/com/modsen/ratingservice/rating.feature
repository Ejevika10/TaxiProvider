Feature: Passenger API
  Scenario: Create passenger rating
    Given Rating request dto
    """
        {
        "name": "passenger",
        "email": "passenger@mail.ru",
        "phone": "71234567890"
        }
    """
    When Create passenger rating
    Then Response status is 201
    And Response body contains Rating response dto
    """
        {
          "id": "1",
          "name": "passenger",
          "email": "passenger@mail.ru",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Get page of passenger ratings
    When Get page of passenger ratings
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
  Scenario: Get passenger rating by id
    When Get passenger rating by id 1
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": 1,
            "name": "passenger",
            "email": "passenger@mail.ru",
            "phone": "71234567890",
            "rating": 0.0
        }
    """
  Scenario: Update passenger rating
    Given Rating request dto
    """
        {
        "name": "new_passenger",
        "email": "new_passenger@mail.ru",
        "phone": "71234567890"
        }
    """
    When Update passenger rating with id 1
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
          "id": "1",
          "name": "new_passenger",
          "email": "new_passenger@mail.ru",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Delete passenger rating
    When Delete passenger rating with id 1
    Then Response status is 204