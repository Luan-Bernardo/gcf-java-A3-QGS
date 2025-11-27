package dev.gpa3.gcfjava.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gpa3.gcfjava.vo.CampeonatoVO;
import dev.gpa3.gcfjava.vo.TimeVO;
import dev.gpa3.gcfjava.service.CampeonatoService;

import java.util.List;

/**
 * Controller REST para gerenciamento de Campeonatos.
 */
@RestController
@RequestMapping("/api/v1/campeonatos")
@RequiredArgsConstructor
public class CampeonatoController {

    private final CampeonatoService campeonatoService;
    
    @GetMapping
    public List<CampeonatoVO> listarCampeonatos() {
        return campeonatoService.listarCampeonatos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCampeonatoPorId(@PathVariable Long id) {
        try {
            CampeonatoVO campeonato = campeonatoService.buscarCampeonatoPorId(id);
            return ResponseEntity.ok(campeonato);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> criarCampeonato(@RequestBody CampeonatoVO campeonatoVO) {
        try {
            CampeonatoVO novoCampeonato = campeonatoService.salvarCampeonato(campeonatoVO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(novoCampeonato);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCampeonato(@PathVariable Long id, @RequestBody CampeonatoVO campeonatoVO) {
        try {
            campeonatoVO.setId(id);
            CampeonatoVO atualizado = campeonatoService.salvarCampeonato(campeonatoVO);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCampeonato(@PathVariable Long id) {
        try {
            campeonatoService.excluirCampeonato(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/times")
    public ResponseEntity<?> listarTimesCampeonato(@PathVariable Long id) {
        try {
            List<TimeVO> times = campeonatoService.listarTimesCampeonato(id);
            return ResponseEntity.ok(times);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
    
    @PostMapping("/{campeonatoId}/times/{timeId}")
    public ResponseEntity<?> adicionarTimeCampeonato(@PathVariable Long campeonatoId, @PathVariable Long timeId) {
        try {
            CampeonatoVO campeonato = campeonatoService.adicionarTimeCampeonato(campeonatoId, timeId);
            return ResponseEntity.ok(campeonato);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{campeonatoId}/times/{timeId}")
    public ResponseEntity<?> removerTimeCampeonato(@PathVariable Long campeonatoId, @PathVariable Long timeId) {
        try {
            CampeonatoVO campeonato = campeonatoService.removerTimeCampeonato(campeonatoId, timeId);
            return ResponseEntity.ok(campeonato);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
