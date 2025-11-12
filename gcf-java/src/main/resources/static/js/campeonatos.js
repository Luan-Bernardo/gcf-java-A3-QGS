document.addEventListener('DOMContentLoaded', () => {
    carregarCampeonatos();
    configurarFormulario();
    configurarEventos();
});

// Função para carregar a lista de campeonatos
function carregarCampeonatos() {
    fetch(`${API_BASE_URL}/campeonatos`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar campeonatos');
            }
            return response.json();
        })
        .then(campeonatos => {
            const listaCampeonatos = document.getElementById('listaCampeonatos');
            listaCampeonatos.innerHTML = '';
            
            campeonatos.forEach(campeonato => {
                const card = document.createElement('div');
                card.className = 'card';
                
                card.innerHTML = `
                    <div class="card-content">
                        <h2 class="card-title">
                            <i class="fas fa-trophy"></i>
                            ${campeonato.nome}
                        </h2>
                        <p>${campeonato.ano}</p>
                        <p>${campeonato.times.length} times participantes</p>
                        <p>Início: ${utils.formatarData(campeonato.dataInicio)}</p>
                        <a href="../html/campeonato.html?id=${campeonato.id}" class="btn">Ver detalhes</a>
                    </div>
                `;
                
                listaCampeonatos.appendChild(card);
            });
            
            if (campeonatos.length === 0) {
                listaCampeonatos.innerHTML = '<p>Nenhum campeonato cadastrado.</p>';
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar os campeonatos.');
        });
}

// Configurar formulário de campeonato
function configurarFormulario() {
    const formCampeonato = document.getElementById('formCampeonato');
    const dataInicio = document.getElementById('dataInicio');
    
    // Definir data padrão como hoje
    const hoje = new Date();
    dataInicio.value = utils.formatarDataISO(hoje);
    
    formCampeonato.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const campeonatoId = document.getElementById('campeonatoId').value;
        const nome = document.getElementById('nomeCampeonato').value;
        const ano = parseInt(document.getElementById('anoCampeonato').value);
        const dataInicioStr = document.getElementById('dataInicio').value;
        
        const campeonato = {
            nome,
            ano,
            dataInicio: dataInicioStr,
            times: []
        };
        
        const method = campeonatoId ? 'PUT' : 'POST';
        const url = campeonatoId ? `${API_BASE_URL}/campeonatos/${campeonatoId}` : `${API_BASE_URL}/campeonatos`;
        
        if (campeonatoId) {
            campeonato.id = parseInt(campeonatoId);
        }
        
        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(campeonato)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao salvar campeonato');
            }
            return response.json();
        })
        .then(() => {
            utils.fecharModal('modalCampeonato');
            carregarCampeonatos();
            formCampeonato.reset();
            document.getElementById('campeonatoId').value = '';
            dataInicio.value = utils.formatarDataISO(hoje);
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao salvar o campeonato.');
        });
    });
}

// Configurar eventos
function configurarEventos() {
    const btnNovoCampeonato = document.getElementById('btnNovoCampeonato');
    
    btnNovoCampeonato.addEventListener('click', () => {
        document.getElementById('modalCampeonatoTitulo').textContent = 'Criar Novo Campeonato';
        document.getElementById('btnSalvarCampeonato').textContent = 'Criar Campeonato';
        document.getElementById('formCampeonato').reset();
        document.getElementById('campeonatoId').value = '';
        
        // Definir data padrão como hoje
        const hoje = new Date();
        document.getElementById('dataInicio').value = utils.formatarDataISO(hoje);
        
        utils.abrirModal('modalCampeonato');
    });
}