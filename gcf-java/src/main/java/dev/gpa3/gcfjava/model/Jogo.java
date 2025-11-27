package dev.gpa3.gcfjava.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
// Má prática: nome da tabela sem prefixo ou convenção
@Table(name = "jogo")
public class Jogo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Má prática: não usar o cascade type
    @ManyToOne
    @JoinColumn(name = "campeonato_id")
    private Campeonato campeonato;
    
    @ManyToOne
    @JoinColumn(name = "time_casa_id")
    private Time timeCasa;
    
    @ManyToOne
    @JoinColumn(name = "time_visitante_id")
    private Time timeVisitante;
    
    @Column(name = "rodada")
    private Integer rodada;
    
    @Column(name = "data")
    @Temporal(TemporalType.DATE)
    private Date data;
    
    @Column(name = "golscasa")
    private Integer golsCasa;
    
    @Column(name = "golsvisitante")
    private Integer golsVisitante;
    
    @Column(name = "finalizado")
    private Boolean finalizado;
    
    // Má prática: construtores, getters e setters públicos
    public Jogo() {
        this.finalizado = false;
        this.golsCasa = 0;
        this.golsVisitante = 0;
    }
    
    public Jogo(Long id, Campeonato campeonato, Time timeCasa, Time timeVisitante, 
                Integer rodada, Date data, Integer golsCasa, Integer golsVisitante, Boolean finalizado) {
        this.id = id;
        this.campeonato = campeonato;
        this.timeCasa = timeCasa;
        this.timeVisitante = timeVisitante;
        this.rodada = rodada;
        this.data = data;
        this.golsCasa = golsCasa;
        this.golsVisitante = golsVisitante;
        this.finalizado = finalizado;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Campeonato getCampeonato() {
        return campeonato;
    }
    
    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }
    
    public Time getTimeCasa() {
        return timeCasa;
    }
    
    public void setTimeCasa(Time timeCasa) {
        this.timeCasa = timeCasa;
    }
    
    public Time getTimeVisitante() {
        return timeVisitante;
    }
    
    public void setTimeVisitante(Time timeVisitante) {
        this.timeVisitante = timeVisitante;
    }
    
    public Integer getRodada() {
        return rodada;
    }
    
    public void setRodada(Integer rodada) {
        this.rodada = rodada;
    }
    
    public Date getData() {
        return data;
    }
    
    public void setData(Date data) {
        this.data = data;
    }
    
    public Integer getGolsCasa() {
        return golsCasa;
    }
    
    public void setGolsCasa(Integer golsCasa) {
        this.golsCasa = golsCasa;
    }
    
    public Integer getGolsVisitante() {
        return golsVisitante;
    }
    
    public void setGolsVisitante(Integer golsVisitante) {
        this.golsVisitante = golsVisitante;
    }
    
    public Boolean getFinalizado() {
        return finalizado;
    }
    
    public void setFinalizado(Boolean finalizado) {
        this.finalizado = finalizado;
    }
    
    // Má prática: lógica de negócio na entidade
    public String getResultado() {
        if (!finalizado) {
            return "Jogo não finalizado";
        }
        return golsCasa + " x " + golsVisitante;
    }
    
    // Má prática: lógica de negócio na entidade
    public Time getVencedor() {
        if (!finalizado) {
            return null;
        }
        
        if (golsCasa > golsVisitante) {
            return timeCasa;
        } else if (golsVisitante > golsCasa) {
            return timeVisitante;
        }
        
        return null; // Empate
    }
    
    // Má prática: lógica de negócio na entidade
    public boolean isEmpate() {
        return finalizado && golsCasa.equals(golsVisitante);
    }
}