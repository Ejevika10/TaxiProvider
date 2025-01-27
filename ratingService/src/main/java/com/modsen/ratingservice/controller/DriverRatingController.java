package com.modsen.ratingservice.controller;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.service.RatingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/driverratings")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "JWT")
@Slf4j
public class DriverRatingController {

    @Qualifier("DriverRatingServiceImpl")
    private final RatingService driverRatingService;

    @GetMapping("/{id}")
    public RatingResponseDto getRating(@PathVariable String id) {
        return driverRatingService.getRatingById(id);
    }

    @GetMapping
    public PageDto<RatingResponseDto> getPageRatings(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                     @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit) {
        return driverRatingService.getPageRatings(offset, limit);
    }

    @GetMapping("/user/{userId}")
    public PageDto<RatingResponseDto> getPageRatingsByUserId(@PathVariable String userId,
                                                             @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                             @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit) {
        return driverRatingService.getPageRatingsByUserId(UUID.fromString(userId), offset, limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponseDto createRating(@Valid @RequestBody RatingRequestDto rideRequestDto,
                                          @RequestHeader("Authorization") String authorizationToken) {
        log.info("add rating controller");
        return driverRatingService.addRating(rideRequestDto, authorizationToken);
    }

    @PutMapping("/{id}")
    public RatingResponseDto updateRating(@PathVariable String id,
                                          @Valid @RequestBody RatingRequestDto ratingRequestDTO,
                                          @RequestHeader("Authorization") String authorizationToken) {
        return driverRatingService.updateRating(id, ratingRequestDTO, authorizationToken);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(@PathVariable String id) {
        driverRatingService.deleteRating(id);
    }
}
