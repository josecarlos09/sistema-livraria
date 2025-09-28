package com.sistema.livraria.services;

import com.sistema.livraria.dtos.UsuarioRecordDto;
import com.sistema.livraria.models.UsuarioModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface que define os serviços relacionados aos usuários da aplicação.
 * Contém métodos para buscar, salvar, atualizar e excluir usuários, com suporte a filtros dinâmicos e paginação.
 */
public interface UsuarioService {

    /**
     * Busca todos os usuários, com suporte a filtros dinâmicos e paginação.
     *
     * @param spec Filtros dinâmicos para construção da consulta.
     * @param pageable Paginação dos resultados.
     * @return Uma página de usuários que atendem aos critérios especificados.
     */
    Page<UsuarioModel> findAll(Specification<UsuarioModel> spec, Pageable pageable);

    /**
     * Busca um usuário pelo seu ID único (UUID).
     *
     * @param usuarioId O ID do usuário a ser buscado.
     * @return Um Optional contendo o usuário se encontrado, ou vazio caso contrário.
     */
    Optional<UsuarioModel> findById(UUID usuarioId);

    /**
     * Deleta um usuário específico.
     *
     * @param usuarioModel O modelo do usuário a ser excluído.
     * @return O modelo do usuário deletado.
     */
    UsuarioModel deleteUsuarioId(UsuarioModel usuarioModel);

    /**
     * Atualiza as informações de um usuário.
     *
     * @param usuarioModel O modelo do usuário a ser atualizado.
     * @param usuarioRecordDto O DTO contendo os novos dados do usuário.
     * @return O modelo de usuário atualizado.
     */
    UsuarioModel updateUsuario(UsuarioModel usuarioModel, UsuarioRecordDto usuarioRecordDto);

    /**
     * Atualiza a senha de um usuário.
     *
     * @param usuarioModel O modelo do usuário.
     * @param dadosUsuarioRecordDto O DTO contendo os novos dados da senha.
     * @return O modelo de usuário com a senha atualizada.
     */
    UsuarioModel updateSenha(UsuarioModel usuarioModel, UsuarioRecordDto dadosUsuarioRecordDto);

    /**
     * Atualiza o status de um usuário (ativo, inativo, etc.).
     *
     * @param usuarioModel O modelo do usuário.
     * @param usuarioRecordDto O DTO contendo os novos dados de status.
     * @return O modelo de usuário com o status atualizado.
     */
    UsuarioModel updateStatusUsuario(UsuarioModel usuarioModel, UsuarioRecordDto usuarioRecordDto);

    /**
     * Verifica se já existe um usuário com o mesmo nome.
     *
     * @param nome O nome a ser verificado.
     * @return Um booleano indicando se o nome já está cadastrado.
     */
    boolean existByNome(String nome);

    /**
     * Salva um novo usuário.
     *
     * @param usuarioRecordDto O DTO com os dados do usuário a ser salvo.
     * @return O modelo do usuário salvo.
     */
    UsuarioModel saveUsuario(UsuarioRecordDto usuarioRecordDto);

    /**
     * Verifica se já existe um usuário com a mesma senha.
     *
     * @param senha A senha a ser verificada.
     * @return Um booleano indicando se a senha já está cadastrada.
     */
    boolean existBySenha(String senha);
}