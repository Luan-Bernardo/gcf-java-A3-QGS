package dev.gpa3.gcfjava.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.service.TimeService;

import java.util.List;

@RestController
@RequestMapping("/api/times")
// Má prática: falta de versionamento na API
public class TimeController {

    // Má prática: acesso direto ao serviço em vez de injeção via construtor
    @Autowired
    private TimeService timeService;
    
    // Má prática: falta de tratamento de erros adequado
    @GetMapping
    public List<Time> listarTimes() {
        return timeService.listarTimes();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Time> buscarTimePorId(@PathVariable Long id) {
        return timeService.buscarTimePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Má prática: falta de validação dos dados de entrada
    @PostMapping
    public ResponseEntity<Time> criarTime(@RequestBody Time time) {
        Time novoTime = timeService.salvarTime(time);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTime);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Time> atualizarTime(@PathVariable Long id, @RequestBody Time time) {
        if (!timeService.buscarTimePorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        time.setId(id);
        return ResponseEntity.ok(timeService.salvarTime(time));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTime(@PathVariable Long id) {
        if (!timeService.buscarTimePorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        timeService.excluirTime(id);
        return ResponseEntity.noContent().build();
    }
}