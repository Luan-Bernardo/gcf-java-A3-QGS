package dev.gpa3.gcfjava.service;

import dev.gpa3.gcfjava.model.Campeonato;
import dev.gpa3.gcfjava.model.Jogo;
import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.CampeonatoRepository;
import dev.gpa3.gcfjava.repository.JogoRepository;
import dev.gpa3.gcfjava.repository.TimeRepository;
import dev.gpa3.gcfjava.vo.JogoVO;
import dev.gpa3.gcfjava.vo.TimeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para JogoService.
 * Valida a lógica de negócio dos métodos da camada de serviço.
 * 
 * Foco: Validações de jogos, regras de negócio e integridade dos dados
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JogoService - Testes de Métodos")
class JogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;

    @Mock
    private CampeonatoRepository campeonatoRepository;

    @Mock
    private TimeRepository timeRepository;

    @InjectMocks
    private JogoService jogoService;

    @Test
    @DisplayName("Deve listar todos os jogos")
    void deveListarTodosJogos() {
        Campeonato campeonato = Campeonato.builder()
                .id(1L)
                .nome("Brasileirão")
                .build();

        Time timeCasa = Time.builder()
                .id(1L)
                .nome("Flamengo")
                .cidade("Rio")
                .build();

        Time timeVisitante = Time.builder()
                .id(2L)
                .nome("Palmeiras")
                .cidade("SP")
                .build();

        Jogo jogo = Jogo.builder()
                .id(1L)
                .campeonato(campeonato)
                .timeCasa(timeCasa)
                .timeVisitante(timeVisitante)
                .rodada(1)
                .finalizado(false)
                .build();

        when(jogoRepository.findAll()).thenReturn(Arrays.asList(jogo));

        List<JogoVO> resultado = jogoService.listarJogos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve salvar jogo válido")
    void deveSalvarJogoValido() {
        Campeonato campeonato = Campeonato.builder()
                .id(1L)
                .nome("Brasileirão")
                .build();

        Time timeCasa = Time.builder()
                .id(1L)
                .nome("Flamengo")
                .cidade("Rio")
                .build();

        Time timeVisitante = Time.builder()
                .id(2L)
                .nome("Palmeiras")
                .cidade("SP")
                .build();

        JogoVO jogoVO = JogoVO.builder()
                .campeonatoId(1L)
                .timeCasa(TimeVO.builder().id(1L).build())
                .timeVisitante(TimeVO.builder().id(2L).build())
                .rodada(1)
                .data(new Date())
                .finalizado(false)
                .golsCasa(0)
                .golsVisitante(0)
                .build();

        Jogo jogoSalvo = Jogo.builder()
                .id(1L)
                .campeonato(campeonato)
                .timeCasa(timeCasa)
                .timeVisitante(timeVisitante)
                .rodada(1)
                .finalizado(false)
                .build();

        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(timeRepository.findById(1L)).thenReturn(Optional.of(timeCasa));
        when(timeRepository.findById(2L)).thenReturn(Optional.of(timeVisitante));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(jogoSalvo);

        JogoVO resultado = jogoService.salvarJogo(jogoVO);

        assertNotNull(resultado);
        verify(jogoRepository).save(any(Jogo.class));
    }

    @Test
    @DisplayName("Deve rejeitar jogo com times iguais")
    void deveRejeitarJogoComTimesIguais() {
        JogoVO jogoInvalido = JogoVO.builder()
                .campeonatoId(1L)
                .timeCasa(TimeVO.builder().id(1L).build())
                .timeVisitante(TimeVO.builder().id(1L).build())
                .rodada(1)
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jogoService.salvarJogo(jogoInvalido));

        assertTrue(exception.getMessage().contains("não pode ser o mesmo"));
    }

    @Test
    @DisplayName("Deve rejeitar jogo com rodada inválida")
    void deveRejeitarJogoComRodadaInvalida() {
        JogoVO jogoInvalido = JogoVO.builder()
                .campeonatoId(1L)
                .timeCasa(TimeVO.builder().id(1L).build())
                .timeVisitante(TimeVO.builder().id(2L).build())
                .rodada(0)
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jogoService.salvarJogo(jogoInvalido));

        assertTrue(exception.getMessage().contains("Rodada inválida"));
    }

    @Test
    @DisplayName("Deve registrar resultado do jogo")
    void deveRegistrarResultado() {
        Campeonato campeonato = Campeonato.builder()
                .id(1L)
                .nome("Brasileirão")
                .build();

        Time timeCasa = Time.builder()
                .id(1L)
                .nome("Flamengo")
                .cidade("Rio")
                .build();

        Time timeVisitante = Time.builder()
                .id(2L)
                .nome("Palmeiras")
                .cidade("SP")
                .build();

        Jogo jogo = Jogo.builder()
                .id(1L)
                .campeonato(campeonato)
                .timeCasa(timeCasa)
                .timeVisitante(timeVisitante)
                .rodada(1)
                .finalizado(false)
                .build();

        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(jogo);

        JogoVO resultado = jogoService.registrarResultado(1L, 2, 1);

        assertNotNull(resultado);
        assertEquals(2, resultado.getGolsCasa());
        assertEquals(1, resultado.getGolsVisitante());
        assertTrue(resultado.getFinalizado());
    }

    @Test
    @DisplayName("Deve excluir jogo existente")
    void deveExcluirJogoExistente() {
        when(jogoRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> jogoService.excluirJogo(1L));

        verify(jogoRepository).deleteById(1L);
    }
}
