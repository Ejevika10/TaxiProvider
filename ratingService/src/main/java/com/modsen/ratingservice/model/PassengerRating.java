package com.modsen.ratingservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "passenger_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRating {
    @Id
    private String id;

    @Indexed(unique=true)
    private Long rideId;

    private Long userId;

    private Integer rating;

    private String comment;

    private Boolean deleted;
}
