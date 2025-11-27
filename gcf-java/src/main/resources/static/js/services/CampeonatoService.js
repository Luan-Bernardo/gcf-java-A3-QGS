/** Service layer para gerenciamento de Campeonatos via API REST. */
const CampeonatoService = {
    
    listarCampeonatos() {
        return fetch(`${API_BASE_URL}/campeonatos`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erro ao carregar campeonatos');
                }
                return response.json();
            });
    },
    
    buscarCampeonatoPorId(id) {
        return fetch(`${API_BASE_URL}/campeonatos/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Erro ao buscar campeonato ${id}`);
                }
                return response.json();
            });
    },
    
    salvarCampeonato(campeonato) {
        const method = campeonato.id ? 'PUT' : 'POST';
        const url = campeonato.id 
            ? `${API_BASE_URL}/campeonatos/${campeonato.id}`
            : `${API_BASE_URL}/campeonatos`;
        
        return fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(campeonato)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorMessage => {
                    throw new Error(errorMessage || 'Erro ao salvar campeonato');
                });
            }
            return response.json();
        });
    },
    
    excluirCampeonato(id) {
        return fetch(`${API_BASE_URL}/campeonatos/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorMessage => {
                    throw new Error(errorMessage || `Erro ao excluir campeonato ${id}`);
                });
            }
        });
    },
    
    listarTimesDoCampeonato(campeonatoId) {
        return fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erro ao carregar times do campeonato');
                }
                return response.json();
            });
    },
    
    adicionarTimeCampeonato(campeonatoId, timeId) {
        return fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times/${timeId}`, {
            method: 'POST'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorMessage => {
                    throw new Error(errorMessage || 'Erro ao adicionar time ao campeonato');
                });
            }
            return response.json();
        });
    },
    
    removerTimeCampeonato(campeonatoId, timeId) {
        return fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times/${timeId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorMessage => {
                    throw new Error(errorMessage || 'Erro ao remover time do campeonato');
                });
            }
            return response.json();
        });
    }
};
