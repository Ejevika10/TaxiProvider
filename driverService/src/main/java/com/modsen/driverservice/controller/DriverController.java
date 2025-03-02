package com.modsen.driverservice.controller;

import com.modsen.driverservice.dto.DriverCreateRequestDto;
import com.modsen.driverservice.dto.DriverResponseDto;
import com.modsen.driverservice.dto.DriverUpdateRequestDto;
import com.modsen.driverservice.dto.PageDto;
import com.modsen.driverservice.service.DriverService;
import com.modsen.driverservice.service.Impl.StorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.modsen.driverservice.util.AppConstants.UUID_REGEXP;

@RestController
@Validated
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class DriverController implements DriverEndpoints {
    private final DriverService driverService;
    private final StorageService storageService;

    @Override
    @GetMapping
    public PageDto<DriverResponseDto> getPageDrivers(@RequestParam (defaultValue = "0") @Min(0) Integer offset,
                                                     @RequestParam (defaultValue = "5") @Min(1) @Max(20) Integer limit) {
        return driverService.getPageDrivers(offset, limit);
    }

    @Override
    @GetMapping("/{id}")
    public DriverResponseDto getDriverById(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                               String id) {
        return driverService.getDriverById(UUID.fromString(id));
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponseDto createDriver(@Valid @RequestBody DriverCreateRequestDto driverRequestDTO) {
        return driverService.createDriver(driverRequestDTO);
    }

    @Override
    @PutMapping("/{id}")
    public DriverResponseDto updateDriver(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                              String id,
                                          @Valid @RequestBody DriverUpdateRequestDto driverRequestDTO) {
        return driverService.updateDriver(UUID.fromString(id), driverRequestDTO);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}")
                                 String id) {
        driverService.deleteDriver(UUID.fromString(id));
    }

    @Override
    @PostMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addAvatar(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id,
                                            @RequestParam MultipartFile file) {
        DriverResponseDto driver = driverService.getDriverById(UUID.fromString(id));
        String fileName = storageService.uploadImage(file, id);
        return new ResponseEntity<>("File uploaded successfully: " + fileName, HttpStatus.OK);
    }

    @Override
    @GetMapping(path = "/{id}/avatar")
    public ResponseEntity<?> downloadAvatar(@PathVariable @Pattern(regexp = UUID_REGEXP, message = "{uuid.invalid}") String id) {
        DriverResponseDto driver = driverService.getDriverById(UUID.fromString(id));
        Resource file = storageService.downloadFile(id);
        String contentType = storageService.getFileContentType(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + "\"")
                .body(file);
    }
}
