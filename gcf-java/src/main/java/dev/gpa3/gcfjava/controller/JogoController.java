package dev.gpa3.gcfjava.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gpa3.gcfjava.model.Jogo;
import dev.gpa3.gcfjava.service.JogoService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/jogos")
// Má prática: falta de versionamento na API
public class JogoController {

    // Má prática: acesso direto ao serviço em vez de injeção via construtor
    @Autowired
    private JogoService jogoService;
    
    // Má prática: falta de tratamento de erros adequado
    @GetMapping
    public List<Jogo> listarJogos() {
        return jogoService.listarJogos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Jogo> buscarJogoPorId(@PathVariable Long id) {
        return jogoService.buscarJogoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Má prática: falta de validação dos dados de entrada
    @PostMapping
    public ResponseEntity<Jogo> criarJogo(@RequestBody Jogo jogo) {
        try {
            Jogo novoJogo = jogoService.salvarJogo(jogo);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoJogo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Jogo> atualizarJogo(@PathVariable Long id, @RequestBody Jogo jogo) {
        if (!jogoService.buscarJogoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        jogo.setId(id);
        
        try {
            return ResponseEntity.ok(jogoService.salvarJogo(jogo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirJogo(@PathVariable Long id) {
        if (!jogoService.buscarJogoPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        jogoService.excluirJogo(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/campeonato/{campeonatoId}")
    public ResponseEntity<List<Jogo>> listarJogosPorCampeonato(@PathVariable Long campeonatoId) {
        return ResponseEntity.ok(jogoService.listarJogosPorCampeonato(campeonatoId));
    }
    
    @GetMapping("/campeonato/{campeonatoId}/rodada/{rodada}")
    public ResponseEntity<List<Jogo>> listarJogosPorCampeonatoERodada(
            @PathVariable Long campeonatoId, @PathVariable Integer rodada) {
        return ResponseEntity.ok(jogoService.listarJogosPorCampeonatoERodada(campeonatoId, rodada));
    }
    
    @GetMapping("/campeonato/{campeonatoId}/rodadas")
    public ResponseEntity<Set<Integer>> listarRodadasPorCampeonato(@PathVariable Long campeonatoId) {
        return ResponseEntity.ok(jogoService.listarRodadasPorCampeonato(campeonatoId));
    }
    
    // Má prática: endpoint que deveria usar PATCH em vez de PUT
    @PutMapping("/{id}/resultado")
    public ResponseEntity<Jogo> registrarResultado(
            @PathVariable Long id, 
            @RequestParam Integer golsCasa, 
            @RequestParam Integer golsVisitante) {
        
        try {
            Jogo jogo = jogoService.registrarResultado(id, golsCasa, golsVisitante);
            return ResponseEntity.ok(jogo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}