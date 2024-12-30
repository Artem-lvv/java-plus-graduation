package ru.yandex.practicum.category.location.service;

import ru.yandex.practicum.category.location.model.dto.CreateLocationDto;
import ru.yandex.practicum.category.location.model.dto.LocationDto;
import ru.yandex.practicum.category.location.model.dto.LocationLatAndLonDto;
import ru.yandex.practicum.category.location.model.dto.UpdateLocationDto;

import java.util.List;


public interface LocationService {

    LocationDto create(CreateLocationDto createLocationDto);

    LocationDto getByCoordinatesOrElseCreate(final LocationLatAndLonDto locationLatAndLonDto);

    LocationDto getById(final long id);

    LocationDto updateById(final long id, final UpdateLocationDto updateLocationDto);

    void deleteById(final long id);

    List<LocationDto> getAll(final String text, final int from, final int size);

}