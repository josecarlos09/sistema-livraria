package com.sistema.livraria.services;

import com.sistema.livraria.dtos.LivroIsbnRecordDto;
import com.sistema.livraria.models.LivroModel;

/**
 * Interface que define os serviços relacionados aos livros na livraria.
 * Contém métodos para registrar e consultar livros através do ISBN.
 */
public interface LivrariaService {

    /**
     * Registra um livro no sistema a partir de seu ISBN, buscando dados via API externa.
     *
     * @param isbn Código ISBN do livro.
     * @param livroIsbnRecordDto DTO contendo informações adicionais para o cadastro.
     * @return O livro salvo com os dados vindos da API e os fornecidos no DTO.
     */
    LivroModel registrarLivroPorIsbn(String isbn, LivroIsbnRecordDto livroIsbnRecordDto);

    /**
     * Consulta um livro existente no banco de dados a partir do ISBN informado.
     *
     * @param isbn Código ISBN do livro (10 ou 13 dígitos).
     * @return O livro encontrado no banco de dados.
     */
    LivroModel consultarLivroPorIsbn(String isbn);
}
