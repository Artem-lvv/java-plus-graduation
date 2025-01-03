package ru.yandex.practicum.category.request.storage.database;


import ru.yandex.practicum.category.exception.type.ConflictException;
import ru.yandex.practicum.category.exception.type.NotFoundException;
import ru.yandex.practicum.category.request.model.Request;
import ru.yandex.practicum.category.request.storage.RequestStorage;
import ru.yandex.practicum.category.state.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InDbRequestStorage implements RequestStorage {
    private static final String SIMPLE_NAME = Request.class.getSimpleName();
    private final RequestRepository requestRepository;

    @Override
    public int countByEventIdAndStatus(final long id, final State state) {
        final int count = requestRepository.countByEventIdAndStatus(id, state);
        log.info("countByEventIdAndStatus count: {}", count);
        return count;
    }

    @Override
    @Transactional
    public Request save(Request request) {
        final Request requestInStorage = requestRepository.save(request);
        log.info("Save {} - {}", SIMPLE_NAME, requestInStorage);
        return requestInStorage;
    }

    @Override
    public List<Request> findAllByRequesterId(final long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Getting all {} : {}", SIMPLE_NAME, requests);
        return requests;
    }

    @Override
    public Optional<Request> getById(final long id) {
        final Optional<Request> request = requestRepository.findById(id);
        log.info("Get Optional<{}> by id - {}", SIMPLE_NAME, id);
        return request;
    }

    @Override
    public Request getByIdOrElseThrow(final long id) {
        return getById(id).orElseThrow(() -> new NotFoundException(SIMPLE_NAME, id));
    }

    @Override
    public List<Request> findAllByRequesterIdAndEventId(final long userId, final long eventId) {
        final List<Request> requests = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        log.info("Getting all by requester id and event id {} : {}", SIMPLE_NAME, requests);
        return requests;
    }

    @Override
    public List<Request> findAllByIdInAndEventId(final Set<Long> ids, final long eventId) {
        final List<Request> requests = requestRepository.findByIdInAndEventId(ids, eventId);
        log.info("Getting all by Ids {} : {}", SIMPLE_NAME, requests);
        return requests;
    }

    @Override
    @Transactional
    public void saveAll(final List<Request> requests) {
        log.info("Save All {} - {}", SIMPLE_NAME, requests);
        requestRepository.saveAll(requests);
    }

    @Override
    public List<Request> findAllByEventId(long eventId) {
        final List<Request> requests = requestRepository.findByEventId(eventId);
        log.info("Getting all by event id {} : {}", SIMPLE_NAME, requests);
        return requests;
    }

    @Override
    public void ifExistsByRequesterIdAndEventIdThenThrow(long userId, long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(SIMPLE_NAME.formatted(" cannot re-apply for the same event : %d", eventId));
        }
    }
}
