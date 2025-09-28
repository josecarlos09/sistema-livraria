package com.sistema.livraria.services.impl;

import com.sistema.livraria.dtos.LivroIsbnRecordDto;
import com.sistema.livraria.dtos.LivroRecordDto;
import com.sistema.livraria.enums.Formato;
import com.sistema.livraria.enums.StatusLivro;
import com.sistema.livraria.exceptios.NotFoundException;
import com.sistema.livraria.models.LivroModel;
import com.sistema.livraria.repositorys.LivroRepository;
import com.sistema.livraria.services.LivroService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Implementação dos serviços relacionados a Livro.
 */
@Service
public class LivroServiceImpl implements LivroService {

    Logger logger = LogManager.getLogger(LivroServiceImpl.class);

    final LivroRepository livroRepository;

    public LivroServiceImpl(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    /**
     * Busca todos os livros com filtros dinâmicos e paginação.
     */
    @Override
    public Page<LivroModel> findAll(Specification<LivroModel> spec, Pageable pageable) {
        return livroRepository.findAll(spec, pageable);
    }

    /**
     * Busca um livro por ID.
     * @param livroId ID do livro
     * @return Optional com o livro encontrado
     * @throws NotFoundException se o livro não for encontrado
     */
    @Override
    public Optional<LivroModel> findById(UUID livroId) {
        Optional<LivroModel> livroOptional = livroRepository.findById(livroId);

        if (livroOptional.isEmpty()) {
            logger.error("ERRO: LIVRO NÃO ENCONTRADO!");
            throw new NotFoundException("Livro não encontrado!");
        }

        return livroOptional;
    }

    /**
     * Cadastra um novo livro com validações de autor e editora.
     */
    @Transactional
    @Override
    public LivroModel save(LivroRecordDto livroRecordDto) {
        var livroModel = new LivroModel();
        BeanUtils.copyProperties(livroRecordDto, livroModel);


        // Define status e formato padrão
        livroModel.setStatusLivro(StatusLivro.DISPONIVEL);
        livroModel.setFormato(Formato.FISICO);

        // Define data de cadastro e atualização
        livroModel.setDataCadastroLivro(LocalDateTime.now(ZoneId.of("America/Recife")));
        livroModel.setDataAtualizacaoLivro(LocalDateTime.now(ZoneId.of("America/Recife")));

        return livroRepository.save(livroModel);
    }

    /**
     * Atualiza os dados de um livro.
     * @param livroModel entidade do livro a ser atualizada
     * @param livroRecordDto dados a serem atualizados
     * @return livro atualizado
     */
    @Transactional
    @Override
    public LivroModel update(LivroModel livroModel, LivroRecordDto livroRecordDto) {
        livroModel.setTitulo(livroRecordDto.titulo());
        livroModel.setSubtitulo(livroRecordDto.subtitulo());
        livroModel.setAutor(livroRecordDto.autor());
        livroModel.setEditora(livroRecordDto.editora());
        livroModel.setCategoria(livroRecordDto.categoria());
        livroModel.setTipoCapa(livroRecordDto.tipoCapa());
        livroModel.setIsbn(livroRecordDto.isbn());
        livroModel.setValor(livroRecordDto.valor());
        livroModel.setQuantidade(livroRecordDto.quantidade());
        livroModel.setStatusLivro(livroRecordDto.status());
        livroModel.setDataAtualizacaoLivro(LocalDateTime.now(ZoneId.of("America/Recife")));

        return livroRepository.save(livroModel);
    }

    /**
     * Deleta um livro. (Ajustar lógica se necessário: atualmente está deletando autor e editora novos)
     */
    @Transactional
    @Override
    public void delete(LivroModel livro){
        livroRepository.delete(livro);
    }

    /**
     * Verifica se um livro já existe pelo título.
     */
    @Override
    public boolean existsByTitulo(String titulo) {
        return livroRepository.existsByTitulo(titulo);
    }

    /**
     * Busca um livro pelo ISBN.
     */
    public Optional<LivroModel> buscarPorIsbn(String isbn) {
        return livroRepository.findByIsbn(isbn);
    }

    /**
     * Salva um livro cadastrado via ISBN (ex: via API externa).
     */
    @Override
    public LivroModel saveLivroIsbn(LivroIsbnRecordDto livroIsbnRecordDto) {
        var livroModel = new LivroModel();
        BeanUtils.copyProperties(livroIsbnRecordDto, livroModel);

        livroModel.setDataCadastroLivro(LocalDateTime.now(ZoneId.of("America/Recife")));
        livroModel.setDataAtualizacaoLivro(LocalDateTime.now(ZoneId.of("America/Recife")));
        livroModel.setFormato(Formato.FISICO);

        return livroRepository.save(livroModel);
    }

    /**
     * Verifica se já existe um livro com o ISBN informado.
     */
    @Override
    public boolean existsByIsbn(String isbn) {
        return livroRepository.existsByIsbn(isbn);
    }

    @Override
    public LivroModel patchStatus(LivroModel livroModel, LivroRecordDto livroRecordDto) {
        livroModel.setStatusLivro(livroRecordDto.status());
        return livroRepository.save(livroModel);
    }
}