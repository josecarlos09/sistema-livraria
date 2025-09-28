package com.sistema.livraria.dtos;

import com.fasterxml.jackson.annotation.JsonView;
import com.sistema.livraria.enums.Categoria;
import com.sistema.livraria.enums.TipoCapa;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record LivroIsbnRecordDto(
        @NotBlank(groups = LivroRecordDto.LivroView.Cadastro.class, message = "O campo ISBN é obrigatório.")
        @Size(groups = LivroRecordDto.LivroView.Cadastro.class, min = 2, max = 100, message = "O ISBN deve conter de 10 a 13 caracteres.")
        @JsonView({LivroRecordDto.LivroView.Cadastro.class, LivroRecordDto.LivroView.PutLivro.class})
        String isbn,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo valor é obrigatório.")
        @DecimalMin(value = "0.0", inclusive = false, groups = LivroView.Cadastro.class, message = "O valor deve ser maior que zero.")
        @Digits(integer = 6, fraction = 2, groups = LivroView.Cadastro.class, message = "Valor inválido.")
        @JsonView({LivroView.Cadastro.class, LivroView.Atualizacao.class})
        BigDecimal valor,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo quantidade é obrigatório.")
        @Min(value = 0, groups = LivroView.Cadastro.class, message = "A quantidade não pode ser negativa.")
        @JsonView({LivroView.Cadastro.class, LivroView.Atualizacao.class})
        Integer quantidade,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo categoria é obrigatório.")
        @JsonView({LivroView.Cadastro.class, LivroView.Atualizacao.class})
        Categoria categoria,

        @NotNull(groups = LivroView.Cadastro.class, message = "O campo tipo de capa é obrigatório.")
        @JsonView({LivroView.Cadastro.class, LivroView.Atualizacao.class})
        TipoCapa tipoCapa){

    public interface LivroView {
        interface Cadastro {}
        interface Atualizacao {}
    }
}


