package dev.gpa3.gcfjava.service;

import dev.gpa3.gcfjava.model.Campeonato;
import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.CampeonatoRepository;
import dev.gpa3.gcfjava.repository.TimeRepository;
import dev.gpa3.gcfjava.vo.CampeonatoVO;
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
 * Testes unitários para CampeonatoService.
 * Valida a lógica de negócio dos métodos da camada de serviço.
 * 
 * Foco: Validações de dados e gerenciamento de relacionamentos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CampeonatoService - Testes de Métodos")
class CampeonatoServiceTest {

    @Mock
    private CampeonatoRepository campeonatoRepository;

    @Mock
    private TimeRepository timeRepository;

    @InjectMocks
    private CampeonatoService campeonatoService;

    @Test
    @DisplayName("Deve listar todos os campeonatos")
    void deveListarTodosCampeonatos() {
        Campeonato campeonato = Campeonato.builder()
                .id(1L)
                .nome("Brasileirão 2025")
                .ano(2025)
                .dataInicio(new Date())
                .times(new HashSet<>())
                .build();

        when(campeonatoRepository.findAll()).thenReturn(Arrays.asList(campeonato));

        List<CampeonatoVO> resultado = campeonatoService.listarCampeonatos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Brasileirão 2025", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve salvar campeonato válido")
    void deveSalvarCampeonatoValido() {
        CampeonatoVO campeonatoVO = CampeonatoVO.builder()
                .nome("Copa do Brasil")
                .ano(2025)
                .dataInicio(new Date())
                .build();

        Campeonato campeonatoSalvo = Campeonato.builder()
                .id(1L)
                .nome("Copa do Brasil")
                .ano(2025)
                .dataInicio(new Date())
                .times(new HashSet<>())
                .build();

        when(campeonatoRepository.save(any(Campeonato.class))).thenReturn(campeonatoSalvo);

        CampeonatoVO resultado = campeonatoService.salvarCampeonato(campeonatoVO);

        assertNotNull(resultado.getId());
        assertEquals("Copa do Brasil", resultado.getNome());
    }

    @Test
    @DisplayName("Deve rejeitar campeonato com nome muito curto")
    void deveRejeitarCampeonatoComNomeCurto() {
        CampeonatoVO campeonatoInvalido = CampeonatoVO.builder()
                .nome("AB")
                .ano(2025)
                .dataInicio(new Date())
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> campeonatoService.salvarCampeonato(campeonatoInvalido));

        assertTrue(exception.getMessage().contains("no mínimo 3 caracteres"));
    }

    @Test
    @DisplayName("Deve adicionar time ao campeonato")
    void deveAdicionarTimeCampeonato() {
        Campeonato campeonato = Campeonato.builder()
                .id(1L)
                .nome("Brasileirão")
                .ano(2025)
                .dataInicio(new Date())
                .times(new HashSet<>())
                .build();

        Time time = Time.builder()
                .id(1L)
                .nome("Flamengo")
                .cidade("Rio de Janeiro")
                .build();

        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(timeRepository.findById(1L)).thenReturn(Optional.of(time));
        when(campeonatoRepository.save(any(Campeonato.class))).thenReturn(campeonato);

        CampeonatoVO resultado = campeonatoService.adicionarTimeCampeonato(1L, 1L);

        assertNotNull(resultado);
        verify(campeonatoRepository).save(any(Campeonato.class));
    }

    @Test
    @DisplayName("Deve rejeitar time duplicado no campeonato")
    void deveRejeitarTimeDuplicado() {
        Time time = Time.builder()
                .id(1L)
                .nome("Flamengo")
                .cidade("Rio de Janeiro")
                .build();

        Set<Time> times = new HashSet<>();
        times.add(time);

        Campeonato campeonato = Campeonato.builder()
                .id(1L)
                .nome("Brasileirão")
                .ano(2025)
                .dataInicio(new Date())
                .times(times)
                .build();

        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(timeRepository.findById(1L)).thenReturn(Optional.of(time));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> campeonatoService.adicionarTimeCampeonato(1L, 1L));

        assertTrue(exception.getMessage().contains("já está participando"));
    }

    @Test
    @DisplayName("Deve excluir campeonato existente")
    void deveExcluirCampeonatoExistente() {
        Campeonato campeonato = Campeonato.builder()
                .id(1L)
                .nome("Brasileirão")
                .ano(2025)
                .dataInicio(new Date())
                .times(new HashSet<>())
                .build();

        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));

        assertDoesNotThrow(() -> campeonatoService.excluirCampeonato(1L));

        verify(campeonatoRepository).delete(campeonato);
    }
}
