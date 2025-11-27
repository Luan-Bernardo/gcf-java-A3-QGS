package dev.gpa3.gcfjava.controller;

import dev.gpa3.gcfjava.service.TimeService;
import dev.gpa3.gcfjava.vo.TimeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Times.
 */
@RestController
@RequestMapping("/api/v1/times")
@RequiredArgsConstructor
public class TimeController {

    private final TimeService timeService;
    
    @GetMapping
    public ResponseEntity<List<TimeVO>> listarTimes() {
        return ResponseEntity.ok(timeService.listarTimes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TimeVO> buscarTimePorId(@PathVariable Long id) {
        try {
            TimeVO time = timeService.buscarTimePorId(id);
            return ResponseEntity.ok(time);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<TimeVO> criarTime(@RequestBody TimeVO timeVO) {
        try {
            TimeVO novoTime = timeService.salvarTime(timeVO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoTime);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TimeVO> atualizarTime(@PathVariable Long id, @RequestBody TimeVO timeVO) {
        try {
            timeService.buscarTimePorId(id);
            timeVO.setId(id);
            TimeVO timeAtualizado = timeService.salvarTime(timeVO);
            return ResponseEntity.ok(timeAtualizado);
        } catch (Exception e) {
            return e instanceof IllegalArgumentException ? 
                ResponseEntity.badRequest().build() : 
                ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTime(@PathVariable Long id) {
        try {
            timeService.excluirTime(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}