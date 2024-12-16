package com.github.artemlv.ewm.compilation.storage.database;

import com.github.artemlv.ewm.compilation.model.Compilation;
import com.github.artemlv.ewm.compilation.storage.CompilationStorage;
import com.github.artemlv.ewm.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InDbCompilationStorage implements CompilationStorage {
    private static final String SIMPLE_NAME = Compilation.class.getSimpleName();
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation getByIdOrElseThrow(final long id) {
        return getById(id).orElseThrow(() -> new NotFoundException(SIMPLE_NAME, id));
    }

    @Override
    public Optional<Compilation> getById(final long id) {
        final Optional<Compilation> compilation = compilationRepository.findById(id);
        log.info("Get Optional<{}> by id - {}", SIMPLE_NAME, id);
        return compilation;
    }

    @Override
    @Transactional
    public Compilation save(final Compilation compilation) {
        final Compilation compilationInStorage = compilationRepository.save(compilation);
        log.info("Save {} - {}", SIMPLE_NAME, compilationInStorage);
        return compilationInStorage;
    }

    @Override
    public void existsByIdOrElseThrow(final long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(SIMPLE_NAME, id);
        }
    }

    @Override
    @Transactional
    public void deleteById(final long id) {
        compilationRepository.deleteById(id);
        log.info("Delete {} by id - {}", SIMPLE_NAME, id);

    }

    @Override
    public List<Compilation> findAllByPinnedIs(final boolean pinned, final PageRequest page) {
        final List<Compilation> compilations = compilationRepository.findAllByPinnedIs(pinned, page);
        log.info("Getting All {} from - {} size - {} pinned - {}", SIMPLE_NAME, page.getPageNumber(),
                page.getPageSize(), pinned);
        return compilations;
    }

    @Override
    public List<Compilation> findAll(final PageRequest page) {
        final List<Compilation> compilations = compilationRepository.findAll(page).getContent();
        log.info("Getting All {} from - {} size - {}", SIMPLE_NAME, page.getPageNumber(), page.getPageSize());
        return compilations;
    }
}
