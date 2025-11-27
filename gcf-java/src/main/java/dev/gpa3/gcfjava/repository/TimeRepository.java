package dev.gpa3.gcfjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.gpa3.gcfjava.model.Time;

import java.util.List;

// Má prática: repositório sem interface genérica
public interface TimeRepository extends JpaRepository<Time, Long> {
    
    // Má prática: query nativa em vez de JPQL
    @Query(value = "SELECT * FROM time WHERE nome LIKE %:nome%", nativeQuery = true)
    List<Time> findByNomeContaining(@Param("nome") String nome);
}