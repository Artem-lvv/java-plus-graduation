package ru.yandex.practicum.category.event.storage.database;

import ru.yandex.practicum.category.event.model.Event;
import ru.yandex.practicum.category.event.storage.EventStorage;
import ru.yandex.practicum.category.exception.type.NotFoundException;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InDbEventStorage implements EventStorage {
    private static final String SIMPLE_NAME = Event.class.getSimpleName();
    private final EventRepository eventRepository;

    @Override
    public List<Event> findAll(final BooleanExpression predicate, final PageRequest pageRequest) {
        final List<Event> events = eventRepository.findAll(predicate, pageRequest).toList();
        log.info("Getting all {} with predicate : [{}] : {}", SIMPLE_NAME, predicate, events);
        return events;
    }

    @Override
    public Optional<Event> getById(final long id) {
        final Optional<Event> event = eventRepository.findById(id);
        log.info("Get Optional<{}> by id - {}", SIMPLE_NAME, id);
        return event;
    }

    @Override
    public Event getByIdOrElseThrow(final long id) {
        return getById(id).orElseThrow(() -> new NotFoundException(SIMPLE_NAME, id));
    }

    @Override
    public List<Event> findAllByInitiatorId(final long userId, final PageRequest pageRequest) {
        final List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest);
        log.info("Getting all by initiator {} : {}", SIMPLE_NAME, events);
        return events;
    }

    @Override
    public List<Event> findAllEventsByLocation(final double lat, final double lon) {
        final List<Event> locations = eventRepository.findByLocationLatAndLocationLon(lat, lon);
        log.info("Getting a list of events by location coordinates - {}, {}, {}", SIMPLE_NAME, lat, lon);
        return locations;
    }

    @Override
    public void existsByIdOrElseThrow(final long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(SIMPLE_NAME, id);
        }
    }

    @Override
    public List<Event> findAll(final Specification<Event> spec, final PageRequest pageRequest) {
        final List<Event> events = eventRepository.findAll(spec, pageRequest).toList();
        log.info("Getting all {} with specification : [{}] : {}", SIMPLE_NAME, spec, events);
        return events;
    }

    @Override
    public List<Event> findAllById(final Set<Long> events) {
        final List<Event> eventsInStorage = eventRepository.findAllById(events);
        log.info("Getting all {} : by Ids {}", SIMPLE_NAME, eventsInStorage);
        return eventsInStorage;
    }

    @Override
    public List<Event> findByCategoryId(long id) {
        final List<Event> events = eventRepository.findByCategoryId(id);
        log.info("Getting all {} : by category id {}", SIMPLE_NAME, id);
        return events;
    }

    @Override
    public List<Event> findAllByLocationAndRadius(final double lat, final double lon, final double radius) {
        final List<Event> locations = eventRepository.findByLocationLatAndLocationLonAndLocationRadius(lat, lon, radius);
        log.info("Getting a list of events by location coordinates - {}, {}, {}, {}", SIMPLE_NAME, lat, lon, radius);
        return locations;
    }

    @Override
    @Transactional
    public Event save(Event event) {
        final Event eventInStorage = eventRepository.save(event);
        log.info("Save {} - {}", SIMPLE_NAME, eventInStorage);
        return eventInStorage;
    }

    @Override
    public void saveAll(final List<Event> lists) {
        log.info("Save All {} - {}", SIMPLE_NAME, lists);
        eventRepository.saveAll(lists);
    }
}
