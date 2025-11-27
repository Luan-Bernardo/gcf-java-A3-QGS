package dev.gpa3.gcfjava.repository;

import dev.gpa3.gcfjava.model.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso aos dados da entidade Time.
 */
@Repository
public interface TimeRepository extends JpaRepository<Time, Long> {
    
    /**
     * Busca times por nome (case-insensitive, busca parcial).
     */
    @Query("SELECT t FROM Time t WHERE LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Time> findByNomeContaining(@Param("nome") String nome);
}