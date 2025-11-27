package dev.gpa3.gcfjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.TimeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TimeService {

    // Má prática: acesso direto ao repositório em vez de injeção via construtor
    @Autowired
    private TimeRepository timeRepository;
    
    // Má prática: falta de tratamento de erros
    public List<Time> listarTimes() {
        return timeRepository.findAll();
    }
    
    // Má prática: retorno de Optional em vez de tratar exceção
    public Optional<Time> buscarTimePorId(Long id) {
        return timeRepository.findById(id);
    }
    
    // Má prática: validação básica no serviço
    public Time salvarTime(Time time) {
        return timeRepository.save(time);
    }
    
    // Má prática: falta de verificação se o time existe
    public void excluirTime(Long id) {
        timeRepository.deleteById(id);
    }
    
    public List<Time> buscarTimesPorNome(String nome) {
        return timeRepository.findByNomeContaining(nome);
    }
}