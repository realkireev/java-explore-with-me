package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitAggregatedDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT NEW ru.practicum.dto.HitAggregatedDto(h.app, h.uri, COUNT(h)) FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end GROUP BY h.app, h.uri ORDER BY COUNT(h) DESC")
    List<HitAggregatedDto> countAllBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT NEW ru.practicum.dto.HitAggregatedDto(h.app, h.uri, COUNT(h)) FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN (:uris) GROUP BY h.app, h.uri ORDER BY COUNT(h) DESC")
    List<HitAggregatedDto> countAllByUrisBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                                 @Param("uris") List<String> uris);

    @Query(nativeQuery = true, value = "SELECT app, uri, COUNT(*) FROM (SELECT DISTINCT app, uri, ip FROM hit " +
            "WHERE timestamp BETWEEN :startDate AND :endDate) AS q GROUP BY app, uri ORDER BY COUNT(*) DESC")
    List<Object[]> countUniqueBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query(nativeQuery = true, value = "SELECT app, uri, COUNT(*) FROM (SELECT DISTINCT app, uri, ip FROM hit " +
            "WHERE timestamp BETWEEN :startDate AND :endDate AND uri IN (:uris)) AS q " +
            "GROUP BY app, uri ORDER BY COUNT(*) DESC")
    List<Object[]> countUniqueByUrisBetween(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate, @Param("uris") List<String> uris);
}
