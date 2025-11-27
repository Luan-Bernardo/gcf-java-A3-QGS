package dev.gpa3.gcfjava.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/** Entidade JPA representando um Jogo de campeonato no banco de dados. */
@Entity
@Table(name = "jogo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jogo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_casa_id", nullable = false)
    private Time timeCasa;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_visitante_id", nullable = false)
    private Time timeVisitante;
    
    @Column(name = "rodada", nullable = false)
    private Integer rodada;
    
    @Column(name = "data")
    @Temporal(TemporalType.DATE)
    private Date data;
    
    @Column(name = "gols_casa")
    @Builder.Default
    private Integer golsCasa = 0;
    
    @Column(name = "gols_visitante")
    @Builder.Default
    private Integer golsVisitante = 0;
    
    @Column(name = "finalizado")
    @Builder.Default
    private Boolean finalizado = false;
}
