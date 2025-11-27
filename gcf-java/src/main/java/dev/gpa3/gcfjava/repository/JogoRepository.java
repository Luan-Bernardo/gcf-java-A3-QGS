package dev.gpa3.gcfjava.repository;

import dev.gpa3.gcfjava.model.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository para acesso aos dados da entidade Jogo.
 */
@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {

    @Query("SELECT j FROM Jogo j WHERE j.campeonato.id = :campeonatoId")
    List<Jogo> findByCampeonatoId(@Param("campeonatoId") Long campeonatoId);
    
    @Query("SELECT j FROM Jogo j WHERE j.campeonato.id = :campeonatoId AND j.rodada = :rodada")
    List<Jogo> findByCampeonatoIdAndRodada(@Param("campeonatoId") Long campeonatoId, @Param("rodada") Integer rodada);
    
    @Query("SELECT j FROM Jogo j WHERE j.campeonato.id = :campeonatoId AND (j.timeCasa.id = :timeId OR j.timeVisitante.id = :timeId)")
    List<Jogo> findByCampeonatoIdAndTimeId(@Param("campeonatoId") Long campeonatoId, @Param("timeId") Long timeId);
    
    @Query("SELECT COUNT(j) FROM Jogo j WHERE j.campeonato.id = :campeonatoId AND j.rodada = :rodada " +
           "AND (j.timeCasa.id = :timeId OR j.timeVisitante.id = :timeId)")
    Long countJogosByTimeIdAndRodada(@Param("campeonatoId") Long campeonatoId, 
                                     @Param("rodada") Integer rodada, 
                                     @Param("timeId") Long timeId);
    
    @Query("SELECT DISTINCT j.rodada FROM Jogo j WHERE j.campeonato.id = :campeonatoId ORDER BY j.rodada")
    Set<Integer> findRodasByCampeonatoId(@Param("campeonatoId") Long campeonatoId);
}
