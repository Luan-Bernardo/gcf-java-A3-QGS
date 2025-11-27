package dev.gpa3.gcfjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.gpa3.gcfjava.model.Campeonato;

import java.util.List;

public interface CampeonatoRepository extends JpaRepository<Campeonato, Long> {

    // Má prática: query nativa em vez de JPQL
    @Query(value = "SELECT * FROM campeonato WHERE ano = :ano", nativeQuery = true)
    List<Campeonato> findByAno(@Param("ano") Integer ano);
    
    // Má prática: query nativa complexa em vez de usar joins no JPQL
    @Query(value = "SELECT c.* FROM campeonato c " +
           "INNER JOIN campeonatotime ct ON c.id = ct.campeonato_id " +
           "WHERE ct.time_id = :timeId", nativeQuery = true)
    List<Campeonato> findByTimeId(@Param("timeId") Long timeId);
}