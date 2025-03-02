package com.modsen.passengerservice.controller;

import com.modsen.passengerservice.dto.AvatarDto;
import com.modsen.passengerservice.dto.PageDto;
import com.modsen.passengerservice.dto.PassengerCreateRequestDto;
import com.modsen.passengerservice.dto.PassengerResponseDto;
import com.modsen.passengerservice.dto.PassengerUpdateRequestDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static com.modsen.passengerservice.util.AppConstants.UUID_REGEXP;

@Validated
@Tag(
        name = "Passenger controller",
        description = """
                The passenger controller handles operations with passengers:
                update or delete existing passenger,
                manage passengers avatars,
                get passenger by id or get collection of passengers.
                """
)
public interface PassengerEndpoints {
    @Operation(
            summary = "Get passengers",
            description = "Allows users to obtain a collection of passengers with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid")
    })
    PageDto<PassengerResponseDto> getPagePassengers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                    @RequestParam (defaultValue = "5")  @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Get passenger by id",
            description = "Allows users to get a passenger by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist")
    })
    PassengerResponseDto getPassenger(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id);

    @Hidden
    PassengerResponseDto createPassenger(@Valid @RequestBody PassengerCreateRequestDto passengerRequestDTO);

    @Operation(
            summary = "Update passenger by id",
            description = """
                    Allows passenger to update its profile.
                    When request complete, a passenger profile will be updated
                    and the user will receive a passenger info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable")
    })
    PassengerResponseDto updatePassenger(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id,
                                                @Valid @RequestBody PassengerUpdateRequestDto passengerRequestDTO);

    @Operation(
            summary = "Delete passenger by id",
            description = """
                    Allows passenger to delete its profile by id.
                    When request complete, the passenger will be deleted.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Request completed"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable")
    })
    void deletePassenger(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id);

    @Operation(
            summary = "Add avatar for passenger by id",
            description = """
                    Allows passenger to add avatar to its profile by id.
                    When request complete, an avatar will be saved for passenger.
                    When passenger already has an avatar, it will be overwritten.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist"),
            @ApiResponse(responseCode = "503", description = "Minio service unavailable")
    })
    AvatarDto addAvatar(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id,
                        @RequestParam MultipartFile file);

    @Operation(
            summary = "Download passenger's avatar by id",
            description = """
                    Allows user to get passenger's avatar by id.
                    When request complete, an avatar will be saved as image.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Passenger with this id doesn't exist"),
            @ApiResponse(responseCode = "503", description = "Minio service unavailable")
    })
    ResponseEntity<Resource> downloadAvatar(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id);

}
