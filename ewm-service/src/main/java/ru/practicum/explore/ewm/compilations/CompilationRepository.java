package ru.practicum.explore.ewm.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.eventsList e WHERE (:pinned is NULL OR c.pinned=:pinned)")
    List<Compilation> findAllWithConditions(@Param("pinned") Boolean pinned, OffsetBasedPageRequest pageRequest);

    @Query("SELECT c FROM Compilation c LEFT JOIN FETCH c.eventsList e WHERE c.id=:compId")
    Optional<Compilation> findByIdFull(@Param("compId") Long compId);
}
