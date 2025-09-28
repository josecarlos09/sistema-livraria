package com.sistema.livraria.services.impl;

import com.sistema.livraria.dtos.UsuarioRecordDto;
import com.sistema.livraria.enums.RoleType;
import com.sistema.livraria.enums.StatusUsuario;
import com.sistema.livraria.enums.TipoPerfio;
import com.sistema.livraria.exceptios.NotFoundException;
import com.sistema.livraria.models.UsuarioModel;
import com.sistema.livraria.repositorys.UsuarioRepository;
import com.sistema.livraria.services.RoleService;
import com.sistema.livraria.services.UsuarioService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação da interface {@link UsuarioService}.
 * Responsável por toda a lógica de negócio relacionada à entidade {@link UsuarioModel}.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    // Logger para registrar mensagens de log do sistema
    Logger logger = LogManager.getLogger(UsuarioServiceImpl.class);

    // Repositório de usuários
    final UsuarioRepository usuarioRepository;

    // Serviço de roles (perfis de acesso)
    final RoleService roleService;

    // Codificador de senhas (BCrypt, por exemplo)
    final PasswordEncoder passwordEncoder;

    /**
     * Construtor para injeção de dependência dos componentes necessários.
     *
     * @param usuarioRepository Repositório de usuários
     * @param roleService Serviço de roles
     * @param passwordEncoder Codificador de senhas
     */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Busca todos os usuários com paginação e especificação (filtros dinâmicos).
     *
     * @param spec Filtros para a busca.
     * @param pageable Objeto de paginação.
     * @return Página contendo os usuários encontrados.
     */
    @Override
    public Page<UsuarioModel> findAll(Specification<UsuarioModel> spec, Pageable pageable) {
        return usuarioRepository.findAll(spec, pageable);
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param usuarioId Identificador único do usuário.
     * @return Optional contendo o usuário se encontrado.
     * @throws NotFoundException Caso o usuário não exista.
     */
    @Override
    public Optional<UsuarioModel> findById(UUID usuarioId) {
        Optional<UsuarioModel> usuarioModelOptional = usuarioRepository.findById(usuarioId);

        if (usuarioModelOptional.isEmpty()) {
            logger.error("ERRO, USUÁRIO NÃO ENCONTRADO!");
            throw new NotFoundException("ERRO, USUÁRIO NÃO ENCONTRADO!");
        }

        return usuarioModelOptional;
    }

    /**
     * Exclui um usuário do sistema.
     *
     * @param usuarioModel Modelo do usuário a ser excluído.
     * @return Usuário removido.
     */
    @Override
    public UsuarioModel deleteUsuarioId(UsuarioModel usuarioModel) {
        usuarioRepository.delete(usuarioModel);
        return usuarioModel;
    }

    /**
     * Atualiza os dados do usuário (exceto senha).
     *
     * @param usuarioModel Modelo do usuário atual.
     * @param usuarioRecordDto Dados novos do usuário.
     * @return Usuário atualizado.
     */
    @Override
    public UsuarioModel updateUsuario(UsuarioModel usuarioModel, UsuarioRecordDto usuarioRecordDto) {
        usuarioModel.setNome(usuarioRecordDto.nome());
        usuarioModel.setStatusUsuario(StatusUsuario.ATIVO);
        usuarioModel.setDataAtualizacao(LocalDateTime.now(ZoneId.of("America/Recife")));

        return usuarioRepository.save(usuarioModel);
    }

    /**
     * Atualiza a senha do usuário.
     *
     * @param usuarioModel Usuário a ser atualizado.
     * @param usuarioRecordDto Dados contendo a nova senha.
     * @return Usuário com senha atualizada.
     */
    @Override
    public UsuarioModel updateSenha(UsuarioModel usuarioModel, UsuarioRecordDto usuarioRecordDto) {
        usuarioModel.setSenha(passwordEncoder.encode(usuarioRecordDto.senha()));
        usuarioModel.setDataAtualizacao(LocalDateTime.now(ZoneId.of("America/Recife")));

        return usuarioRepository.save(usuarioModel);
    }

    /**
     * Atualiza o status de um usuário (ATIVO, INATIVO, BLOQUEADO, etc).
     *
     * @param usuarioModel Usuário a ser atualizado.
     * @param usuarioRecordDto DTO contendo o novo status.
     * @return Usuário atualizado.
     */
    @Override
    public UsuarioModel updateStatusUsuario(UsuarioModel usuarioModel, UsuarioRecordDto usuarioRecordDto) {
        usuarioModel.setStatusUsuario(usuarioRecordDto.statusUsuario());
        return usuarioRepository.save(usuarioModel);
    }

    /**
     * Verifica se existe um usuário com o nome informado.
     *
     * @param nome Nome a ser verificado.
     * @return true se existir, false caso contrário.
     */
    @Override
    public boolean existByNome(String nome) {
        return usuarioRepository.existsByNome(nome);
    }

    /**
     * Verifica se existe um usuário com a senha informada (não recomendado por segurança).
     *
     * @param senha Senha a ser verificada.
     * @return true se existir, false caso contrário.
     */
    @Override
    public boolean existBySenha(String senha) {
        return usuarioRepository.existsBySenha(senha);
    }

    /**
     * Cria e salva um novo usuário no banco de dados.
     *
     * @param usuarioRecordDto DTO com os dados do novo usuário.
     * @return Usuário criado.
     */
    @Transactional
    @Override
    public UsuarioModel saveUsuario(UsuarioRecordDto usuarioRecordDto) {
        var usuarioModel = new UsuarioModel();

        // Copia os dados do DTO para o modelo
        BeanUtils.copyProperties(usuarioRecordDto, usuarioModel);

        // Configurações padrão para novo usuário
        usuarioModel.setPerfilUsuario(TipoPerfio.USUARIO);
        usuarioModel.setStatusUsuario(StatusUsuario.ATIVO);
        usuarioModel.setDataCriacao(LocalDateTime.now(ZoneId.of("America/Recife")));
        usuarioModel.setDataAtualizacao(LocalDateTime.now(ZoneId.of("America/Recife")));

        // Codifica a senha antes de salvar
        usuarioModel.setSenha(passwordEncoder.encode(usuarioModel.getSenha()));

        // Define a role padrão (ROLE_USUARIO)
        usuarioModel.getRoles().add(roleService.findByRoleNome(RoleType.ROLE_USUARIO));

        // Salva o novo usuário no banco
        return usuarioRepository.save(usuarioModel);
    }
}