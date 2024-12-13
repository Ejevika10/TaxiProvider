Feature: DriverRating API
  Scenario: Create driver rating
    Given Rating request dto
    """
        {
            "rideId": 1,
            "userId": 1,
            "rating": 1,
            "comment": "This is a comment"
        }
    """
    When Create driver rating
    Then Response status is 201
    And Response body contains Rating response dto
    """
        {
            "id": "6748eb64aec5be63e5293caf",
            "rideId": 1,
            "userId": 1,
            "rating": 1,
            "comment": "This is a comment"
        }
    """
  Scenario: Get page of driver ratings
    When Get page of driver ratings
    Then Response status is 200
    And Response body contains Page dto
    """
        {
          "pageNumber": 0,
          "pageSize": 5,
          "totalPages": 1,
          "totalElements": 2,
          "content": [
            {
                "id": "6748eb64aec5be63e5293caf",
                "rideId": 2,
                "userId": 1,
                "rating": 1,
                "comment": "This is a comment"
            },
            {
                "id": "675b7c7fae1ffb29763bab21",
                "rideId": 1,
                "userId": 1,
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get page of driver ratings by user id
    When Get page of driver ratings by user id 1
    Then Response status is 200
    And Response body contains Page dto
    """
        {
          "pageNumber": 0,
          "pageSize": 5,
          "totalPages": 1,
          "totalElements": 2,
          "content": [
            {
                "id": "6748eb64aec5be63e5293caf",
                "rideId": 2,
                "userId": 1,
                "rating": 1,
                "comment": "This is a comment"
            },
            {
                "id": "675b7c7fae1ffb29763bab21",
                "rideId": 1,
                "userId": 1,
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get driver rating by id
    When Get driver rating by id "675b7c7fae1ffb29763bab21"
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": "675b7c7fae1ffb29763bab21",
            "rideId": 2,
            "userId": 1,
            "rating": 1,
            "comment": "This is a comment"
        }
    """
  Scenario: Update driver rating
    Given Rating request dto
    """
        {
            "rideId": 2,
            "userId": 1,
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
    When Update driver rating with id "675b7c7fae1ffb29763bab21"
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": "675b7c7fae1ffb29763bab21",
            "rideId": 2,
            "userId": 1,
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
  Scenario: Delete driver rating
    When Delete driver rating with id "675b7c7fae1ffb29763bab21"
    Then Response status is 204
