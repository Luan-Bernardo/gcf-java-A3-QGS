package dev.gpa3.gcfjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade JPA representando um Campeonato no banco de dados.
 */
@Entity
@Table(name = "campeonato")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campeonato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "ano", nullable = false)
    private Integer ano;
    
    @Column(name = "data_inicio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
        name = "campeonato_time",
        joinColumns = @JoinColumn(name = "campeonato_id"),
        inverseJoinColumns = @JoinColumn(name = "time_id")
    )
    @Builder.Default
    private Set<Time> times = new HashSet<>();
}
