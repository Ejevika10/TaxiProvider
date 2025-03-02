package com.modsen.ratingservice.controller;

import com.modsen.ratingservice.dto.PageDto;
import com.modsen.ratingservice.dto.RatingRequestDto;
import com.modsen.ratingservice.dto.RatingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static com.modsen.ratingservice.util.AppConstants.UUID_REGEXP;

@Validated
@Tag(
        name = "Driver rating controller",
        description = """
                The driver rating controller provides functionality to work with ratings for drivers.
                Passengers can add rating, update or delete existing rating,
                get driver rating by rating id or get collection of ratings by driver id.
                """
)
public interface DriverRatingEndpoints {
    @Operation(
            summary = "Get driver rating by id",
            description = "Allows users to get a driver rating by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Rating with this id doesn't exist")
    })
    RatingResponseDto getRating(@PathVariable String id);

    @Operation(
            summary = "Get driver ratings",
            description = "Allows users to obtain a collection of driver ratings with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid")
    })
    PageDto<RatingResponseDto> getPageRatings(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                              @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Get driver ratings by driver id",
            description = "Allows users to obtain a collection of driver ratings by driver id with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist")
    })
    PageDto<RatingResponseDto> getPageRatingsByUserId(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String userId,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                      @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Add rating",
            description = """
                    Allows passenger to add a new rating for a ride.
                    When request complete, a rating will be added
                    and the driver average rating will be updated.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Ride with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    RatingResponseDto createRating(@Valid @RequestBody RatingRequestDto ratingRequestDto,
                                   @RequestHeader("Authorization") String authorizationToken);

    @Operation(
            summary = "Update rating by id",
            description = """
                    Allows passenger to update an existing rating for a ride.
                    When request complete, a rating will be updated
                    and the driver average rating will be updated.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Rating with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    RatingResponseDto updateRating(@PathVariable String id,
                                   @Valid @RequestBody RatingRequestDto ratingRequestDTO,
                                   @RequestHeader("Authorization") String authorizationToken);

    @Operation(
            summary = "Delete rating by id",
            description = """
                    Allows passenger to delete an existing rating for a ride.
                    When request complete, a rating will be deleted
                    and the driver average rating will be updated.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Rating with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts")
    })
    void deleteRating(@PathVariable String id);

}
