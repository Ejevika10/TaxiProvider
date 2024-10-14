package com.modsen.ratingservice.repository;

import com.modsen.ratingservice.model.PassengerRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PassengerRatingRepository extends MongoRepository<PassengerRating, String> {
    boolean existsByIdAndDeletedIsFalse(String id);
    boolean existsByRideIdAndDeletedIsFalse(Long rideId);

    List<PassengerRating> findAllByDeletedIsFalse();
    Page<PassengerRating> findAllByDeletedIsFalse(Pageable pageable);
    Optional<PassengerRating> findByIdAndDeletedIsFalse(String id);
    Optional<PassengerRating> findByRideIdAndDeletedIsFalse(Long rideId);

    List<PassengerRating> findAllByUserIdAndDeletedIsFalse(Long userId);
    Page<PassengerRating> findAllByUserIdAndDeletedIsFalse(Long userId, Pageable pageable);
}
