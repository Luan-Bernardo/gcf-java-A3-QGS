package dev.gpa3.gcfjava.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
// Má prática: nome da tabela sem prefixo ou convenção
@Table(name = "campeonato")
public class Campeonato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome")
    private String nome;
    
    @Column(name = "ano")
    private Integer ano;
    
    @Column(name = "datainicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
    
    // Má prática: relacionamento sem definir o fetch type (usando o padrão LAZY)
    // Má prática: usando HashSet ao invés de uma coleção mais específica
    @ManyToMany
    @JoinTable(
        name = "campeonatotime",
        joinColumns = @JoinColumn(name = "campeonato_id"),
        inverseJoinColumns = @JoinColumn(name = "time_id")
    )
    private Set<Time> times = new HashSet<>();
    
    // Má prática: construtores, getters e setters públicos
    public Campeonato() {}
    
    public Campeonato(Long id, String nome, Integer ano, Date dataInicio) {
        this.id = id;
        this.nome = nome;
        this.ano = ano;
        this.dataInicio = dataInicio;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Integer getAno() {
        return ano;
    }
    
    public void setAno(Integer ano) {
        this.ano = ano;
    }
    
    public Date getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public Set<Time> getTimes() {
        return times;
    }
    
    public void setTimes(Set<Time> times) {
        this.times = times;
    }
    
    // Má prática: método que deveria estar em um serviço
    public void adicionarTime(Time time) {
        this.times.add(time);
    }
    
    // Má prática: método que deveria estar em um serviço
    public void removerTime(Time time) {
        this.times.remove(time);
    }
}