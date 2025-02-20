Feature: PassengerRating API
  Scenario: Create passenger rating
    Given Access token
    And Rating request dto
    """
        {
            "rideId": 1,
            "userId": "00000000-0000-0001-0000-000000000002",
            "rating": 1,
            "comment": "This is a comment"
        }
    """
    When Create passenger rating
    Then Response status is 201
    And Response body contains Rating response dto
    """
        {
            "id": "67af2045d38b5434a550ce2e",
            "rideId": 1,
            "userId": "00000000-0000-0001-0000-000000000002",
            "rating": 1,
            "comment": "This is a comment"
        }
    """
  Scenario: Get page of passenger ratings
    Given Access token
    When Get page of passenger ratings
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
                "id": "67af2045d38b5434a550ce2e",
                "rideId": 2,
                "userId": "00000000-0000-0001-0000-000000000002",
                "rating": 1,
                "comment": "This is a comment"
            },
            {
                "id": "67af2045d38b5434a550ce2e",
                "rideId": 1,
                "userId": "00000000-0000-0001-0000-000000000002",
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get page of passenger ratings by user id
    Given Access token
    When Get page of passenger ratings by user id "00000000-0000-0001-0000-000000000002"
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
                "id": "67af2045d38b5434a550ce2e",
                "rideId": 2,
                "userId": "00000000-0000-0001-0000-000000000002",
                "rating": 1,
                "comment": "This is a comment"
            },
            {
                "id": "67af2045d38b5434a550ce2e",
                "rideId": 1,
                "userId": "00000000-0000-0001-0000-000000000002",
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get passenger rating by id
    Given Access token
    When Get passenger rating by id "67af2045d38b5434a550ce2e"
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": "67af2045d38b5434a550ce2e",
            "rideId": 2,
            "userId": "00000000-0000-0001-0000-000000000002",
            "rating": 1,
            "comment": "This is a comment"
        }
    """
  Scenario: Update passenger rating
    Given Access token
    And Rating request dto
    """
        {
            "rideId": 2,
            "userId": "00000000-0000-0001-0000-000000000002",
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
    When Update passenger rating with id "67af2045d38b5434a550ce2e"
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": "67af2045d38b5434a550ce2e",
            "rideId": 2,
            "userId": "00000000-0000-0001-0000-000000000002",
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
  Scenario: Delete passenger rating
    Given Access token
    When Delete passenger rating with id "67af2045d38b5434a550ce2e"
    Then Response status is 204
