package dev.gpa3.gcfjava.service;

import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.TimeRepository;
import dev.gpa3.gcfjava.vo.TimeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio de Times.
 */
@Service
@RequiredArgsConstructor
public class TimeService {

    private final TimeRepository timeRepository;
    
    @Transactional(readOnly = true)
    public List<TimeVO> listarTimes() {
        return timeRepository.findAll().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TimeVO buscarTimePorId(Long id) {
        return timeRepository.findById(id)
                .map(this::toVO)
                .orElseThrow(() -> new RuntimeException("Time não encontrado com id: " + id));
    }
    
    @Transactional
    public TimeVO salvarTime(TimeVO timeVO) {
        validarTime(timeVO);
        Time time = toEntity(timeVO);
        Time timeSalvo = timeRepository.save(time);
        return toVO(timeSalvo);
    }
    
    @Transactional
    public void excluirTime(Long id) {
        if (!timeRepository.existsById(id)) {
            throw new RuntimeException("Time não encontrado com id: " + id);
        }
        timeRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<TimeVO> buscarTimesPorNome(String nome) {
        return timeRepository.findByNomeContaining(nome).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
    
    private void validarTime(TimeVO timeVO) {
        if (timeVO.getNome() == null || timeVO.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do time é obrigatório");
        }
        if (timeVO.getCidade() == null || timeVO.getCidade().trim().isEmpty()) {
            throw new RuntimeException("Cidade do time é obrigatória");
        }
    }
    
    private TimeVO toVO(Time time) {
        return TimeVO.builder()
                .id(time.getId())
                .nome(time.getNome())
                .cidade(time.getCidade())
                .urlEscudo(time.getUrlEscudo())
                .build();
    }
    
    private Time toEntity(TimeVO timeVO) {
        return Time.builder()
                .id(timeVO.getId())
                .nome(timeVO.getNome())
                .cidade(timeVO.getCidade())
                .urlEscudo(timeVO.getUrlEscudo())
                .build();
    }
}