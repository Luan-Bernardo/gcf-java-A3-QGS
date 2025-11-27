package dev.gpa3.gcfjava.service;

import dev.gpa3.gcfjava.model.Campeonato;
import dev.gpa3.gcfjava.model.Classificacao;
import dev.gpa3.gcfjava.model.Jogo;
import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.CampeonatoRepository;
import dev.gpa3.gcfjava.repository.JogoRepository;
import dev.gpa3.gcfjava.repository.TimeRepository;
import dev.gpa3.gcfjava.vo.ClassificacaoVO;
import dev.gpa3.gcfjava.vo.JogoVO;
import dev.gpa3.gcfjava.vo.TimeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/** Service para lógica de negócio de Jogos e Classificação. */
@Service
@RequiredArgsConstructor
public class JogoService {

    private final JogoRepository jogoRepository;
    private final CampeonatoRepository campeonatoRepository;
    private final TimeRepository timeRepository;
    
    public List<JogoVO> listarJogos() {
        return jogoRepository.findAll().stream()
                .map(this::converterParaVO)
                .collect(Collectors.toList());
    }
    
    public JogoVO buscarJogoPorId(Long id) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));
        return converterParaVO(jogo);
    }
    
    @Transactional
    public JogoVO salvarJogo(JogoVO jogoVO) {
        validarJogo(jogoVO);
        
        Campeonato campeonato = campeonatoRepository.findById(jogoVO.getCampeonatoId())
                .orElseThrow(() -> new RuntimeException("Campeonato não encontrado"));
        
        Time timeCasa = timeRepository.findById(jogoVO.getTimeCasa().getId())
                .orElseThrow(() -> new RuntimeException("Time da casa não encontrado"));
        
        Time timeVisitante = timeRepository.findById(jogoVO.getTimeVisitante().getId())
                .orElseThrow(() -> new RuntimeException("Time visitante não encontrado"));
        
        Jogo jogo;
        if (jogoVO.getId() != null) {
            jogo = jogoRepository.findById(jogoVO.getId())
                    .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));
        } else {
            jogo = new Jogo();
        }
        
        jogo.setCampeonato(campeonato);
        jogo.setTimeCasa(timeCasa);
        jogo.setTimeVisitante(timeVisitante);
        jogo.setRodada(jogoVO.getRodada());
        jogo.setData(jogoVO.getData());
        jogo.setGolsCasa(jogoVO.getGolsCasa());
        jogo.setGolsVisitante(jogoVO.getGolsVisitante());
        jogo.setFinalizado(jogoVO.getFinalizado());
        
        Jogo jogoSalvo = jogoRepository.save(jogo);
        return converterParaVO(jogoSalvo);
    }
    
    @Transactional
    public void excluirJogo(Long id) {
        if (!jogoRepository.existsById(id)) {
            throw new RuntimeException("Jogo não encontrado");
        }
        jogoRepository.deleteById(id);
    }
    
    public List<JogoVO> listarJogosPorCampeonato(Long campeonatoId) {
        return jogoRepository.findByCampeonatoId(campeonatoId).stream()
                .map(this::converterParaVO)
                .collect(Collectors.toList());
    }
    
    public List<JogoVO> listarJogosPorCampeonatoERodada(Long campeonatoId, Integer rodada) {
        return jogoRepository.findByCampeonatoIdAndRodada(campeonatoId, rodada).stream()
                .map(this::converterParaVO)
                .collect(Collectors.toList());
    }
    
    public Set<Integer> listarRodadasPorCampeonato(Long campeonatoId) {
        return jogoRepository.findRodasByCampeonatoId(campeonatoId);
    }
    
    public List<ClassificacaoVO> calcularClassificacao(Long campeonatoId) {
        Campeonato campeonato = campeonatoRepository.findById(campeonatoId)
                .orElseThrow(() -> new RuntimeException("Campeonato não encontrado"));
        
        Map<Long, Classificacao> classificacaoMap = campeonato.getTimes().stream()
                .collect(Collectors.toMap(
                        Time::getId,
                        time -> new Classificacao(time.getId(), time.getNome())
                ));
        
        List<Jogo> jogos = jogoRepository.findByCampeonatoId(campeonatoId);
        
        jogos.stream()
                .filter(Jogo::getFinalizado)
                .forEach(jogo -> {
                    Long timeCasaId = jogo.getTimeCasa().getId();
                    Long timeVisitanteId = jogo.getTimeVisitante().getId();
                    
                    Classificacao classifCasa = classificacaoMap.get(timeCasaId);
                    Classificacao classifVisitante = classificacaoMap.get(timeVisitanteId);
                    
                    if (classifCasa != null && classifVisitante != null) {
                        atualizarEstatisticas(classifCasa, jogo.getGolsCasa(), jogo.getGolsVisitante());
                        atualizarEstatisticas(classifVisitante, jogo.getGolsVisitante(), jogo.getGolsCasa());
                    }
                });
        
        return classificacaoMap.values().stream()
                .sorted(Comparator
                        .comparing(Classificacao::getPontos).reversed()
                        .thenComparing(Classificacao::getVitorias, Comparator.reverseOrder())
                        .thenComparing(Classificacao::getSaldoGols, Comparator.reverseOrder())
                        .thenComparing(Classificacao::getGolsPro, Comparator.reverseOrder()))
                .map(this::converterClassificacaoParaVO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public JogoVO registrarResultado(Long jogoId, Integer golsCasa, Integer golsVisitante) {
        if (golsCasa < 0 || golsVisitante < 0) {
            throw new RuntimeException("Os gols não podem ser negativos");
        }
        
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));
        
        jogo.setGolsCasa(golsCasa);
        jogo.setGolsVisitante(golsVisitante);
        jogo.setFinalizado(true);
        
        Jogo jogoAtualizado = jogoRepository.save(jogo);
        return converterParaVO(jogoAtualizado);
    }
    
    private void validarJogo(JogoVO jogoVO) {
        if (jogoVO.getTimeCasa().getId().equals(jogoVO.getTimeVisitante().getId())) {
            throw new RuntimeException("O time da casa não pode ser o mesmo que o time visitante");
        }
        
        if (jogoVO.getRodada() == null || jogoVO.getRodada() < 1) {
            throw new RuntimeException("Rodada inválida");
        }
        
        if (jogoVO.getId() == null) {
            Long jogosTimeCasa = jogoRepository.countJogosByTimeIdAndRodada(
                    jogoVO.getCampeonatoId(), jogoVO.getRodada(), jogoVO.getTimeCasa().getId());
            
            Long jogosTimeVisitante = jogoRepository.countJogosByTimeIdAndRodada(
                    jogoVO.getCampeonatoId(), jogoVO.getRodada(), jogoVO.getTimeVisitante().getId());
            
            if (jogosTimeCasa > 0 || jogosTimeVisitante > 0) {
                throw new RuntimeException("Um ou ambos os times já possuem jogo nesta rodada");
            }
        }
        
        if (jogoVO.getFinalizado() && (jogoVO.getGolsCasa() == null || jogoVO.getGolsVisitante() == null)) {
            throw new RuntimeException("Jogo finalizado deve ter placar definido");
        }
        
        if (jogoVO.getGolsCasa() != null && jogoVO.getGolsCasa() < 0) {
            throw new RuntimeException("Gols do time da casa não podem ser negativos");
        }
        
        if (jogoVO.getGolsVisitante() != null && jogoVO.getGolsVisitante() < 0) {
            throw new RuntimeException("Gols do time visitante não podem ser negativos");
        }
    }
    
    private void atualizarEstatisticas(Classificacao classificacao, Integer golsMarcados, Integer golsSofridos) {
        classificacao.setJogos(classificacao.getJogos() + 1);
        classificacao.setGolsPro(classificacao.getGolsPro() + golsMarcados);
        classificacao.setGolsContra(classificacao.getGolsContra() + golsSofridos);
        classificacao.setSaldoGols(classificacao.getGolsPro() - classificacao.getGolsContra());
        
        if (golsMarcados > golsSofridos) {
            classificacao.setVitorias(classificacao.getVitorias() + 1);
            classificacao.setPontos(classificacao.getPontos() + 3);
        } else if (golsMarcados.equals(golsSofridos)) {
            classificacao.setEmpates(classificacao.getEmpates() + 1);
            classificacao.setPontos(classificacao.getPontos() + 1);
        } else {
            classificacao.setDerrotas(classificacao.getDerrotas() + 1);
        }
    }
    
    private JogoVO converterParaVO(Jogo jogo) {
        return JogoVO.builder()
                .id(jogo.getId())
                .campeonatoId(jogo.getCampeonato().getId())
                .campeonatoNome(jogo.getCampeonato().getNome())
                .timeCasa(TimeVO.builder()
                        .id(jogo.getTimeCasa().getId())
                        .nome(jogo.getTimeCasa().getNome())
                        .cidade(jogo.getTimeCasa().getCidade())
                        .urlEscudo(jogo.getTimeCasa().getUrlEscudo())
                        .build())
                .timeVisitante(TimeVO.builder()
                        .id(jogo.getTimeVisitante().getId())
                        .nome(jogo.getTimeVisitante().getNome())
                        .cidade(jogo.getTimeVisitante().getCidade())
                        .urlEscudo(jogo.getTimeVisitante().getUrlEscudo())
                        .build())
                .rodada(jogo.getRodada())
                .data(jogo.getData())
                .golsCasa(jogo.getGolsCasa())
                .golsVisitante(jogo.getGolsVisitante())
                .finalizado(jogo.getFinalizado())
                .build();
    }
    
    private ClassificacaoVO converterClassificacaoParaVO(Classificacao classificacao) {
        return ClassificacaoVO.builder()
                .timeId(classificacao.getTimeId())
                .timeNome(classificacao.getTimeNome())
                .pontos(classificacao.getPontos())
                .jogos(classificacao.getJogos())
                .vitorias(classificacao.getVitorias())
                .empates(classificacao.getEmpates())
                .derrotas(classificacao.getDerrotas())
                .golsPro(classificacao.getGolsPro())
                .golsContra(classificacao.getGolsContra())
                .saldoGols(classificacao.getSaldoGols())
                .build();
    }
}
