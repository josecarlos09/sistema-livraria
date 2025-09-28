package com.sistema.livraria.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import com.sistema.livraria.enums.Categoria;
import com.sistema.livraria.enums.StatusLivro;
import com.sistema.livraria.enums.TipoCapa;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LivroRecordDto(
        @NotBlank(groups = LivroView.Cadastro.class, message = "O campo ISBN é obrigatório.")
        @Size(groups = LivroView.Cadastro.class, min = 2, max = 100, message = "O ISBN deve conter de 10 a 13 caracteres.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        String isbn,

        @NotBlank(groups = LivroView.Cadastro.class, message = "O campo título é obrigatório.")
        @Size(groups = LivroView.Cadastro.class, min = 2, max = 100, message = "O título deve conter entre 2 e 100 caracteres.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        String titulo,

        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        String subtitulo,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo valor é obrigatório.")
        @DecimalMin(value = "0.0", inclusive = false, groups = LivroView.Cadastro.class, message = "O valor deve ser maior que zero.")
        @Digits(integer = 6, fraction = 2, groups = LivroView.Cadastro.class, message = "Valor inválido.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        BigDecimal valor,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo quantidade é obrigatório.")
        @Min(value = 0, groups = LivroView.Cadastro.class, message = "A quantidade não pode ser negativa.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        Integer quantidade,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo categoria é obrigatório.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        Categoria categoria,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo tipo de capa é obrigatório.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        TipoCapa tipoCapa,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo tipo de autor é obrigatório.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        String autor,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo tipo de editora é obrigatório.")
        @JsonView({LivroView.Cadastro.class, LivroView.PutLivro.class})
        String editora,

        @NotNull(groups = LivroView.PathStatus.class, message = "O campo status é obrigatório.")
        @JsonView(LivroView.PathStatus.class)
        StatusLivro status){
    public interface LivroView {
        interface Cadastro {}
        interface PutLivro {}
        interface PathStatus{}
    }
}
