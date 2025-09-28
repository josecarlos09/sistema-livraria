package com.sistema.livraria.repositorys;

import com.sistema.livraria.enums.RoleType;
import com.sistema.livraria.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleModel, UUID> {
    // Método para buscar uma RoleModel a partir do nome do tipo de role (RoleType)
    Optional<RoleModel> findByRoleNome(RoleType nome);
}