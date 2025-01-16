package ru.yandex.practicum.storage.database;


import ru.yandex.practicum.exception.type.ConflictException;
import ru.yandex.practicum.exception.type.NotFoundException;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.state.State;
import ru.yandex.practicum.storage.RequestStorage;
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
        final int count = requestRepository.countByEventAndStatus(id, state);
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
        List<Request> requests = requestRepository.findAllByRequester(userId);
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
        final List<Request> requests = requestRepository.findByRequesterAndEvent(userId, eventId);
        log.info("Getting all by requester id and event id {} : {}", SIMPLE_NAME, requests);
        return requests;
    }

    @Override
    public List<Request> findAllByIdInAndEventId(final Set<Long> ids, final long eventId) {
        final List<Request> requests = requestRepository.findByIdInAndEvent(ids, eventId);
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
        final List<Request> requests = requestRepository.findByEvent(eventId);
        log.info("Getting all by event id {} : {}", SIMPLE_NAME, requests);
        return requests;
    }

    @Override
    public void ifExistsByRequesterIdAndEventIdThenThrow(long userId, long eventId) {
        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ConflictException(SIMPLE_NAME.formatted(" cannot re-apply for the same event : %d", eventId));
        }
    }
}
