package com.modsen.ratingservice.repository;

import com.modsen.ratingservice.model.DriverRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverRatingRepository extends MongoRepository<DriverRating, String> {
    boolean existsByIdAndDeletedIsFalse(String id);

    boolean existsByRideIdAndDeletedIsFalse(Long rideId);

    List<DriverRating> findAllByDeletedIsFalse();

    Page<DriverRating> findAllByDeletedIsFalse(Pageable pageable);

    Optional<DriverRating> findByIdAndDeletedIsFalse(String id);

    Optional<DriverRating> findByRideIdAndDeletedIsFalse(Long rideId);

    List<DriverRating> findAllByUserIdAndDeletedIsFalse(UUID userId);

    Page<DriverRating> findAllByUserIdAndDeletedIsFalse(UUID userId, Pageable pageable);

    List<DriverRating> findTop100ByUserIdAndDeletedIsFalse(UUID userId);

    List<DriverRating> findAllByRideIdIsInAndDeletedIsFalse(List<Long> rideIds);
}
