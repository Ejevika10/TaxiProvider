Feature: DriverRating API
  Scenario: Create driver rating
    Given Access token
    And Rating request dto
    """
        {
            "rideId": 1,
            "userId": "00000000-0000-0001-0000-000000000001",
            "rating": 1,
            "comment": "This is a comment"
        }
    """
    When Create driver rating
    Then Response status is 201
    And Response body contains Rating response dto
    """
        {
            "id": "67af2043d38b5434a550ce2d",
            "rideId": 1,
            "userId": "00000000-0000-0001-0000-000000000001",
            "rating": 1,
            "comment": "This is a comment"
        }
    """
  Scenario: Get page of driver ratings
    Given Access token
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
                "id": "67af2043d38b5434a550ce2d",
                "rideId": 2,
                "userId": "00000000-0000-0001-0000-000000000001",
                "rating": 1,
                "comment": "This is a comment"
            },
            {
                "id": "67af2043d38b5434a550ce2d",
                "rideId": 1,
                "userId": "00000000-0000-0001-0000-000000000001",
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get page of driver ratings by user id
    Given Access token
    When Get page of driver ratings by user id "00000000-0000-0001-0000-000000000001"
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
                "id": "67af2043d38b5434a550ce2d",
                "rideId": 2,
                "userId": "00000000-0000-0001-0000-000000000001",
                "rating": 1,
                "comment": "This is a comment"
            },
            {
                "id": "67af2043d38b5434a550ce2d",
                "rideId": 1,
                "userId": "00000000-0000-0001-0000-000000000001",
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get driver rating by id
    Given Access token
    When Get driver rating by id "67af2043d38b5434a550ce2d"
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": "67af2043d38b5434a550ce2d",
            "rideId": 2,
            "userId": "00000000-0000-0001-0000-000000000001",
            "rating": 1,
            "comment": "This is a comment"
        }
    """
  Scenario: Update driver rating
    Given Access token
    And Rating request dto
    """
        {
            "rideId": 2,
            "userId": "00000000-0000-0001-0000-000000000001",
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
    When Update driver rating with id "67af2043d38b5434a550ce2d"
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": "67af2043d38b5434a550ce2d",
            "rideId": 2,
            "userId": "00000000-0000-0001-0000-000000000001",
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
  Scenario: Delete driver rating
    Given Access token
    When Delete driver rating with id "67af2043d38b5434a550ce2d"
    Then Response status is 204
