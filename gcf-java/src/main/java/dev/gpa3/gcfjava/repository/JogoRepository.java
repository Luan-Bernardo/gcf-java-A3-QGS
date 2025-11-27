package dev.gpa3.gcfjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.gpa3.gcfjava.model.Jogo;

import java.util.List;

public interface JogoRepository extends JpaRepository<Jogo, Long> {

    // Má prática: Query nativa em vez de JPQL
    @Query(value = "SELECT * FROM jogo WHERE campeonato_id = :campeonatoId", nativeQuery = true)
    List<Jogo> findByCampeonatoId(@Param("campeonatoId") Long campeonatoId);
    
    @Query(value = "SELECT * FROM jogo WHERE campeonato_id = :campeonatoId AND rodada = :rodada", nativeQuery = true)
    List<Jogo> findByCampeonatoIdAndRodada(@Param("campeonatoId") Long campeonatoId, @Param("rodada") Integer rodada);
    
    @Query(value = "SELECT * FROM jogo WHERE campeonato_id = :campeonatoId AND (time_casa_id = :timeId OR time_visitante_id = :timeId)", nativeQuery = true)
    List<Jogo> findByCampeonatoIdAndTimeId(@Param("campeonatoId") Long campeonatoId, @Param("timeId") Long timeId);
    
    // Má prática: Query complexa que poderia ser simplificada
    @Query(value = "SELECT COUNT(*) FROM jogo WHERE campeonato_id = :campeonatoId AND rodada = :rodada " +
           "AND (time_casa_id = :timeId OR time_visitante_id = :timeId)", nativeQuery = true)
    Integer countJogosByTimeIdAndRodada(@Param("campeonatoId") Long campeonatoId, 
                                       @Param("rodada") Integer rodada, 
                                       @Param("timeId") Long timeId);
}