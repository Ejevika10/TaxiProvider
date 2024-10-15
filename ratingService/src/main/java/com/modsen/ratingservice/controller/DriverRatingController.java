package com.modsen.ratingservice.controller;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import com.modsen.ratingservice.service.RatingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/driverratings")
@RequiredArgsConstructor
@Validated
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

    @GetMapping("/user/{id}")
    public PageDto<RatingResponseDto> getPageRatingsByUserId(@PathVariable @Min(0) Long id,
                                                             @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                             @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit) {
        return driverRatingService.getPageRatingsByUserId(id, offset, limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponseDto createRating(@Valid @RequestBody RatingRequestDto rideRequestDto) {
        return driverRatingService.addRating(rideRequestDto);
    }

    @PutMapping("/{id}")
    public RatingResponseDto updateRating(@PathVariable String id,
                                          @Valid @RequestBody RatingRequestDto ratingRequestDTO) {
        return driverRatingService.updateRating(id, ratingRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(@PathVariable String id) {
        driverRatingService.deleteRating(id);
    }
}
