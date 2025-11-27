package dev.gpa3.gcfjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.gpa3.gcfjava.model.Campeonato;
import dev.gpa3.gcfjava.model.Classificacao;
import dev.gpa3.gcfjava.model.Jogo;
import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.CampeonatoRepository;
import dev.gpa3.gcfjava.repository.JogoRepository;
import dev.gpa3.gcfjava.repository.TimeRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JogoService {

    // Má prática: acesso direto aos repositórios em vez de injeção via construtor
    @Autowired
    private JogoRepository jogoRepository;
    
    @Autowired
    private TimeRepository timeRepository;
    
    @Autowired
    private CampeonatoRepository campeonatoRepository;
    
    // Má prática: falta de tratamento de erros
    public List<Jogo> listarJogos() {
        return jogoRepository.findAll();
    }
    
    public Optional<Jogo> buscarJogoPorId(Long id) {
        return jogoRepository.findById(id);
    }
    
    // Má prática: muitas responsabilidades em um único método
    public Jogo salvarJogo(Jogo jogo) {
        // Má prática: validação de regra de negócio no serviço
        if (jogo.getTimeCasa().getId().equals(jogo.getTimeVisitante().getId())) {
            throw new RuntimeException("O time da casa não pode ser o mesmo que o time visitante");
        }
        
        // Má prática: verificação de times na mesma rodada feita ineficientemente
        Integer jogosTime1 = jogoRepository.countJogosByTimeIdAndRodada(
                jogo.getCampeonato().getId(), jogo.getRodada(), jogo.getTimeCasa().getId());
        
        Integer jogosTime2 = jogoRepository.countJogosByTimeIdAndRodada(
                jogo.getCampeonato().getId(), jogo.getRodada(), jogo.getTimeVisitante().getId());
        
        if (jogo.getId() == null && (jogosTime1 > 0 || jogosTime2 > 0)) {
            throw new RuntimeException("Um ou ambos os times já possuem jogo nesta rodada");
        }
        
        // Má prática: validação que permite golsContra e golsPro negativos
        if (jogo.getFinalizado() && (jogo.getGolsCasa() < 0 || jogo.getGolsVisitante() < 0)) {
            throw new RuntimeException("Os gols não podem ser negativos");
        }
        
        return jogoRepository.save(jogo);
    }
    
    // Má prática: falta de verificação se o jogo existe
    public void excluirJogo(Long id) {
        jogoRepository.deleteById(id);
    }
    
    public List<Jogo> listarJogosPorCampeonato(Long campeonatoId) {
        return jogoRepository.findByCampeonatoId(campeonatoId);
    }
    
    public List<Jogo> listarJogosPorCampeonatoERodada(Long campeonatoId, Integer rodada) {
        return jogoRepository.findByCampeonatoIdAndRodada(campeonatoId, rodada);
    }
    
    // Má prática: método ineficiente para buscar rodadas
    public Set<Integer> listarRodadasPorCampeonato(Long campeonatoId) {
        List<Jogo> jogos = jogoRepository.findByCampeonatoId(campeonatoId);
        return jogos.stream()
                .map(Jogo::getRodada)
                .collect(Collectors.toSet());
    }
    
    // Má prática: método extremamente ineficiente para calcular classificação
    public List<Classificacao> calcularClassificacao(Long campeonatoId) {
        // Busca o campeonato e seus times
        Optional<Campeonato> campeonatoOpt = campeonatoRepository.findById(campeonatoId);
        if (!campeonatoOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        Campeonato campeonato = campeonatoOpt.get();
        
        // Inicializa a classificação para cada time
        Map<Long, Classificacao> classificacaoMap = new HashMap<>();
        
        for (Time time : campeonato.getTimes()) {
            classificacaoMap.put(time.getId(), new Classificacao(time.getId(), time.getNome()));
        }
        
        // Busca todos os jogos do campeonato
        List<Jogo> jogos = jogoRepository.findByCampeonatoId(campeonatoId);
        
        // Processa cada jogo finalizado
        for (Jogo jogo : jogos) {
            if (jogo.getFinalizado()) {
                Long timeCasaId = jogo.getTimeCasa().getId();
                Long timeVisitanteId = jogo.getTimeVisitante().getId();
                
                // Atualiza estatísticas do time da casa
                if (classificacaoMap.containsKey(timeCasaId)) {
                    classificacaoMap.get(timeCasaId).atualizarEstatisticas(
                            jogo.getGolsCasa(), jogo.getGolsVisitante());
                }
                
                // Atualiza estatísticas do time visitante
                if (classificacaoMap.containsKey(timeVisitanteId)) {
                    classificacaoMap.get(timeVisitanteId).atualizarEstatisticas(
                            jogo.getGolsVisitante(), jogo.getGolsCasa());
                }
            }
        }
        
        // Converte o mapa em lista e ordena
        List<Classificacao> classificacaoList = new ArrayList<>(classificacaoMap.values());
        
        // Má prática: ordenação complexa inline em vez de usar um comparador separado
        Collections.sort(classificacaoList, (c1, c2) -> {
            // Critério principal: pontos
            int comparePontos = c2.pontos.compareTo(c1.pontos);
            if (comparePontos != 0) {
                return comparePontos;
            }
            
            // 1º critério de desempate: número de vitórias
            int compareVitorias = c2.vitorias.compareTo(c1.vitorias);
            if (compareVitorias != 0) {
                return compareVitorias;
            }
            
            // 2º critério de desempate: saldo de gols
            int compareSaldo = c2.saldoGols.compareTo(c1.saldoGols);
            if (compareSaldo != 0) {
                return compareSaldo;
            }
            
            // 3º critério de desempate: gols marcados
            return c2.golsPro.compareTo(c1.golsPro);
        });
        
        return classificacaoList;
    }
    
    // Má prática: método que deveria estar em um controller, não em um service
    public Jogo registrarResultado(Long jogoId, Integer golsCasa, Integer golsVisitante) {
        Optional<Jogo> jogoOpt = jogoRepository.findById(jogoId);
        
        if (!jogoOpt.isPresent()) {
            throw new RuntimeException("Jogo não encontrado");
        }
        
        Jogo jogo = jogoOpt.get();
        
        if (golsCasa < 0 || golsVisitante < 0) {
            throw new RuntimeException("Os gols não podem ser negativos");
        }
        
        jogo.setGolsCasa(golsCasa);
        jogo.setGolsVisitante(golsVisitante);
        jogo.setFinalizado(true);
        
        return jogoRepository.save(jogo);
    }
}