package dev.gpa3.gcfjava.model;

import lombok.Getter;
import lombok.Setter;

/** Classe auxiliar para cálculo de estatísticas de classificação de times. */
@Getter
@Setter
public class Classificacao {
    
    private Long timeId;
    private String timeNome;
    private Integer pontos;
    private Integer jogos;
    private Integer vitorias;
    private Integer empates;
    private Integer derrotas;
    private Integer golsPro;
    private Integer golsContra;
    private Integer saldoGols;
    
    public Classificacao(Long timeId, String timeNome) {
        this.timeId = timeId;
        this.timeNome = timeNome;
        this.pontos = 0;
        this.jogos = 0;
        this.vitorias = 0;
        this.empates = 0;
        this.derrotas = 0;
        this.golsPro = 0;
        this.golsContra = 0;
        this.saldoGols = 0;
    }
}
