/** Controller para gerenciamento de detalhes de Campeonato (Classificação, Jogos, Rodadas e Times). */

let campeonatoId;

document.addEventListener('DOMContentLoaded', () => {
    const params = utils.getUrlParams();
    campeonatoId = params.id;
    
    if (!campeonatoId) {
        window.location.href = '/html/index.html';
        return;
    }
    
    carregarDetalhesCampeonato();
    carregarClassificacao();
    carregarJogos();
    carregarRodadas();
    carregarTimesCampeonato();
    
    utils.configurarAbas();
    configurarModalJogo();
    configurarEventos();
});

async function carregarDetalhesCampeonato() {
    try {
        const campeonato = await CampeonatoService.buscarCampeonatoPorId(campeonatoId);
        document.getElementById('campeonatoNome').textContent = campeonato.nome;
        document.getElementById('campeonatoAno').textContent = campeonato.ano;
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar os detalhes do campeonato', 'error');
    }
}

async function carregarClassificacao() {
    try {
        const classificacao = await JogoService.obterClassificacao(campeonatoId);
        const tabelaClassificacao = document.getElementById('tabelaClassificacao');
        tabelaClassificacao.innerHTML = '';
        
        classificacao.forEach((item, index) => {
            const row = document.createElement('tr');
            
            if (index === 0) {
                row.classList.add('pos-1');
            }
            
            const saldoClass = item.saldoGols > 0 ? 'saldo-positivo' : 
                            (item.saldoGols < 0 ? 'saldo-negativo' : '');
            
            row.innerHTML = `
                <td>${index + 1}</td>
                <td>${item.timeNome}</td>
                <td>${item.pontos}</td>
                <td>${item.jogos}</td>
                <td>${item.vitorias}</td>
                <td>${item.empates}</td>
                <td>${item.derrotas}</td>
                <td>${item.golsPro}</td>
                <td>${item.golsContra}</td>
                <td class="${saldoClass}">${item.saldoGols}</td>
            `;
            
            tabelaClassificacao.appendChild(row);
        });
        
        if (classificacao.length === 0) {
            tabelaClassificacao.innerHTML = '<tr><td colspan="10" style="text-align: center;">Nenhum time na classificação</td></tr>';
        }
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar a classificação', 'error');
    }
}

