package com.gestaofinanceira.service;

import com.gestaofinanceira.model.Relatorio;
import com.gestaofinanceira.model.Transacao;

import java.util.List;

public interface RelatorioFactory {
    Relatorio criarRelatorio(List<Transacao> todasTransacoes);
}
