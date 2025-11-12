package dev.gpa3.gcfjava.model;

// Má prática: todos os campos são públicos
public class Classificacao {
    public Long timeId;
    public String timeNome;
    public Integer pontos;
    public Integer jogos;
    public Integer vitorias;
    public Integer empates;
    public Integer derrotas;
    public Integer golsPro;
    public Integer golsContra;
    public Integer saldoGols;
    
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
    
    // Má prática: lógica de cálculo no modelo em vez de um serviço
    public void atualizarEstatisticas(Integer golsMarcados, Integer golsSofridos) {
        this.jogos++;
        this.golsPro += golsMarcados;
        this.golsContra += golsSofridos;
        this.saldoGols = this.golsPro - this.golsContra;
        
        if (golsMarcados > golsSofridos) {
            this.vitorias++;
            this.pontos += 3;
        } else if (golsMarcados == golsSofridos) {
            this.empates++;
            this.pontos += 1;
        } else {
            this.derrotas++;
        }
    }
}