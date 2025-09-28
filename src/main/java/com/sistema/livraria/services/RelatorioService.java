package com.sistema.livraria.services;

public interface RelatorioService {
    byte[] gerarRelatorioLivrosPorCategoria();
    byte[] gerarRelatorioLivrosPorAutor();
    byte[] gerarRelatorioLivrosPorValor(double valorMinimo);
    byte[] gerarRelatorioGenerico(String tituloRelatorio);
    byte[] gerarRelatorioLivrosPorEditora();
    byte[] gerarRelatorioLivroPorStatus(String statusFiltro);
    byte[] gerarRelatorioLivrosPorEstoqueZerado();
}
