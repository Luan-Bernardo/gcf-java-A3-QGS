package dev.gpa3.gcfjava.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA representando a tabela 'time' no banco de dados.
 */
@Entity
@Table(name = "time")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Time {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;
    
    @Column(name = "url_escudo", length = 500)
    private String urlEscudo;
}