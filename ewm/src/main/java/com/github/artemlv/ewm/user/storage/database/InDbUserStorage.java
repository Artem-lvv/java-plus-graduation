package com.github.artemlv.ewm.user.storage.database;

import com.github.artemlv.ewm.exception.type.NotFoundException;
import com.github.artemlv.ewm.user.model.User;
import com.github.artemlv.ewm.user.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InDbUserStorage implements UserStorage {
    private static final String SIMPLE_NAME = User.class.getSimpleName();
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User save(User user) {
        final User userInStorage = userRepository.save(user);
        log.info("Save {} - {}", SIMPLE_NAME, userInStorage);
        return userInStorage;
    }

    @Override
    @Transactional
    public void deleteById(final long id) {
        userRepository.deleteById(id);
        log.info("Delete {} by id - {}", SIMPLE_NAME, id);
    }

    @Override
    public List<User> getAll(final List<Long> ids, final int from, final int size) {
        final PageRequest pageRequest = PageRequest.of(from, size);

        final List<User> users = ObjectUtils.isEmpty(ids) ? userRepository
                .findAll(pageRequest)
                .getContent() : userRepository.findAllByIdIn(ids, pageRequest);

        log.info("Getting {} from - {} size - {}", SIMPLE_NAME, from, size);
        return users;
    }

    @Override
    public Optional<User> getById(final long id) {
        final Optional<User> category = userRepository.findById(id);
        log.info("Get Optional<{}> by id - {}", SIMPLE_NAME, id);
        return category;
    }

    @Override
    public User getByIdOrElseThrow(final long id) {
        return getById(id).orElseThrow(() -> new NotFoundException(SIMPLE_NAME, id));
    }

    @Override
    public void existsByIdOrElseThrow(final long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(SIMPLE_NAME, id);
        }
    }
}
