Feature: Car API
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

  Scenario: Create car
  Given Access token
  And Car request dto
  """
          {
          "color": "red",
          "model": "sedan",
          "brand": "audi",
          "number": "12345",
          "driverId": "00000000-0000-0001-0000-000000000001"
          }
      """
  When Create car
  Then Response status is 201
  And Response body contains Car response dto
  """
          {
            "id": 1,
            "color": "red",
            "model": "sedan",
            "brand": "audi",
            "number": "12345",
            "driver": {
                "id": "00000000-0000-0001-0000-000000000001",
                "name": "Driver",
                "email": "driver@email.com",
                "phone": "71234567890",
                "rating": 0.0
            }
          }
      """
  Scenario: Get page of cars
  Given Access token
  When Get page of cars
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
                "color": "red",
                "model": "sedan",
                "brand": "audi",
                "number": "12345",
                "driver": {
                    "id": "00000000-0000-0001-0000-000000000001",
                    "name": "Driver",
                    "email": "driver@email.com",
                    "phone": "71234567890",
                    "rating": 0.0
                }
              }
            ]
          }
      """
  Scenario: Get page of cars by driver id
  Given Access token
  When Get page of cars by driver id "00000000-0000-0001-0000-000000000001"
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
                "color": "red",
                "model": "sedan",
                "brand": "audi",
                "number": "12345",
                "driver": {
                    "id": "00000000-0000-0001-0000-000000000001",
                    "name": "Driver",
                    "email": "driver@email.com",
                    "phone": "71234567890",
                    "rating": 0.0
                }
              }
            ]
          }
      """

  Scenario: Get car by id
  Given Access token
  When Get car by id 1
  Then Response status is 200
  And Response body contains Car response dto
  """
          {
            "id": 1,
            "color": "red",
            "model": "sedan",
            "brand": "audi",
            "number": "12345",
            "driver": {
                "id": "00000000-0000-0001-0000-000000000001",
                "name": "Driver",
                "email": "driver@email.com",
                "phone": "71234567890",
                "rating": 0.0
            }
          }
      """
  Scenario: Update car
  Given Access token
  Given Car request dto
  """
          {
          "color": "blue",
          "model": "new_model",
          "brand": "bmw",
          "number": "12345",
          "driverId": "00000000-0000-0001-0000-000000000001"
          }
      """
  When Update car with id 1
  Then Response status is 200
  And Response body contains Car response dto
  """
          {
            "id": 1,
            "color": "blue",
            "model": "new_model",
            "brand": "bmw",
            "number": "12345",
            "driver": {
                "id": "00000000-0000-0001-0000-000000000001",
                "name": "Driver",
                "email": "driver@email.com",
                "phone": "71234567890",
                "rating": 0.0
            }
          }
      """
  Scenario: Delete car
  Given Access token
  When Delete car with id 1
  Then Response status is 204

  Scenario: Delete driver
  Given Access token
  When Delete driver with id "00000000-0000-0001-0000-000000000001"
  Then Response status is 204
