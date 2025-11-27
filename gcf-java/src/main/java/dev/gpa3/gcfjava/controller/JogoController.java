package dev.gpa3.gcfjava.controller;

import dev.gpa3.gcfjava.service.JogoService;
import dev.gpa3.gcfjava.vo.ClassificacaoVO;
import dev.gpa3.gcfjava.vo.JogoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/** Controller REST para gerenciamento de Jogos e Classificação. */
@RestController
@RequestMapping("/api/v1/jogos")
@RequiredArgsConstructor
public class JogoController {

    private final JogoService jogoService;
    
    @GetMapping
    public ResponseEntity<List<JogoVO>> listarJogos() {
        return ResponseEntity.ok(jogoService.listarJogos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<JogoVO> buscarJogoPorId(@PathVariable Long id) {
        try {
            JogoVO jogo = jogoService.buscarJogoPorId(id);
            return ResponseEntity.ok(jogo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> criarJogo(@RequestBody JogoVO jogoVO) {
        try {
            JogoVO novoJogo = jogoService.salvarJogo(jogoVO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoJogo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarJogo(@PathVariable Long id, @RequestBody JogoVO jogoVO) {
        try {
            jogoService.buscarJogoPorId(id);
            jogoVO.setId(id);
            JogoVO jogoAtualizado = jogoService.salvarJogo(jogoVO);
            return ResponseEntity.ok(jogoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirJogo(@PathVariable Long id) {
        try {
            jogoService.excluirJogo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/campeonato/{campeonatoId}")
    public ResponseEntity<List<JogoVO>> listarJogosPorCampeonato(@PathVariable Long campeonatoId) {
        return ResponseEntity.ok(jogoService.listarJogosPorCampeonato(campeonatoId));
    }
    
    @GetMapping("/campeonato/{campeonatoId}/rodada/{rodada}")
    public ResponseEntity<List<JogoVO>> listarJogosPorCampeonatoERodada(
            @PathVariable Long campeonatoId, @PathVariable Integer rodada) {
        return ResponseEntity.ok(jogoService.listarJogosPorCampeonatoERodada(campeonatoId, rodada));
    }
    
    @GetMapping("/campeonato/{campeonatoId}/rodadas")
    public ResponseEntity<Set<Integer>> listarRodadasPorCampeonato(@PathVariable Long campeonatoId) {
        return ResponseEntity.ok(jogoService.listarRodadasPorCampeonato(campeonatoId));
    }
    
    @GetMapping("/campeonato/{campeonatoId}/classificacao")
    public ResponseEntity<List<ClassificacaoVO>> obterClassificacao(@PathVariable Long campeonatoId) {
        try {
            List<ClassificacaoVO> classificacao = jogoService.calcularClassificacao(campeonatoId);
            return ResponseEntity.ok(classificacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}/resultado")
    public ResponseEntity<JogoVO> registrarResultado(
            @PathVariable Long id, 
            @RequestParam Integer golsCasa, 
            @RequestParam Integer golsVisitante) {
        try {
            JogoVO jogo = jogoService.registrarResultado(id, golsCasa, golsVisitante);
            return ResponseEntity.ok(jogo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
