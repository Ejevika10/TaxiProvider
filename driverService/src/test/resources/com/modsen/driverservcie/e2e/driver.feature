Feature: Driver API
  Scenario: Create driver
    Given Access token
    And Driver create request dto
    """
        {
        "id": "00000000-0000-0001-0000-000000000001",
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
          "id": "00000000-0000-0001-0000-000000000001",
          "name": "Driver",
          "email": "driver@email.com",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Get page of drivers
    Given Access token
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
                "id": "00000000-0000-0001-0000-000000000001",
                "name": "Driver",
                "email": "driver@email.com",
                "phone": "71234567890",
                "rating": 0.0
            }
          ]
        }
    """
  Scenario: Get driver by id
    Given Access token
    When Get driver by id "00000000-0000-0001-0000-000000000001"
    Then Response status is 200
    And Response body contains Driver response dto
    """
        {
            "id": "00000000-0000-0001-0000-000000000001",
            "name": "Driver",
            "email": "driver@email.com",
            "phone": "71234567890",
            "rating": 0.0
        }
    """
  Scenario: Update driver
    Given Access token
    And Driver update request dto
    """
        {
        "name": "new_driver",
        "email": "new_driver@email.com",
        "phone": "71234567890"
        }
    """
    When Update driver with id "00000000-0000-0001-0000-000000000001"
    Then Response status is 200
    And Response body contains Driver response dto
    """
        {
          "id": "00000000-0000-0001-0000-000000000001",
          "name": "new_driver",
          "email": "new_driver@email.com",
          "phone": "71234567890",
          "rating": "0.0"
        }
    """
  Scenario: Delete driver
    Given Access token
    When Delete driver with id "00000000-0000-0001-0000-000000000001"
    Then Response status is 204
