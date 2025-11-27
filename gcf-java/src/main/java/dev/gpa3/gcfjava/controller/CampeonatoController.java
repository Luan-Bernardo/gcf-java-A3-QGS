package dev.gpa3.gcfjava.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gpa3.gcfjava.model.Campeonato;
import dev.gpa3.gcfjava.model.Classificacao;
import dev.gpa3.gcfjava.model.Jogo;
import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.service.CampeonatoService;
import dev.gpa3.gcfjava.service.JogoService;

import java.util.List;

@RestController
@RequestMapping("/api/campeonatos")
// Má prática: falta de versionamento na API
public class CampeonatoController {

    // Má prática: acesso direto aos serviços em vez de injeção via construtor
    @Autowired
    private CampeonatoService campeonatoService;
    
    @Autowired
    private JogoService jogoService;
    
    // Má prática: falta de tratamento de erros adequado
    @GetMapping
    public List<Campeonato> listarCampeonatos() {
        return campeonatoService.listarCampeonatos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Campeonato> buscarCampeonatoPorId(@PathVariable Long id) {
        return campeonatoService.buscarCampeonatoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Má prática: falta de validação dos dados de entrada
    @PostMapping
    public ResponseEntity<Campeonato> criarCampeonato(@RequestBody Campeonato campeonato) {
        Campeonato novoCampeonato = campeonatoService.salvarCampeonato(campeonato);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCampeonato);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Campeonato> atualizarCampeonato(@PathVariable Long id, @RequestBody Campeonato campeonato) {
        if (!campeonatoService.buscarCampeonatoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        campeonato.setId(id);
        return ResponseEntity.ok(campeonatoService.salvarCampeonato(campeonato));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCampeonato(@PathVariable Long id) {
        if (!campeonatoService.buscarCampeonatoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        campeonatoService.excluirCampeonato(id);
        return ResponseEntity.noContent().build();
    }
    
    // Má prática: endpoints que misturam responsabilidades
    @GetMapping("/{id}/times")
    public ResponseEntity<List<Time>> listarTimesCampeonato(@PathVariable Long id) {
        if (!campeonatoService.buscarCampeonatoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(campeonatoService.listarTimesCampeonato(id));
    }
    
    @PostMapping("/{campeonatoId}/times/{timeId}")
    public ResponseEntity<Campeonato> adicionarTimeCampeonato(@PathVariable Long campeonatoId, @PathVariable Long timeId) {
        Campeonato campeonato = campeonatoService.adicionarTimeCampeonato(campeonatoId, timeId);
        
        if (campeonato == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(campeonato);
    }
    
    @DeleteMapping("/{campeonatoId}/times/{timeId}")
    public ResponseEntity<Campeonato> removerTimeCampeonato(@PathVariable Long campeonatoId, @PathVariable Long timeId) {
        Campeonato campeonato = campeonatoService.removerTimeCampeonato(campeonatoId, timeId);
        
        if (campeonato == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(campeonato);
    }
    
    // Má prática: endpoints que deveriam estar em um controlador separado
    @GetMapping("/{id}/jogos")
    public ResponseEntity<List<Jogo>> listarJogosCampeonato(@PathVariable Long id) {
        if (!campeonatoService.buscarCampeonatoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(jogoService.listarJogosPorCampeonato(id));
    }
    
    @GetMapping("/{id}/rodadas/{rodada}/jogos")
    public ResponseEntity<List<Jogo>> listarJogosCampeonatoPorRodada(@PathVariable Long id, @PathVariable Integer rodada) {
        if (!campeonatoService.buscarCampeonatoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(jogoService.listarJogosPorCampeonatoERodada(id, rodada));
    }
    
    @GetMapping("/{id}/classificacao")
    public ResponseEntity<List<Classificacao>> getClassificacao(@PathVariable Long id) {
        if (!campeonatoService.buscarCampeonatoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(jogoService.calcularClassificacao(id));
    }
}