async function carregarJogos() {
    try {
        const jogos = await JogoService.listarJogosPorCampeonato(campeonatoId);
        const listaJogos = document.getElementById('listaJogos');
        listaJogos.innerHTML = '';
        
        jogos.forEach(jogo => {
            const row = document.createElement('tr');
            
            const placar = jogo.finalizado ? 
                `${jogo.golsCasa} x ${jogo.golsVisitante}` : 
                '-';
                
            row.innerHTML = `
                <td>${jogo.rodada}ª</td>
                <td>${utils.formatarData(jogo.data)}</td>
                <td>${jogo.timeCasa.nome} x ${jogo.timeVisitante.nome}</td>
                <td>${placar}</td>
                <td class="actions">
                    <button class="btn btn-sm btn-edit" onclick="editarJogo(${jogo.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-delete" onclick="excluirJogo(${jogo.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            
            listaJogos.appendChild(row);
        });
        
        if (jogos.length === 0) {
            listaJogos.innerHTML = '<tr><td colspan="5" style="text-align: center;">Nenhum jogo cadastrado</td></tr>';
        }
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar os jogos', 'error');
    }
}

async function carregarRodadas() {
    try {
        const rodadas = await JogoService.listarRodadas(campeonatoId);
        const listaRodadas = document.getElementById('listaRodadas');
        listaRodadas.innerHTML = '';
        
        if (rodadas.length === 0) {
            listaRodadas.innerHTML = '<p>Nenhuma rodada cadastrada</p>';
            return;
        }
        
        rodadas.sort((a, b) => a - b).forEach(rodada => {
            const rodadaContainer = document.createElement('div');
            rodadaContainer.className = 'rodada-container';
            
            const rodadaHeader = document.createElement('div');
            rodadaHeader.className = 'rodada-header';
            rodadaHeader.innerHTML = `<h3>Rodada ${rodada} <span class="toggle-icon">▼</span></h3>`;
            
            const rodadaContent = document.createElement('div');
            rodadaContent.className = 'rodada-content';
            rodadaContent.id = `rodada-${rodada}`;
            
            rodadaHeader.addEventListener('click', () => {
                const estaAberta = rodadaContent.classList.contains('active');
                const toggleIcon = rodadaHeader.querySelector('.toggle-icon');
                
                if (estaAberta) {
                    rodadaContent.classList.remove('active');
                    toggleIcon.textContent = '▼';
                } else {
                    rodadaContent.classList.add('active');
                    toggleIcon.textContent = '▲';
                    if (rodadaContent.innerHTML === '') {
                        carregarJogosRodada(rodada, rodadaContent);
                    }
                }
            });
            
            rodadaContainer.appendChild(rodadaHeader);
            rodadaContainer.appendChild(rodadaContent);
            listaRodadas.appendChild(rodadaContainer);
        });
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar as rodadas', 'error');
    }
}

async function carregarJogosRodada(rodada, rodadaContent) {
    try {
        const jogos = await JogoService.listarJogosPorRodada(campeonatoId, rodada);
        rodadaContent.innerHTML = '';
        
        const table = document.createElement('table');
        table.innerHTML = `
            <thead>
                <tr>
                    <th>Data</th>
                    <th>Confronto</th>
                    <th>Placar</th>
                </tr>
            </thead>
            <tbody id="jogos-rodada-${rodada}">
            </tbody>
        `;
        
        const tbody = table.querySelector(`#jogos-rodada-${rodada}`);
        
        jogos.forEach(jogo => {
            const row = document.createElement('tr');
            
            const placar = jogo.finalizado ? 
                `${jogo.golsCasa} x ${jogo.golsVisitante}` : 
                '-';
                
            row.innerHTML = `
                <td>${utils.formatarData(jogo.data)}</td>
                <td>${jogo.timeCasa.nome} x ${jogo.timeVisitante.nome}</td>
                <td>${placar}</td>
            `;
            
            tbody.appendChild(row);
        });
        
        if (jogos.length === 0) {
            table.innerHTML = '<p>Nenhum jogo nesta rodada</p>';
        }
        
        rodadaContent.appendChild(table);
    } catch (error) {
        console.error('Erro:', error);
        rodadaContent.innerHTML = '<p>Erro ao carregar jogos desta rodada</p>';
    }
}

async function carregarTimesCampeonato() {
    try {
        const times = await CampeonatoService.listarTimesDoCampeonato(campeonatoId);
        const listaTimesCampeonato = document.getElementById('listaTimesCampeonato');
        listaTimesCampeonato.innerHTML = '';
        
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
                    <button class="btn btn-sm btn-delete" onclick="removerTimeCampeonato(${time.id})">
                        <i class="fas fa-times"></i>
                    </button>
                </td>
            `;
            
            listaTimesCampeonato.appendChild(row);
        });
        
        if (times.length === 0) {
            listaTimesCampeonato.innerHTML = '<tr><td colspan="3" style="text-align: center;">Nenhum time participante</td></tr>';
        }
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar os times do campeonato', 'error');
    }
}

function configurarModalJogo() {
    const formJogo = document.getElementById('formJogo');
    const jogoFinalizado = document.getElementById('jogoFinalizado');
    const divResultado = document.getElementById('divResultado');
    
    jogoFinalizado.addEventListener('change', () => {
        divResultado.style.display = jogoFinalizado.checked ? 'block' : 'none';
    });
    
    formJogo.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const jogoId = document.getElementById('jogoId').value;
        const timeCasaId = document.getElementById('timeCasa').value;
        const timeVisitanteId = document.getElementById('timeVisitante').value;
        const rodada = document.getElementById('rodada').value;
        const dataJogo = document.getElementById('dataJogo').value;
        const finalizado = document.getElementById('jogoFinalizado').checked;
        const golsCasa = document.getElementById('golsCasa').value;
        const golsVisitante = document.getElementById('golsVisitante').value;
        
        if (timeCasaId === timeVisitanteId) {
            utils.mostrarMensagem('O time da casa e o visitante não podem ser o mesmo', 'warning');
            return;
        }
        
        const jogo = {
            campeonatoId: campeonatoId,
            timeCasa: { id: timeCasaId },
            timeVisitante: { id: timeVisitanteId },
            rodada: parseInt(rodada),
            data: dataJogo ? `${dataJogo}T12:00:00` : null,
            finalizado: finalizado,
            golsCasa: finalizado ? parseInt(golsCasa) || 0 : 0,
            golsVisitante: finalizado ? parseInt(golsVisitante) || 0 : 0
        };
        
        if (jogoId) {
            jogo.id = parseInt(jogoId);
        }
        
        try {
            await JogoService.salvarJogo(jogo);
            utils.fecharModal('modalJogo');
            utils.mostrarMensagem('Jogo salvo com sucesso', 'success');
            carregarJogos();
            carregarRodadas();
            carregarClassificacao();
            formJogo.reset();
            document.getElementById('jogoId').value = '';
            document.getElementById('divResultado').style.display = 'none';
        } catch (error) {
            console.error('Erro:', error);
            utils.mostrarMensagem(error.message || 'Erro ao salvar o jogo', 'error');
        }
    });
}

function configurarEventos() {
    const btnNovoJogo = document.getElementById('btnNovoJogo');
    const btnAdicionarTime = document.getElementById('btnAdicionarTime');
    const btnConfirmarAdicionarTime = document.getElementById('btnConfirmarAdicionarTime');
    
    btnNovoJogo.addEventListener('click', () => {
        document.getElementById('modalJogoTitulo').textContent = 'Novo Jogo';
        document.getElementById('btnSalvarJogo').textContent = 'Cadastrar';
        document.getElementById('formJogo').reset();
        document.getElementById('jogoId').value = '';
        document.getElementById('divResultado').style.display = 'none';
        
        carregarTimesSelect();
        
        const hoje = new Date();
        document.getElementById('dataJogo').value = utils.formatarDataISO(hoje);
        
        utils.abrirModal('modalJogo');
    });
    
    btnAdicionarTime.addEventListener('click', () => {
        carregarTimesDisponiveis();
        utils.abrirModal('modalAdicionarTime');
    });
    
    btnConfirmarAdicionarTime.addEventListener('click', () => {
        const timeId = document.getElementById('selectTime').value;
        
        if (!timeId) {
            utils.mostrarMensagem('Selecione um time', 'warning');
            return;
        }
        
        adicionarTimeCampeonato(timeId);
    });
}

async function carregarTimesSelect() {
    try {
        const times = await CampeonatoService.listarTimesDoCampeonato(campeonatoId);
        const selectTimeCasa = document.getElementById('timeCasa');
        const selectTimeVisitante = document.getElementById('timeVisitante');
        
        selectTimeCasa.innerHTML = '<option value="">Selecione o time</option>';
        selectTimeVisitante.innerHTML = '<option value="">Selecione o time</option>';
        
        times.forEach(time => {
            const optionCasa = document.createElement('option');
            optionCasa.value = time.id;
            optionCasa.textContent = time.nome;
            selectTimeCasa.appendChild(optionCasa);
            
            const optionVisitante = document.createElement('option');
            optionVisitante.value = time.id;
            optionVisitante.textContent = time.nome;
            selectTimeVisitante.appendChild(optionVisitante);
        });
        
        return times;
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar os times', 'error');
        throw error;
    }
}

async function carregarTimesDisponiveis() {
    try {
        const [todosTimes, timesCampeonato] = await Promise.all([
            TimeService.listarTimes(),
            CampeonatoService.listarTimesDoCampeonato(campeonatoId)
        ]);
        
        const timesDisponiveis = todosTimes.filter(time => {
            return !timesCampeonato.some(tc => tc.id === time.id);
        });
        
        const selectTime = document.getElementById('selectTime');
        selectTime.innerHTML = '<option value="">Selecione o time</option>';
        
        timesDisponiveis.forEach(time => {
            const option = document.createElement('option');
            option.value = time.id;
            option.textContent = time.nome;
            selectTime.appendChild(option);
        });
        
        if (timesDisponiveis.length === 0) {
            selectTime.innerHTML = '<option value="">Todos os times já estão no campeonato</option>';
        }
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar os times disponíveis', 'error');
    }
}

async function editarJogo(id) {
    try {
        const jogo = await JogoService.buscarJogoPorId(id);
        
        document.getElementById('jogoId').value = jogo.id;
        document.getElementById('rodada').value = jogo.rodada;
        document.getElementById('dataJogo').value = utils.formatarDataISO(jogo.data);
        document.getElementById('jogoFinalizado').checked = jogo.finalizado;
        document.getElementById('golsCasa').value = jogo.golsCasa;
        document.getElementById('golsVisitante').value = jogo.golsVisitante;
        document.getElementById('divResultado').style.display = jogo.finalizado ? 'block' : 'none';
        
        await carregarTimesSelect();
        document.getElementById('timeCasa').value = jogo.timeCasa.id;
        document.getElementById('timeVisitante').value = jogo.timeVisitante.id;
        
        document.getElementById('modalJogoTitulo').textContent = 'Editar Jogo';
        document.getElementById('btnSalvarJogo').textContent = 'Salvar';
        utils.abrirModal('modalJogo');
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao carregar os dados do jogo', 'error');
    }
}

async function excluirJogo(id) {
    utils.confirmar('Deseja realmente excluir este jogo?', async () => {
        try {
            await JogoService.excluirJogo(id);
            utils.mostrarMensagem('Jogo excluído com sucesso', 'success');
            carregarJogos();
            carregarRodadas();
            carregarClassificacao();
        } catch (error) {
            console.error('Erro:', error);
            utils.mostrarMensagem('Erro ao excluir o jogo', 'error');
        }
    });
}

async function adicionarTimeCampeonato(timeId) {
    try {
        await CampeonatoService.adicionarTimeCampeonato(campeonatoId, timeId);
        utils.fecharModal('modalAdicionarTime');
        utils.mostrarMensagem('Time adicionado com sucesso', 'success');
        carregarTimesCampeonato();
        carregarTimesSelect();
    } catch (error) {
        console.error('Erro:', error);
        utils.mostrarMensagem('Erro ao adicionar o time ao campeonato', 'error');
    }
}

async function removerTimeCampeonato(timeId) {
    utils.confirmar('Deseja realmente remover este time do campeonato?', async () => {
        try {
            await CampeonatoService.removerTimeCampeonato(campeonatoId, timeId);
            utils.mostrarMensagem('Time removido com sucesso', 'success');
            carregarTimesCampeonato();
            carregarTimesSelect();
        } catch (error) {
            console.error('Erro:', error);
            utils.mostrarMensagem('Erro ao remover o time do campeonato', 'error');
        }
    });
}
