package com.modsen.ratingservice.controller;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.service.RatingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
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

import static com.modsen.ratingservice.util.AppConstants.UUID_REGEXP;

@RestController
@RequestMapping("/api/v1/passengerratings")
@RequiredArgsConstructor
@Validated
public class PassengerRatingController implements PassengerRatingEndpoints {

    @Qualifier("PassengerRatingServiceImpl")
    private final RatingService passengerRatingService;

    @Override
    @GetMapping("/{id}")
    public RatingResponseDto getRating(@PathVariable String id) {
        return passengerRatingService.getRatingById(id);
    }

    @Override
    @GetMapping
    public PageDto<RatingResponseDto> getPageRatings(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                     @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit) {
        return passengerRatingService.getPageRatings(offset, limit);
    }

    @Override
    @GetMapping("/user/{userId}")
    public PageDto<RatingResponseDto> getPageRatingsByUserId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                                                 String userId,
                                                             @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                             @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit) {
        return passengerRatingService.getPageRatingsByUserId(UUID.fromString(userId), offset, limit);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponseDto createRating(@Valid @RequestBody RatingRequestDto rideRequestDto,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken) {
        return passengerRatingService.addRating(rideRequestDto, authorizationToken);
    }

    @Override
    @PutMapping("/{id}")
    public RatingResponseDto updateRating(@PathVariable String id,
                                          @Valid @RequestBody RatingRequestDto ratingRequestDTO,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationToken) {
        return passengerRatingService.updateRating(id, ratingRequestDTO, authorizationToken);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(@PathVariable String id) {
        passengerRatingService.deleteRating(id);
    }
}

