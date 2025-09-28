package com.sistema.livraria.services;

import com.sistema.livraria.enums.RoleType;
import com.sistema.livraria.models.RoleModel;

/**
 * Interface que define os serviços relacionados aos papéis de usuários (roles) na aplicação.
 * Contém métodos para buscar um papel específico de usuário com base no tipo de papel (RoleType).
 */
public interface RoleService {

    /**
     * Busca um modelo de papel (role) com base no nome do papel (RoleType).
     *
     * @param roleType Tipo do papel do usuário (ex: ADMIN, USER, etc.), fornecido através do enum RoleType.
     * @return O modelo de papel (RoleModel) correspondente ao tipo fornecido.
     */
    RoleModel findByRoleNome(RoleType roleType);
}
