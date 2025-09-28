package com.sistema.livraria.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sistema.livraria.enums.*;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_LIVRO")
public class LivroModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID livroId;

    @Column(unique = true)
    private String isbn;

    @Column
    private String titulo;

    @Column
    private String subtitulo;

    @Column
    private BigDecimal valor;

    @Column
    private Integer quantidade;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusLivro statusLivro;

    @Column
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column
    @Enumerated(EnumType.STRING)
    private TipoCapa tipoCapa;

    @Column
    @Enumerated(EnumType.STRING)
    private Formato formato;

    @Column
    private String dataPublicacao;

    @Column
    private Integer numeroPaginas;

    @Column
    private String capaUrl;

    @Column
    private String autor;

    @Column
    private String editora;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCadastroLivro;

    @Column
    private LocalDateTime dataAtualizacaoLivro;

    // Métodos acessores e modificadores (GETs e SETs)
    public UUID getLivroId() {
        return livroId;
    }

    public void setLivroId(UUID livroId) {
        this.livroId = livroId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusLivro getStatusLivro() {
        return statusLivro;
    }

    public void setStatusLivro(StatusLivro statusLivro) {
        this.statusLivro = statusLivro;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public TipoCapa getTipoCapa() {
        return tipoCapa;
    }

    public void setTipoCapa(TipoCapa tipoCapa) {
        this.tipoCapa = tipoCapa;
    }

    public Formato getFormato() {
        return formato;
    }

    public void setFormato(Formato formato) {
        this.formato = formato;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Integer getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(Integer numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public String getCapaUrl() {
        return capaUrl;
    }

    public void setCapaUrl(String capaUrl) {
        this.capaUrl = capaUrl;
    }

    public LocalDateTime getDataCadastroLivro() {
        return dataCadastroLivro;
    }

    public void setDataCadastroLivro(LocalDateTime dataCadastroLivro) {
        this.dataCadastroLivro = dataCadastroLivro;
    }

    public LocalDateTime getDataAtualizacaoLivro() {
        return dataAtualizacaoLivro;
    }

    public void setDataAtualizacaoLivro(LocalDateTime dataAtualizacaoLivro) {
        this.dataAtualizacaoLivro = dataAtualizacaoLivro;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }
}