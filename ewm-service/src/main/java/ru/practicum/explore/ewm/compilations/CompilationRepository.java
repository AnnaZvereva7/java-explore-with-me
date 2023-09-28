package ru.practicum.explore.ewm.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation c WHERE (:pinned is NULL OR c.pinned=:pinned)")
    List<Compilation> findAllWithConditions(@Param("pinned") Boolean pinned, OffsetBasedPageRequest pageRequest);
}
