package ru.yandex.practicum.storage.database;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.state.State;

import java.util.List;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester(final long userId);

    int countByEventAndStatus(final long id, final State state);

    List<Request> findByRequesterAndEvent(final long userId, final long eventId);

    List<Request> findByIdInAndEvent(final Set<Long> ids, long eventId);

    List<Request> findByEvent(final long eventId);

    boolean existsByRequesterAndEvent(final long userId, final long eventId);
}
