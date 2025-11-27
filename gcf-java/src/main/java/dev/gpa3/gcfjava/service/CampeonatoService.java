package dev.gpa3.gcfjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.gpa3.gcfjava.model.Campeonato;
import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.CampeonatoRepository;
import dev.gpa3.gcfjava.repository.TimeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CampeonatoService {

    // Má prática: acesso direto aos repositórios em vez de injeção via construtor
    @Autowired
    private CampeonatoRepository campeonatoRepository;
    
    @Autowired
    private TimeRepository timeRepository;
    
    // Má prática: falta de tratamento de erros
    public List<Campeonato> listarCampeonatos() {
        return campeonatoRepository.findAll();
    }
    
    public Optional<Campeonato> buscarCampeonatoPorId(Long id) {
        return campeonatoRepository.findById(id);
    }
    
    // Má prática: validação básica no serviço
    public Campeonato salvarCampeonato(Campeonato campeonato) {
        return campeonatoRepository.save(campeonato);
    }
    
    // Má prática: falta de verificação se o campeonato existe
    public void excluirCampeonato(Long id) {
        campeonatoRepository.deleteById(id);
    }
    
    // Má prática: método que deveria ter validações mais robustas
    public Campeonato adicionarTimeCampeonato(Long campeonatoId, Long timeId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId).orElse(null);
        Time time = timeRepository.findById(timeId).orElse(null);
        
        if (campeonato != null && time != null) {
            campeonato.getTimes().add(time);
            return campeonatoRepository.save(campeonato);
        }
        
        return null;
    }
    
    // Má prática: método que deveria ter validações mais robustas
    public Campeonato removerTimeCampeonato(Long campeonatoId, Long timeId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId).orElse(null);
        Time time = timeRepository.findById(timeId).orElse(null);
        
        if (campeonato != null && time != null) {
            campeonato.getTimes().remove(time);
            return campeonatoRepository.save(campeonato);
        }
        
        return null;
    }
    
    // Má prática: método ineficiente que busca todos os times em vez de usar uma query específica
    public List<Time> listarTimesCampeonato(Long campeonatoId) {
        Optional<Campeonato> campeonatoOpt = campeonatoRepository.findById(campeonatoId);
        if (campeonatoOpt.isPresent()) {
            return new ArrayList<>(campeonatoOpt.get().getTimes());
        }
        return new ArrayList<>();
    }
}