Feature: Driver API
  Scenario: Create driver
    Given Driver request dto
    """
        {
        "name": "Driver",
        "email": "driver@email.com",
        "phone": "71234567890"
        }
    """
    When Create driver
    Then Response status is 201
    And Response body contains Driver response dto
    """
        {
          "id": "1",
          "name": "Driver",
          "email": "driver@email.com",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Get page of drivers
    When Get page of drivers
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
                "name": "Driver",
                "email": "driver@email.com",
                "phone": "71234567890",
                "rating": 0.0
            }
          ]
        }
    """
  Scenario: Get driver by id
    When Get driver by id 1
    Then Response status is 200
    And Response body contains Driver response dto
    """
        {
            "id": 1,
            "name": "Driver",
            "email": "driver@email.com",
            "phone": "71234567890",
            "rating": 0.0
        }
    """
  Scenario: Update driver
    Given Driver request dto
    """
        {
        "name": "new_driver",
        "email": "new_driver@email.com",
        "phone": "71234567890"
        }
    """
    When Update driver with id 1
    Then Response status is 200
    And Response body contains Driver response dto
    """
        {
          "id": "1",
          "name": "new_driver",
          "email": "new_driver@email.com",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Delete driver
    When Delete driver with id 1
    Then Response status is 204
