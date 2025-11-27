package dev.gpa3.gcfjava.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.gpa3.gcfjava.model.Campeonato;
import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.vo.CampeonatoVO;
import dev.gpa3.gcfjava.vo.TimeVO;
import dev.gpa3.gcfjava.repository.CampeonatoRepository;
import dev.gpa3.gcfjava.repository.TimeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para lógica de negócio de Campeonatos.
 */
@Service
@RequiredArgsConstructor
public class CampeonatoService {

    private final CampeonatoRepository campeonatoRepository;
    private final TimeRepository timeRepository;
    
    public List<CampeonatoVO> listarCampeonatos() {
        return campeonatoRepository.findAll()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
    
    public CampeonatoVO buscarCampeonatoPorId(Long id) {
        Campeonato campeonato = campeonatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campeonato não encontrado com ID: " + id));
        return toVO(campeonato);
    }
    
    @Transactional
    public CampeonatoVO salvarCampeonato(CampeonatoVO campeonatoVO) {
        validarCampeonato(campeonatoVO);
        Campeonato campeonato = toEntity(campeonatoVO);
        Campeonato salvo = campeonatoRepository.save(campeonato);
        return toVO(salvo);
    }
    
    @Transactional
    public void excluirCampeonato(Long id) {
        Campeonato campeonato = campeonatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campeonato não encontrado com ID: " + id));
        campeonatoRepository.delete(campeonato);
    }
    
    @Transactional
    public CampeonatoVO adicionarTimeCampeonato(Long campeonatoId, Long timeId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new RuntimeException("Campeonato não encontrado com ID: " + campeonatoId));
        
        Time time = timeRepository.findById(timeId)
                .orElseThrow(() -> new RuntimeException("Time não encontrado com ID: " + timeId));
        
        if (campeonato.getTimes().contains(time)) {
            throw new RuntimeException("Time já está participando deste campeonato");
        }
        
        campeonato.getTimes().add(time);
        Campeonato salvo = campeonatoRepository.save(campeonato);
        return toVO(salvo);
    }
    
    @Transactional
    public CampeonatoVO removerTimeCampeonato(Long campeonatoId, Long timeId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new RuntimeException("Campeonato não encontrado com ID: " + campeonatoId));
        
        Time time = timeRepository.findById(timeId)
                .orElseThrow(() -> new RuntimeException("Time não encontrado com ID: " + timeId));
        
        if (!campeonato.getTimes().contains(time)) {
            throw new RuntimeException("Time não está participando deste campeonato");
        }
        
        campeonato.getTimes().remove(time);
        Campeonato salvo = campeonatoRepository.save(campeonato);
        return toVO(salvo);
    }
    
    public List<TimeVO> listarTimesCampeonato(Long campeonatoId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new RuntimeException("Campeonato não encontrado com ID: " + campeonatoId));
        
        return campeonato.getTimes()
                .stream()
                .map(this::timeToVO)
                .collect(Collectors.toList());
    }
    
    private void validarCampeonato(CampeonatoVO campeonato) {
        if (campeonato.getNome() == null || campeonato.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do campeonato é obrigatório");
        }
        
        if (campeonato.getNome().trim().length() < 3) {
            throw new RuntimeException("Nome do campeonato deve ter no mínimo 3 caracteres");
        }
        
        if (campeonato.getAno() == null || campeonato.getAno() < 1900) {
            throw new RuntimeException("Ano inválido");
        }
        
        if (campeonato.getDataInicio() == null) {
            throw new RuntimeException("Data de início é obrigatória");
        }
    }
    
    private CampeonatoVO toVO(Campeonato campeonato) {
        return CampeonatoVO.builder()
                .id(campeonato.getId())
                .nome(campeonato.getNome())
                .ano(campeonato.getAno())
                .dataInicio(campeonato.getDataInicio())
                .times(campeonato.getTimes()
                        .stream()
                        .map(this::timeToVO)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private Campeonato toEntity(CampeonatoVO vo) {
        return Campeonato.builder()
                .id(vo.getId())
                .nome(vo.getNome())
                .ano(vo.getAno())
                .dataInicio(vo.getDataInicio())
                .build();
    }
    
    private TimeVO timeToVO(Time time) {
        return TimeVO.builder()
                .id(time.getId())
                .nome(time.getNome())
                .cidade(time.getCidade())
                .urlEscudo(time.getUrlEscudo())
                .build();
    }
}
