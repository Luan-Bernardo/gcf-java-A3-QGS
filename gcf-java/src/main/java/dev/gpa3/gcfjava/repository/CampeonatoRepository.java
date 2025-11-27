package dev.gpa3.gcfjava.repository;

import dev.gpa3.gcfjava.model.Campeonato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso aos dados da entidade Campeonato.
 */
@Repository
public interface CampeonatoRepository extends JpaRepository<Campeonato, Long> {

    @Query("SELECT c FROM Campeonato c WHERE c.ano = :ano")
    List<Campeonato> findByAno(@Param("ano") Integer ano);
    
    @Query("SELECT c FROM Campeonato c JOIN c.times t WHERE t.id = :timeId")
    List<Campeonato> findByTimeId(@Param("timeId") Long timeId);
    
    @Query("SELECT c FROM Campeonato c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Campeonato> findByNomeContaining(@Param("termo") String termo);
}
