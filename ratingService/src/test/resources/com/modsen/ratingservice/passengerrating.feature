Feature: PassengerRating API
  Scenario: Create passenger rating
    Given Rating request dto
    """
        {
            "rideId": 1,
            "userId": 1,
            "rating": 1,
            "comment": "This is a comment"
        }
    """
    When Create passenger rating
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
                "id": "6748eb64aec5be63e5293caf",
                "rideId": 1,
                "userId": 1,
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get page of passenger ratings by user id
    When Get page of passenger ratings by user id 1
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
                "id": "6748eb64aec5be63e5293caf",
                "rideId": 1,
                "userId": 1,
                "rating": 1,
                "comment": "This is a comment"
            }
          ]
        }
    """
  Scenario: Get passenger rating by id
    When Get passenger rating by id "6748efbbaec5be63e5293cb1"
    Then Response status is 200
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
  Scenario: Update passenger rating
    Given Rating request dto
    """
        {
            "rideId": 1,
            "userId": 1,
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
    When Update passenger rating with id "6748efbbaec5be63e5293cb1"
    Then Response status is 200
    And Response body contains Rating response dto
    """
        {
            "id": "6748eb64aec5be63e5293caf",
            "rideId": 1,
            "userId": 1,
            "rating": 5,
            "comment": "This is a new comment"
        }
    """
  Scenario: Delete passenger rating
    When Delete passenger rating with id "6748efbbaec5be63e5293cb1"
    Then Response status is 204
