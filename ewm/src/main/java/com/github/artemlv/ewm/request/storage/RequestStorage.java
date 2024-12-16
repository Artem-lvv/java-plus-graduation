package com.github.artemlv.ewm.request.storage;

import com.github.artemlv.ewm.request.model.Request;
import com.github.artemlv.ewm.state.State;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestStorage {
    int countByEventIdAndStatus(final long id, final State state);

    Request save(final Request request);

    List<Request> findAllByRequesterId(final long userId);

    Optional<Request> getById(final long id);

    Request getByIdOrElseThrow(final long id);

    List<Request> findAllByRequesterIdAndEventId(final long userId, final long eventId);

    List<Request> findAllByIdInAndEventId(final Set<Long> ids, final long eventId);

    void saveAll(List<Request> requests);

    List<Request> findAllByEventId(final long eventId);

    void ifExistsByRequesterIdAndEventIdThenThrow(final long userId, final long eventId);
}
