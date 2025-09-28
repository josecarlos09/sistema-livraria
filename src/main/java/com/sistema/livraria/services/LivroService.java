package com.sistema.livraria.services;

import com.sistema.livraria.dtos.LivroIsbnRecordDto;
import com.sistema.livraria.dtos.LivroRecordDto;
import com.sistema.livraria.models.LivroModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface que define os serviços relacionados aos livros na livraria.
 * Contém métodos para salvar, atualizar, excluir, consultar e buscar livros,
 * incluindo suporte para ISBN e editoras.
 */
public interface LivroService {

    /**
     * Busca todos os livros com base em uma especificação e paginação.
     *
     * @param spec Especificação para filtrar os livros.
     * @param pageable Informações de paginação.
     * @return Página de livros encontrados conforme os critérios fornecidos.
     */
    Page<LivroModel> findAll(Specification<LivroModel> spec, Pageable pageable);

    /**
     * Busca um livro pelo seu ID.
     *
     * @param livroId ID do livro.
     * @return Um Optional com o livro encontrado ou vazio se não encontrado.
     */
    Optional<LivroModel> findById(UUID livroId);

    /**
     * Salva um novo livro no sistema com base nas informações fornecidas.
     *
     * @param livroRecordDto DTO com os dados do livro.
     * @return O livro salvo no banco de dados.
     */
    LivroModel save(LivroRecordDto livroRecordDto);

    /**
     * Atualiza as informações de um livro existente.
     *
     * @param livroModel O livro a ser atualizado.
     * @param livroRecordDto DTO com os novos dados do livro.
     * @return O livro atualizado.
     */
    LivroModel update(LivroModel livroModel, LivroRecordDto livroRecordDto);

    /**
     * Verifica se já existe um livro com o título informado.
     *
     * @param titulo Título do livro.
     * @return True se o livro existir, caso contrário, False.
     */
    boolean existsByTitulo(String titulo);

    /**
     * Exclui um livro do sistema.
     *
     * @param livroModel O livro a ser excluído.
     */
    void delete(LivroModel livroModel);

    /**
     * Busca um livro pelo seu ISBN.
     *
     * @param isbn Código ISBN do livro (10 ou 13 dígitos).
     * @return Um Optional com o livro encontrado ou vazio se não encontrado.
     */
    Optional<LivroModel> buscarPorIsbn(String isbn);

    /**
     * Salva um livro utilizando dados fornecidos via ISBN.
     *
     * @param livroIsbnRecordDto DTO contendo dados para registrar o livro com ISBN.
     * @return O livro salvo no banco de dados.
     */
    LivroModel saveLivroIsbn(LivroIsbnRecordDto livroIsbnRecordDto);

    /**
     * Verifica se já existe um livro com o ISBN informado.
     *
     * @param isbn Código ISBN do livro.
     * @return True se o livro existir, caso contrário, False.
     */
    boolean existsByIsbn(String isbn);

    LivroModel patchStatus(LivroModel livroModel, LivroRecordDto livroRecordDto);
}