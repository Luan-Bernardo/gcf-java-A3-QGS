package dev.gpa3.gcfjava.service;

import dev.gpa3.gcfjava.model.Time;
import dev.gpa3.gcfjava.repository.TimeRepository;
import dev.gpa3.gcfjava.vo.TimeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TimeService.
 * Valida a lógica de negócio dos métodos da camada de serviço.
 * 
 * Padrão AAA (Arrange-Act-Assert):
 * - Arrange: Preparar dados de teste
 * - Act: Executar método a ser testado
 * - Assert: Verificar resultado esperado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TimeService - Testes de Métodos")
class TimeServiceTest {

    @Mock
    private TimeRepository timeRepository;

    @InjectMocks
    private TimeService timeService;

    @Test
    @DisplayName("Deve listar todos os times cadastrados")
    void deveListarTodosOsTimes() {
        // Arrange - Preparar dados de teste
        Time time = Time.builder()
                .id(1L)
                .nome("Flamengo")
                .cidade("Rio de Janeiro")
                .build();
        when(timeRepository.findAll()).thenReturn(Arrays.asList(time));

        // Act - Executar método
        List<TimeVO> resultado = timeService.listarTimes();

        // Assert - Verificar resultado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Flamengo", resultado.get(0).getNome());
        assertEquals("Rio de Janeiro", resultado.get(0).getCidade());
    }

    @Test
    @DisplayName("Deve buscar time por ID válido")
    void deveBuscarTimePorId() {
        // Arrange
        Time time = Time.builder()
                .id(1L)
                .nome("Flamengo")
                .cidade("Rio de Janeiro")
                .build();
        when(timeRepository.findById(1L)).thenReturn(Optional.of(time));

        // Act
        TimeVO resultado = timeService.buscarTimePorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Flamengo", resultado.getNome());
        assertEquals("Rio de Janeiro", resultado.getCidade());
    }

    @Test
    @DisplayName("Deve lançar exceção quando time não encontrado")
    void deveLancarExcecaoQuandoTimeNaoEncontrado() {
        // Arrange - Simular ID inexistente
        when(timeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert - Verificar se exceção é lançada
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> timeService.buscarTimePorId(999L));
        
        assertTrue(exception.getMessage().contains("Time não encontrado"));
    }

    @Test
    @DisplayName("Deve salvar time com dados válidos")
    void deveSalvarTimeValido() {
        // Arrange - Preparar time para salvar
        TimeVO timeVO = TimeVO.builder()
                .nome("Palmeiras")
                .cidade("São Paulo")
                .build();

        Time timeSalvo = Time.builder()
                .id(1L)
                .nome("Palmeiras")
                .cidade("São Paulo")
                .build();
        when(timeRepository.save(any(Time.class))).thenReturn(timeSalvo);

        // Act - Salvar time
        TimeVO resultado = timeService.salvarTime(timeVO);

        // Assert - Verificar se foi salvo corretamente
        assertNotNull(resultado.getId());
        assertEquals("Palmeiras", resultado.getNome());
        assertEquals("São Paulo", resultado.getCidade());
        verify(timeRepository, times(1)).save(any(Time.class));
    }

    @Test
    @DisplayName("Deve rejeitar time com nome vazio")
    void deveRejeitarTimeComNomeVazio() {
        // Arrange - Time com nome inválido
        TimeVO timeInvalido = TimeVO.builder()
                .nome("")
                .cidade("São Paulo")
                .build();

        // Act & Assert - Verificar validação
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> timeService.salvarTime(timeInvalido));
        
        assertTrue(exception.getMessage().contains("Nome do time é obrigatório"));
        verify(timeRepository, never()).save(any(Time.class));
    }

    @Test
    @DisplayName("Deve excluir time existente")
    void deveExcluirTimeExistente() {
        // Arrange - Time existe no banco
        when(timeRepository.existsById(1L)).thenReturn(true);

        // Act - Excluir time
        assertDoesNotThrow(() -> timeService.excluirTime(1L));

        // Assert - Verificar se foi excluído
        verify(timeRepository, times(1)).deleteById(1L);
    }
}
