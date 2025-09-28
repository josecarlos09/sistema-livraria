package com.sistema.livraria.repositorys;

import com.sistema.livraria.enums.StatusLivro;
import com.sistema.livraria.models.LivroModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface responsável pelo acesso aos dados da entidade LivroModel.
 *
 * Estende JpaRepository para fornecer operações CRUD básicas e
 * JpaSpecificationExecutor para permitir o uso de filtros dinâmicos com Specifications.
 */
public interface LivroRepository extends JpaRepository<LivroModel, UUID>, JpaSpecificationExecutor<LivroModel> {

    boolean existsByTitulo(String titulo);

    Optional<LivroModel> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    // O nome do método deve corresponder ao campo String 'editora' no LivroModel
    boolean existsByEditora(String editora);

    // O nome do método deve corresponder ao campo String 'autor' no LivroModel
    boolean existsByAutor(String autor);

    List<LivroModel> findByStatusLivro(StatusLivro statusLivro);

    List<LivroModel> findByQuantidade(int quantidade);
}
