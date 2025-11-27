package dev.gpa3.gcfjava.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/** Value Object para transferência de dados de Jogo entre camadas. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JogoVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long campeonatoId;
    private String campeonatoNome;
    private TimeVO timeCasa;
    private TimeVO timeVisitante;
    private Integer rodada;
    private Date data;
    private Integer golsCasa;
    private Integer golsVisitante;
    private Boolean finalizado;
}
