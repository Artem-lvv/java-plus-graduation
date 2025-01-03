package ru.yandex.practicum.category.request.storage.database;

import ru.yandex.practicum.category.request.model.Request;
import ru.yandex.practicum.category.state.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(final long userId);

    int countByEventIdAndStatus(final long id, final State state);

    List<Request> findByRequesterIdAndEventId(final long userId, final long eventId);

    List<Request> findByIdInAndEventId(final Set<Long> ids, long eventId);

    List<Request> findByEventId(final long eventId);

    boolean existsByRequesterIdAndEventId(final long userId, final long eventId);
}
