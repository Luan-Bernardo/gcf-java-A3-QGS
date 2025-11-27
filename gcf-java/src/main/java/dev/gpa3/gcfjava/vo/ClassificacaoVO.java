package dev.gpa3.gcfjava.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** Value Object para estatísticas de classificação de um time em um campeonato. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificacaoVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
}
