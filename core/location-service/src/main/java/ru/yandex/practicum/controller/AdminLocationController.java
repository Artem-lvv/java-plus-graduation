package ru.yandex.practicum.controller;

import jakarta.validation.constraints.PositiveOrZero;
import ru.yandex.practicum.location.model.dto.CreateLocationDto;
import ru.yandex.practicum.location.model.dto.LocationDto;
import ru.yandex.practicum.location.model.dto.UpdateLocationDto;
import ru.yandex.practicum.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.validation.ConstraintNotZero;

import javax.xml.stream.Location;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {
    private static final String SIMPLE_NAME = Location.class.getSimpleName();
    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto create(@RequestBody @Valid final CreateLocationDto createLocationDto) {
        log.info("Request to create a {} - {}", SIMPLE_NAME, createLocationDto);
        return locationService.create(createLocationDto);
    }

    @GetMapping("/{locId}")
    public LocationDto getById(@PathVariable @Positive final long locId) {
        log.info("Request to obtain a {} by id - {}", SIMPLE_NAME, locId);
        return locationService.getById(locId);
    }

    @PatchMapping("/{locId}")
    public LocationDto update(@RequestBody final UpdateLocationDto updateLocationDto,
                              @PathVariable @Positive final long locId) {
        log.info("Request to update {} by id - {} - {}", SIMPLE_NAME, locId, updateLocationDto);
        return locationService.updateById(locId, updateLocationDto);
    }

    @DeleteMapping("/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable final long locId) {
        log.info("Request to delete a {} by id - {}", SIMPLE_NAME, locId);
        locationService.deleteById(locId);
    }

    @GetMapping("/lat/{lat}/lon/{lon}")
    public LocationDto getByCoordinates(@PathVariable final double lat, @PathVariable double lon) {
        log.info("Request to location {} by lat {} - lon {}", SIMPLE_NAME, lat, lon);
        return locationService.getByCoordinates(lat, lon);
    }

    @GetMapping
    public List<LocationDto> getAllByCoordinates(@RequestParam @ConstraintNotZero final Double lat,
                                                 @RequestParam @ConstraintNotZero final Double lon,
                                                 @RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero final double radius) {
        log.info("Admin locations requested by {} - lat {} - lon {} - radius {}", SIMPLE_NAME, lat, lon, radius);
        return locationService.getAllByCoordinates(lat, lon, radius);
    }

    @GetMapping("/ids")
    public List<LocationDto> getAllByIds(@RequestParam List<Long> ids) {
        log.info("Admin locations requested by {} - ids {} ", SIMPLE_NAME, ids);
        return locationService.getAllByIds(ids);
    }
}
