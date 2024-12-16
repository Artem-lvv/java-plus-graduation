package com.github.artemlv.ewm.compilation.storage.database;

import com.github.artemlv.ewm.compilation.model.Compilation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinnedIs(final boolean isPinned, final PageRequest pageRequest);
}
