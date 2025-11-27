package dev.gpa3.gcfjava.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Value Object para transferência de dados de Time entre camadas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nome;
    private String cidade;
    private String urlEscudo;
}
