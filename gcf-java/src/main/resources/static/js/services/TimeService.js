/** Service layer para gerenciamento de Times via API REST. */
const TimeService = {
    
    listarTimes() {
        return fetch(`${API_BASE_URL}/times`)
            .then(response => {
                if (!response.ok) throw new Error('Erro ao carregar times');
                return response.json();
            });
    },
    
    buscarTimePorId(id) {
        return fetch(`${API_BASE_URL}/times/${id}`)
            .then(response => {
                if (!response.ok) throw new Error('Time não encontrado');
                return response.json();
            });
    },
    
    salvarTime(time) {
        const method = time.id ? 'PUT' : 'POST';
        const url = time.id ? 
            `${API_BASE_URL}/times/${time.id}` : 
            `${API_BASE_URL}/times`;
        
        return fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(time)
        }).then(response => {
            if (!response.ok) throw new Error('Erro ao salvar time');
            return response.json();
        });
    },
    
    excluirTime(id) {
        return fetch(`${API_BASE_URL}/times/${id}`, {
            method: 'DELETE'
        }).then(response => {
            if (!response.ok) throw new Error('Erro ao excluir time');
        });
    }
};
