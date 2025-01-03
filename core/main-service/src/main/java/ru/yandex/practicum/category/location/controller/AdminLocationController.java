package ru.yandex.practicum.category.location.controller;

import ru.yandex.practicum.category.location.model.dto.CreateLocationDto;
import ru.yandex.practicum.category.location.model.dto.LocationDto;
import ru.yandex.practicum.category.location.model.dto.UpdateLocationDto;
import ru.yandex.practicum.category.location.service.LocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.Location;

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
}
