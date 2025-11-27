let campeonatoId;

document.addEventListener('DOMContentLoaded', () => {
    const params = utils.getUrlParams();
    campeonatoId = params.id;
    
    if (!campeonatoId) {
        window.location.href = '../html/index.html';
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

// Carregar detalhes do campeonato
function carregarDetalhesCampeonato() {
    fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar campeonato');
            }
            return response.json();
        })
        .then(campeonato => {
            document.getElementById('campeonatoNome').textContent = campeonato.nome;
            document.getElementById('campeonatoAno').textContent = campeonato.ano;
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar os detalhes do campeonato.');
        });
}

// Carregar classificação
function carregarClassificacao() {
    fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/classificacao`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar classificação');
            }
            return response.json();
        })
        .then(classificacao => {
            const tabelaClassificacao = document.getElementById('tabelaClassificacao');
            tabelaClassificacao.innerHTML = '';
            
            classificacao.forEach((item, index) => {
                const row = document.createElement('tr');
                
                if (index === 0) {
                    row.classList.add('pos-1');
                }
                
                let saldoClass = item.saldoGols > 0 ? 'saldo-positivo' : 
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
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar a classificação.');
        });
}

// Carregar jogos
function carregarJogos() {
    fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/jogos`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar jogos');
            }
            return response.json();
        })
        .then(jogos => {
            const listaJogos = document.getElementById('listaJogos');
            listaJogos.innerHTML = '';
            
            jogos.forEach(jogo => {
                const row = document.createElement('tr');
                
                let placar = jogo.finalizado ? 
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
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar os jogos.');
        });
}

// Carregar rodadas
function carregarRodadas() {
    // Primeiro vamos buscar todas as rodadas disponíveis
    fetch(`${API_BASE_URL}/jogos/campeonato/${campeonatoId}/rodadas`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar rodadas');
            }
            return response.json();
        })
        .then(rodadas => {
            const listaRodadas = document.getElementById('listaRodadas');
            listaRodadas.innerHTML = '';
            
            if (rodadas.length === 0) {
                listaRodadas.innerHTML = '<p>Nenhuma rodada cadastrada</p>';
                return;
            }
            
            // Para cada rodada, vamos buscar os jogos
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
                    rodadaContent.classList.toggle('active');
                    const toggleIcon = rodadaHeader.querySelector('.toggle-icon');
                    toggleIcon.textContent = rodadaContent.classList.contains('active') ? '▲' : '▼';
                    
                    if (rodadaContent.classList.contains('active') && rodadaContent.innerHTML === '') {
                        carregarJogosRodada(rodada, rodadaContent);
                    }
                });
                
                rodadaContainer.appendChild(rodadaHeader);
                rodadaContainer.appendChild(rodadaContent);
                listaRodadas.appendChild(rodadaContainer);
            });
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar as rodadas.');
        });
}

