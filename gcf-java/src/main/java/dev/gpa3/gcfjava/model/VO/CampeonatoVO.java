package dev.gpa3.gcfjava.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Value Object para transferência de dados de Campeonato entre camadas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampeonatoVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nome;
    private Integer ano;
    private Date dataInicio;
    private List<TimeVO> times;
}
