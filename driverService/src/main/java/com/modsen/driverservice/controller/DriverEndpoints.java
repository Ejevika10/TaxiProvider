package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.AvatarDto;
import com.modsen.driverservice.dto.DriverCreateRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.DriverUpdateRequestDto;
import com.modsen.driverservice.dto.PageDto;
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

import static com.modsen.driverservice.util.AppConstants.UUID_REGEXP;

@Validated
@Tag(
        name = "Driver controller",
        description = """
                The driver controller handles operations with drivers:
                update or delete existing driver,
                manage driver avatars,
                get driver by id or get collection of drivers.
                """
)
public interface DriverEndpoints {
    @Operation(
            summary = "Get drivers",
            description = "Allows users to obtain a collection of drivers with pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid")
    })
    PageDto<DriverResponseDto> getPageDrivers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                              @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit);

    @Operation(
            summary = "Get driver by id",
            description = "Allows users to get a driver by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist")
    })
    DriverResponseDto getDriverById(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id);

    @Hidden
    DriverResponseDto createDriver(@Valid @RequestBody DriverCreateRequestDto driverRequestDTO);

    @Operation(
            summary = "Update driver by id",
            description = """
                    Allows driver to update its profile.
                    When request complete, a driver profile will be updated
                    and the user will receive a driver info.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "409", description = "Your request has conflicts"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable")
    })
    DriverResponseDto updateDriver(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id,
                                   @Valid @RequestBody DriverUpdateRequestDto driverRequestDTO);

    @Operation(
            summary = "Delete driver by id",
            description = """
                    Allows driver to delete its profile by id.
                    When request complete, the driver will be deleted.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Request completed"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "503", description = "Keycloak service unavailable")
    })
    void deleteDriver(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id);

    @Operation(
            summary = "Add avatar for driver by id",
            description = """
                    Allows driver to add avatar to its profile by id.
                    When request complete, an avatar will be saved for driver.
                    When driver already has an avatar, it will be overwritten.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "503", description = "Minio service unavailable")
    })
    AvatarDto addAvatar(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id,
                        @RequestParam MultipartFile file);

    @Operation(
            summary = "Download driver's avatar by id",
            description = """
                    Allows user to get driver's avatar by id.
                    When request complete, an avatar will be saved as image.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request completed"),
            @ApiResponse(responseCode = "400", description = "Your request is invalid"),
            @ApiResponse(responseCode = "404", description = "Driver with this id doesn't exist"),
            @ApiResponse(responseCode = "503", description = "Minio service unavailable")
    })
    ResponseEntity<Resource> downloadAvatar(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id);

}
