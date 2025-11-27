package dev.gpa3.gcfjava.model;

import jakarta.persistence.*;

@Entity
// Má prática: nome da tabela sem prefixo ou convenção
@Table(name = "time")
public class Time {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Má prática: nome da coluna sem prefixo
    private Long id;
    
    // Má prática: nome das colunas com iniciais maiúsculas
    @Column(name = "nome")
    private String nome;
    
    @Column(name = "cidade")
    private String cidade;
    
    @Column(name = "urlescudo")
    private String urlEscudo;
    
    // Má prática: construtores, getters e setters públicos
    public Time() {}
    
    public Time(Long id, String nome, String cidade, String urlEscudo) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.urlEscudo = urlEscudo;
    }
    
    // Má prática: getters e setters sem validação
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
    
    public String getCidade() {
        return cidade;
    }
    
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    
    public String getUrlEscudo() {
        return urlEscudo;
    }
    
    public void setUrlEscudo(String urlEscudo) {
        this.urlEscudo = urlEscudo;
    }
}