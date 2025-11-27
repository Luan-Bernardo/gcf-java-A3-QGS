/**
 * Controller para gerenciamento de Times.
 */
document.addEventListener('DOMContentLoaded', () => {
    carregarTimes();
    configurarFormulario();
    configurarEventos();
});

function carregarTimes() {
    TimeService.listarTimes()
        .then(times => {
            const listaTimes = document.getElementById('listaTimes');
            listaTimes.innerHTML = '';
            
            times.forEach(time => {
                const row = document.createElement('tr');
                
                let nomeComEscudo = time.nome;
                if (time.urlEscudo) {
                    nomeComEscudo = `
                        <div class="time-nome-container">
                            <img src="${time.urlEscudo}" alt="Escudo ${time.nome}" class="escudo-time">
                            <span>${time.nome}</span>
                        </div>
                    `;
                }
                
                row.innerHTML = `
                    <td>${nomeComEscudo}</td>
                    <td>${time.cidade}</td>
                    <td class="actions">
                        <button class="btn btn-sm btn-edit" onclick="editarTime(${time.id})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-delete" onclick="excluirTime(${time.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                `;
                listaTimes.appendChild(row);
            });
            
            if (times.length === 0) {
                listaTimes.innerHTML = '<tr><td colspan="3" style="text-align: center;">Nenhum time cadastrado</td></tr>';
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            utils.mostrarMensagem('Erro ao carregar os times', 'error');
        });
}

function configurarFormulario() {
    const formTime = document.getElementById('formTime');
    
    formTime.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const time = {
            id: document.getElementById('timeId').value || null,
            nome: document.getElementById('nomeTime').value,
            cidade: document.getElementById('cidadeTime').value,
            urlEscudo: document.getElementById('urlEscudo').value || null
        };
        
        TimeService.salvarTime(time)
            .then(() => {
                utils.fecharModal('modalTime');
                carregarTimes();
                formTime.reset();
                
                const mensagem = time.id ? 'Time atualizado com sucesso!' : 'Time cadastrado com sucesso!';
                utils.mostrarMensagem(mensagem, 'success');
            })
            .catch(error => {
                console.error('Erro:', error);
                utils.mostrarMensagem('Erro ao salvar o time', 'error');
            });
    });
}

function configurarEventos() {
    const btnNovoTime = document.getElementById('btnNovoTime');
    
    btnNovoTime.addEventListener('click', () => {
        document.getElementById('modalTimeTitulo').textContent = 'Novo Time';
        document.getElementById('btnSalvarTime').textContent = 'Cadastrar';
        document.getElementById('formTime').reset();
        document.getElementById('timeId').value = '';
        utils.abrirModal('modalTime');
    });
}

function editarTime(id) {
    TimeService.buscarTimePorId(id)
        .then(time => {
            document.getElementById('timeId').value = time.id;
            document.getElementById('nomeTime').value = time.nome;
            document.getElementById('cidadeTime').value = time.cidade;
            document.getElementById('urlEscudo').value = time.urlEscudo || '';
            
            document.getElementById('modalTimeTitulo').textContent = 'Editar Time';
            document.getElementById('btnSalvarTime').textContent = 'Salvar';
            utils.abrirModal('modalTime');
        })
        .catch(error => {
            console.error('Erro:', error);
            utils.mostrarMensagem('Erro ao carregar os dados do time', 'error');
        });
}

function excluirTime(id) {
    utils.confirmar('Deseja realmente excluir este time?', () => {
        TimeService.excluirTime(id)
            .then(() => {
                carregarTimes();
                utils.mostrarMensagem('Time excluído com sucesso!', 'success');
            })
            .catch(error => {
                console.error('Erro:', error);
                utils.mostrarMensagem('Erro ao excluir o time', 'error');
            });
    });
}
