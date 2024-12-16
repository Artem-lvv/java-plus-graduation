package com.github.artemlv.ewm.user.storage.database;

import com.github.artemlv.ewm.user.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(final List<Long> ids, final PageRequest pageRequest);
}
