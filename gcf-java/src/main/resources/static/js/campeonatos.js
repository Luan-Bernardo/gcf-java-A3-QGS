/**
 * Controller para gerenciamento de Campeonatos.
 */
document.addEventListener('DOMContentLoaded', () => {
    carregarCampeonatos();
    configurarFormulario();
    configurarEventos();
});

function carregarCampeonatos() {
    CampeonatoService.listarCampeonatos()
        .then(campeonatos => {
            const listaCampeonatos = document.getElementById('listaCampeonatos');
            listaCampeonatos.innerHTML = '';
            
            campeonatos.forEach(campeonato => {
                const card = document.createElement('div');
                card.className = 'card';
                
                card.innerHTML = `
                    <div class="card-content">
                        <div style="display: flex; justify-content: space-between; align-items: start;">
                            <h2 class="card-title" style="margin: 0;">
                                <i class="fas fa-trophy"></i>
                                ${campeonato.nome}
                            </h2>
                            <button class="btn btn-sm btn-delete" onclick="excluirCampeonato(${campeonato.id})" title="Excluir campeonato">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                        <p><strong>Ano:</strong> ${campeonato.ano}</p>
                        <p><strong>Times:</strong> ${campeonato.times.length} participantes</p>
                        <p><strong>Início:</strong> ${utils.formatarData(campeonato.dataInicio)}</p>
                        <a href="/html/campeonato.html?id=${campeonato.id}" class="btn">Ver detalhes</a>
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
            utils.mostrarMensagem('Erro ao carregar os campeonatos', 'error');
        });
}

function configurarFormulario() {
    const formCampeonato = document.getElementById('formCampeonato');
    const dataInicio = document.getElementById('dataInicio');
    
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
            dataInicio: dataInicioStr ? `${dataInicioStr}T12:00:00` : null,
            times: []
        };
        
        if (campeonatoId) {
            campeonato.id = parseInt(campeonatoId);
        }
        
        CampeonatoService.salvarCampeonato(campeonato)
            .then(() => {
                utils.fecharModal('modalCampeonato');
                carregarCampeonatos();
                formCampeonato.reset();
                document.getElementById('campeonatoId').value = '';
                dataInicio.value = utils.formatarDataISO(hoje);
                
                utils.mostrarMensagem('Campeonato salvo com sucesso!', 'success');
            })
            .catch(error => {
                console.error('Erro:', error);
                utils.mostrarMensagem(error.message || 'Erro ao salvar o campeonato', 'error');
            });
    });
}

function configurarEventos() {
    const btnNovoCampeonato = document.getElementById('btnNovoCampeonato');
    
    btnNovoCampeonato.addEventListener('click', () => {
        document.getElementById('modalCampeonatoTitulo').textContent = 'Criar Novo Campeonato';
        document.getElementById('btnSalvarCampeonato').textContent = 'Criar Campeonato';
        document.getElementById('formCampeonato').reset();
        document.getElementById('campeonatoId').value = '';
        
        const hoje = new Date();
        document.getElementById('dataInicio').value = utils.formatarDataISO(hoje);
        
        utils.abrirModal('modalCampeonato');
    });
}

function excluirCampeonato(id) {
    utils.confirmar('Deseja realmente excluir este campeonato? Esta ação não pode ser desfeita e todos os jogos associados serão excluídos.', () => {
        CampeonatoService.excluirCampeonato(id)
            .then(() => {
                carregarCampeonatos();
                utils.mostrarMensagem('Campeonato excluído com sucesso!', 'success');
            })
            .catch(error => {
                console.error('Erro:', error);
                utils.mostrarMensagem(error.message || 'Erro ao excluir o campeonato', 'error');
            });
    });
}
