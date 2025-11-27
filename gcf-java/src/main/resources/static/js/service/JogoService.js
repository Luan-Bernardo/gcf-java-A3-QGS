/** Service layer para gerenciamento de Jogos, Rodadas e Classificação via API REST. */
const JogoService = {
    
    async listarJogos() {
        const response = await fetch(`${API_BASE_URL}/jogos`);
        if (!response.ok) {
            throw new Error('Erro ao listar jogos');
        }
        return await response.json();
    },
    
    async buscarJogoPorId(id) {
        const response = await fetch(`${API_BASE_URL}/jogos/${id}`);
        if (!response.ok) {
            throw new Error('Erro ao buscar jogo');
        }
        return await response.json();
    },
    
    async salvarJogo(jogo) {
        const method = jogo.id ? 'PUT' : 'POST';
        const url = jogo.id ? `${API_BASE_URL}/jogos/${jogo.id}` : `${API_BASE_URL}/jogos`;
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jogo)
        });
        
        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(errorMessage || 'Erro ao salvar jogo');
        }
        return await response.json();
    },
    
    async excluirJogo(id) {
        const response = await fetch(`${API_BASE_URL}/jogos/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Erro ao excluir jogo');
        }
    },
    
    async listarJogosPorCampeonato(campeonatoId) {
        const response = await fetch(`${API_BASE_URL}/jogos/campeonato/${campeonatoId}`);
        if (!response.ok) {
            throw new Error('Erro ao listar jogos do campeonato');
        }
        return await response.json();
    },
    
    async listarJogosPorRodada(campeonatoId, rodada) {
        const response = await fetch(`${API_BASE_URL}/jogos/campeonato/${campeonatoId}/rodada/${rodada}`);
        if (!response.ok) {
            throw new Error('Erro ao listar jogos da rodada');
        }
        return await response.json();
    },
    
    async listarRodadas(campeonatoId) {
        const response = await fetch(`${API_BASE_URL}/jogos/campeonato/${campeonatoId}/rodadas`);
        if (!response.ok) {
            throw new Error('Erro ao listar rodadas');
        }
        return await response.json();
    },
    
    async obterClassificacao(campeonatoId) {
        const response = await fetch(`${API_BASE_URL}/jogos/campeonato/${campeonatoId}/classificacao`);
        if (!response.ok) {
            throw new Error('Erro ao obter classificação');
        }
        return await response.json();
    },
    
    async registrarResultado(jogoId, golsCasa, golsVisitante) {
        const response = await fetch(
            `${API_BASE_URL}/jogos/${jogoId}/resultado?golsCasa=${golsCasa}&golsVisitante=${golsVisitante}`,
            {
                method: 'PATCH'
            }
        );
        
        if (!response.ok) {
            throw new Error('Erro ao registrar resultado');
        }
        return await response.json();
    }
};
