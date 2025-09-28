package com.sistema.livraria.specifications;

import com.sistema.livraria.models.LivroModel;
import com.sistema.livraria.models.UsuarioModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

/**
 * Classe responsável por definir templates de especificações de filtros dinâmicos
 * utilizados nas consultas aos dados das entidades do sistema de livraria.
 *
 * Utiliza a biblioteca Spring Data JPA Specification com o suporte da biblioteca de terceiros
 * Net Kaczmarzyk, que facilita a criação de filtros declarativos com anotações.
 */
public class SpecificationsTemplate {

    /**
     * Interface para definir filtros de busca na entidade UsuarioModel.
     *
     * Os filtros suportados são:
     * - Igualdade para o campo "usuarioId"
     * - Igualdade para o campo "senha"
     * - Busca parcial (LIKE) para o campo "nome"
     *
     * Esta interface é utilizada junto aos endpoints para permitir buscas dinâmicas.
     */
    @And({
            @Spec(path = "usuarioId", spec = Equal.class), // Filtra por igualdade no campo usuarioId
            @Spec(path = "senha", spec = Equal.class),     // Filtra por igualdade no campo senha
            @Spec(path = "nome", spec = Like.class),       // Filtra por similaridade no campo nome (LIKE)
    })
    public interface UsuarioSpec extends Specification<UsuarioModel> {}

    /**
     * Interface para definir filtros de busca na entidade LivroModel.
     *
     * Os filtros suportados são:
     * - Busca parcial (LIKE) para o campo "titulo"
     * - Busca parcial (LIKE) para o campo "isbn"
     * - Igualdade para o campo "valor"
     */
    @And({
            @Spec(path = "titulo", spec = Like.class),     // Filtra por similaridade no campo titulo (LIKE)
            @Spec(path = "isbn", spec = Like.class),       // Filtra por similaridade no campo isbn (LIKE)
            @Spec(path = "autores", spec = Like.class),       // Filtra por similaridade no campo isbn (LIKE)
            @Spec(path = "valor", spec = Equal.class),    // Filtra por igualdade no campo valor

    })
    public interface LivroSpec extends Specification<LivroModel> {}
}