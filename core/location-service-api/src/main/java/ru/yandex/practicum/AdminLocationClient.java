package ru.yandex.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.location.model.dto.CreateLocationDto;
import ru.yandex.practicum.location.model.dto.LocationDto;
import ru.yandex.practicum.location.model.dto.UpdateLocationDto;

@FeignClient(name = "${admin.location.service.name:LOCATION-SERVICE}", url = "${admin.locations.service.url}")
public interface AdminLocationClient {

    @PostMapping("/admin/locations")
    LocationDto create(@RequestBody @Valid CreateLocationDto createLocationDto);

    @GetMapping("/admin/locations/{locId}")
    LocationDto getById(@PathVariable @Positive long locId);

    @PatchMapping("/admin/locations/{locId}")
    LocationDto update(@RequestBody UpdateLocationDto updateLocationDto,
                       @PathVariable @Positive long locId);

    @DeleteMapping("/admin/locations/{locId}")
    void deleteById(@PathVariable long locId);

    @GetMapping("/admin/locations/lat/{lat}/lon/{lon}")
    LocationDto getByCoordinates(@PathVariable @Positive double lat,
                                  @PathVariable double lon);
}
