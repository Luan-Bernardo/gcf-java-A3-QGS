/** Constantes e funções utilitárias compartilhadas. */

const API_BASE_URL = 'http://localhost:8080/api/v1';

const utils = {
    formatarData: (dateString) => {
        if (!dateString) return '';
        const data = new Date(dateString + 'T00:00:00');
        return data.toLocaleDateString('pt-BR');
    },
    
    formatarDataISO: (dateString) => {
        if (!dateString) return '';
        if (dateString instanceof Date) {
            const year = dateString.getFullYear();
            const month = String(dateString.getMonth() + 1).padStart(2, '0');
            const day = String(dateString.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        }
        if (dateString.includes('T')) {
            return dateString.split('T')[0];
        }
        return dateString;
    },
    
    abrirModal: (modalId) => {
        document.getElementById(modalId).style.display = 'block';
    },
    
    fecharModal: (modalId) => {
        document.getElementById(modalId).style.display = 'none';
    },
    
    configurarAbas: () => {
        document.querySelectorAll('.tab-link').forEach(tab => {
            tab.addEventListener('click', () => {
                document.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
                document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
                
                tab.classList.add('active');
                document.getElementById(tab.dataset.tab).classList.add('active');
            });
        });
    },
    
    getUrlParams: () => {
        const params = {};
        const queryString = window.location.search;
        const urlParams = new URLSearchParams(queryString);
        
        for (const [key, value] of urlParams.entries()) {
            params[key] = value;
        }
        
        return params;
    },
    
    mostrarMensagem: (mensagem, tipo = 'info') => {
        const toast = document.createElement('div');
        toast.className = `toast toast-${tipo}`;
        toast.textContent = mensagem;
        
        document.body.appendChild(toast);
        
        setTimeout(() => toast.classList.add('show'), 100);
        
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => document.body.removeChild(toast), 300);
        }, 3000);
    },
    
    confirmar: (mensagem, callback) => {
        const overlay = document.createElement('div');
        overlay.className = 'confirm-overlay';
        
        const dialog = document.createElement('div');
        dialog.className = 'confirm-dialog';
        
        const messageP = document.createElement('p');
        messageP.textContent = mensagem;
        messageP.className = 'confirm-message';
        
        const buttonsDiv = document.createElement('div');
        buttonsDiv.className = 'confirm-buttons';
        
        const btnCancelar = document.createElement('button');
        btnCancelar.textContent = 'Cancelar';
        btnCancelar.className = 'btn-cancelar';
        btnCancelar.onclick = () => document.body.removeChild(overlay);
        
        const btnConfirmar = document.createElement('button');
        btnConfirmar.textContent = 'Confirmar';
        btnConfirmar.className = 'btn-confirmar';
        btnConfirmar.onclick = () => {
            document.body.removeChild(overlay);
            callback();
        };
        
        buttonsDiv.appendChild(btnCancelar);
        buttonsDiv.appendChild(btnConfirmar);
        
        dialog.appendChild(messageP);
        dialog.appendChild(buttonsDiv);
        overlay.appendChild(dialog);
        
        document.body.appendChild(overlay);
    }
};

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.close').forEach(closeBtn => {
        closeBtn.addEventListener('click', () => {
            const modal = closeBtn.closest('.modal');
            modal.style.display = 'none';
        });
    });
    
    window.addEventListener('click', (event) => {
        document.querySelectorAll('.modal').forEach(modal => {
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        });
    });
});