// Carregar jogos de uma rodada específica
function carregarJogosRodada(rodada, rodadaContent) {
    fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/rodadas/${rodada}/jogos`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar jogos da rodada');
            }
            return response.json();
        })
        .then(jogos => {
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
                
                let placar = jogo.finalizado ? 
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
        })
        .catch(error => {
            console.error('Erro:', error);
            rodadaContent.innerHTML = '<p>Erro ao carregar jogos desta rodada</p>';
        });
}

// Carregar times do campeonato
function carregarTimesCampeonato() {
    fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar times');
            }
            return response.json();
        })
        .then(times => {
            const listaTimesCampeonato = document.getElementById('listaTimesCampeonato');
            listaTimesCampeonato.innerHTML = '';
            
            times.forEach(time => {
                const timeCard = document.createElement('div');
                timeCard.className = 'time-card';
                
                timeCard.innerHTML = `
                    <div class="time-info">
                        ${time.urlEscudo ? `<img src="${time.urlEscudo}" alt="Escudo ${time.nome}" class="time-escudo">` : ''}
                        <div>
                            <h3>${time.nome}</h3>
                            <p>${time.cidade}</p>
                        </div>
                    </div>
                    <button class="btn btn-sm btn-delete" onclick="removerTimeCampeonato(${time.id})">
                        <i class="fas fa-times"></i>
                    </button>
                `;
                
                listaTimesCampeonato.appendChild(timeCard);
            });
            
            if (times.length === 0) {
                listaTimesCampeonato.innerHTML = '<p>Nenhum time participante</p>';
            }
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar os times do campeonato.');
        });
}

// Configurar modal de jogo
function configurarModalJogo() {
    const formJogo = document.getElementById('formJogo');
    const jogoFinalizado = document.getElementById('jogoFinalizado');
    const divResultado = document.getElementById('divResultado');
    
    // Configurar evento de jogo finalizado
    jogoFinalizado.addEventListener('change', () => {
        divResultado.style.display = jogoFinalizado.checked ? 'block' : 'none';
    });
    
    // Configurar evento de submit do formulário
    formJogo.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const jogoId = document.getElementById('jogoId').value;
        const timeCasaId = document.getElementById('timeCasa').value;
        const timeVisitanteId = document.getElementById('timeVisitante').value;
        const rodada = document.getElementById('rodada').value;
        const dataJogo = document.getElementById('dataJogo').value;
        const finalizado = document.getElementById('jogoFinalizado').checked;
        const golsCasa = document.getElementById('golsCasa').value;
        const golsVisitante = document.getElementById('golsVisitante').value;
        
        // Validar times diferentes
        if (timeCasaId === timeVisitanteId) {
            alert('O time da casa e o visitante não podem ser o mesmo!');
            return;
        }
        
        const jogo = {
            campeonato: { id: campeonatoId },
            timeCasa: { id: timeCasaId },
            timeVisitante: { id: timeVisitanteId },
            rodada: parseInt(rodada),
            data: dataJogo,
            finalizado: finalizado,
            golsCasa: parseInt(golsCasa),
            golsVisitante: parseInt(golsVisitante)
        };
        
        const method = jogoId ? 'PUT' : 'POST';
        const url = jogoId ? `${API_BASE_URL}/jogos/${jogoId}` : `${API_BASE_URL}/jogos`;
        
        if (jogoId) {
            jogo.id = parseInt(jogoId);
        }
        
        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jogo)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao salvar jogo');
            }
            return response.json();
        })
        .then(() => {
            utils.fecharModal('modalJogo');
            carregarJogos();
            carregarRodadas();
            carregarClassificacao();
            formJogo.reset();
            document.getElementById('jogoId').value = '';
            document.getElementById('divResultado').style.display = 'none';
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao salvar o jogo.');
        });
    });
}

// Configurar eventos
function configurarEventos() {
    const btnNovoJogo = document.getElementById('btnNovoJogo');
    const btnAdicionarTime = document.getElementById('btnAdicionarTime');
    const btnConfirmarAdicionarTime = document.getElementById('btnConfirmarAdicionarTime');
    
    // Botão novo jogo
    btnNovoJogo.addEventListener('click', () => {
        document.getElementById('modalJogoTitulo').textContent = 'Novo Jogo';
        document.getElementById('btnSalvarJogo').textContent = 'Cadastrar';
        document.getElementById('formJogo').reset();
        document.getElementById('jogoId').value = '';
        document.getElementById('divResultado').style.display = 'none';
        
        // Carregar times para o select
        carregarTimesSelect();
        
        // Definir data padrão como hoje
        const hoje = new Date();
        document.getElementById('dataJogo').value = utils.formatarDataISO(hoje);
        
        utils.abrirModal('modalJogo');
    });
    
    // Botão adicionar time ao campeonato
    btnAdicionarTime.addEventListener('click', () => {
        carregarTimesDisponiveis();
        utils.abrirModal('modalAdicionarTime');
    });
    
    // Botão confirmar adicionar time
    btnConfirmarAdicionarTime.addEventListener('click', () => {
        const timeId = document.getElementById('selectTime').value;
        
        if (!timeId) {
            alert('Selecione um time!');
            return;
        }
        
        adicionarTimeCampeonato(timeId);
    });
}

// Carregar times para o select do modal de jogo
function carregarTimesSelect() {
    return new Promise((resolve, reject) => {
        fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erro ao carregar times');
                }
                return response.json();
            })
            .then(times => {
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
                resolve(times);
            })
            .catch(error => {
                console.error('Erro:', error);
                alert('Erro ao carregar os times.');
                reject(error);
            });
    });
}

// Carregar times disponíveis para adicionar ao campeonato
function carregarTimesDisponiveis() {
    // Primeiro carregamos todos os times
    fetch(`${API_BASE_URL}/times`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar times');
            }
            return response.json();
        })
        .then(todosTimes => {
            // Depois carregamos os times já no campeonato
            fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erro ao carregar times do campeonato');
                    }
                    return response.json();
                })
                .then(timesCampeonato => {
                    // Filtramos os times que ainda não estão no campeonato
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
                })
                .catch(error => {
                    console.error('Erro:', error);
                    alert('Erro ao carregar os times do campeonato.');
                });
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar os times.');
        });
}

// Função para editar jogo
function editarJogo(id) {
    fetch(`${API_BASE_URL}/jogos/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar jogo');
            }
            return response.json();
        })
        .then(jogo => {
            document.getElementById('jogoId').value = jogo.id;
            document.getElementById('rodada').value = jogo.rodada;
            document.getElementById('dataJogo').value = utils.formatarDataISO(jogo.data);
            document.getElementById('jogoFinalizado').checked = jogo.finalizado;
            document.getElementById('golsCasa').value = jogo.golsCasa;
            document.getElementById('golsVisitante').value = jogo.golsVisitante;
            document.getElementById('divResultado').style.display = jogo.finalizado ? 'block' : 'none';
            
            // Carregar times para o select
            carregarTimesSelect().then(() => {
                document.getElementById('timeCasa').value = jogo.timeCasa.id;
                document.getElementById('timeVisitante').value = jogo.timeVisitante.id;
            });
            
            document.getElementById('modalJogoTitulo').textContent = 'Editar Jogo';
            document.getElementById('btnSalvarJogo').textContent = 'Salvar';
            utils.abrirModal('modalJogo');
        })
        .catch(error => {
            console.error('Erro:', error);
            alert('Erro ao carregar os dados do jogo.');
        });
}

// Função para excluir jogo
function excluirJogo(id) {
    if (!confirm('Deseja realmente excluir este jogo?')) {
        return;
    }
    
    fetch(`${API_BASE_URL}/jogos/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao excluir jogo');
        }
        carregarJogos();
        carregarRodadas();
        carregarClassificacao();
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao excluir o jogo.');
    });
}

// Função para adicionar time ao campeonato
function adicionarTimeCampeonato(timeId) {
    fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times/${timeId}`, {
        method: 'POST'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao adicionar time');
        }
        return response.json();
    })
    .then(() => {
        utils.fecharModal('modalAdicionarTime');
        carregarTimesCampeonato();
        carregarTimesSelect();
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao adicionar o time ao campeonato.');
    });
}

// Função para remover time do campeonato
function removerTimeCampeonato(timeId) {
    if (!confirm('Deseja realmente remover este time do campeonato?')) {
        return;
    }
    
    fetch(`${API_BASE_URL}/campeonatos/${campeonatoId}/times/${timeId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Erro ao remover time');
        }
        return response.json();
    })
    .then(() => {
        carregarTimesCampeonato();
        carregarTimesSelect();
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro ao remover o time do campeonato.');
    });
}