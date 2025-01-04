package ru.yandex.practicum.category.user.storage.database;

import ru.yandex.practicum.category.user.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(final List<Long> ids, final PageRequest pageRequest);
}
