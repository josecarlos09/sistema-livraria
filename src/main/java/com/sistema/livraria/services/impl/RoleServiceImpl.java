package com.sistema.livraria.services.impl;

import com.sistema.livraria.enums.RoleType;
import com.sistema.livraria.models.RoleModel;
import com.sistema.livraria.repositorys.RoleRepository;
import com.sistema.livraria.services.RoleService;
import org.springframework.stereotype.Service;

/**
 * Implementação da interface {@link RoleService}.
 * Esta classe é responsável por fornecer a lógica de negócio relacionada às funções (roles)
 * dos usuários no sistema, como ADMIN, USER, etc.
 */
@Service
public class RoleServiceImpl implements RoleService {

    // Repositório responsável por manipular os dados das roles (funções/permissões)
    final RoleRepository roleRepository;

    /**
     * Construtor para injeção de dependência do repositório de roles.
     *
     * @param roleRepository o repositório de roles injetado automaticamente pelo Spring.
     */
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Método para buscar uma role específica pelo seu tipo (enum {@link RoleType}).
     *
     * @param roleType O tipo de role que se deseja buscar (ex: ADMIN, USER).
     * @return {@link RoleModel} A entidade da role correspondente ao tipo informado.
     * @throws RuntimeException Se a role não for encontrada no banco de dados.
     */
    @Override
    public RoleModel findByRoleNome(RoleType roleType) {
        // Busca a role pelo nome (representado como enum) no banco de dados
        return roleRepository.findByRoleNome(roleType)
                // Lança uma exceção caso a role não exista
                .orElseThrow(() -> new RuntimeException("ERROR: ROLE NÃO EXISTENTE."));
    }
}