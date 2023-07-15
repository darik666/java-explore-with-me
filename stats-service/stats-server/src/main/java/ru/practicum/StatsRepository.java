package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT NEW ru.practicum.dto.ViewStatsDto(s.app, s.uri, " +
            "CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s) END) " +
            "FROM Stat s " +
            "WHERE s.timestamp >= :start " +
            "AND s.timestamp <= :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(*) DESC")
    List<ru.practicum.dto.ViewStatsDto> findViewStatsWithoutUris(@Param("start") LocalDateTime start,
                                                                 @Param("end") LocalDateTime end,
                                                                 @Param("unique") boolean unique);

    @Query("SELECT NEW ru.practicum.dto.ViewStatsDto(s.app, s.uri, " +
            "CASE WHEN :unique = true THEN COUNT(DISTINCT s.ip) ELSE COUNT(s) END) " +
            "FROM Stat s " +
            "WHERE s.timestamp >= :start " +
            "AND s.timestamp <= :end " +
            "AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(*) DESC")
    List<ViewStatsDto> findViewStatsWithUris(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("uris") List<String> uris,
                                             @Param("unique") boolean unique);

}