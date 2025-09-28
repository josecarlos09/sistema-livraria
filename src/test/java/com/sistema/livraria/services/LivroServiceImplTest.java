package com.sistema.livraria.services;

import com.sistema.livraria.dtos.LivroRecordDto;
import com.sistema.livraria.enums.*;
import com.sistema.livraria.exceptios.NotFoundException;
import com.sistema.livraria.models.LivroModel;
import com.sistema.livraria.repositorys.LivroRepository;
import com.sistema.livraria.services.impl.LivroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivroServiceImplTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroServiceImpl livroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<LivroModel> livros = new ArrayList<>();
        livros.add(new LivroModel());
        Page<LivroModel> page = new PageImpl<>(livros);

        when(livroRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<LivroModel> result = livroService.findAll(null, Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        verify(livroRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testFindById_Success() {
        UUID id = UUID.randomUUID();
        LivroModel livro = new LivroModel();
        when(livroRepository.findById(id)).thenReturn(Optional.of(livro));

        Optional<LivroModel> result = livroService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(livro, result.get());
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(livroRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> livroService.findById(id));
    }

    @Test
    void testSave() {
        LivroRecordDto dto = new LivroRecordDto(
                "1234567890",
                "Título",
                "Subtítulo",
                new BigDecimal("50.0"),
                10,
                Categoria.CIENCIAS,
                TipoCapa.COMUM,
                "Autor",
                "Editora",
                StatusLivro.DISPONIVEL
        );

        LivroModel savedLivro = new LivroModel();
        when(livroRepository.save(any(LivroModel.class))).thenReturn(savedLivro);

        LivroModel result = livroService.save(dto);

        assertNotNull(result);
        verify(livroRepository, times(1)).save(any(LivroModel.class));
    }

    @Test
    void testUpdate() {
        LivroRecordDto dto = new LivroRecordDto(
                "9876543210",
                "Novo Título",
                "Novo Subtítulo",
                new BigDecimal("100.0"),
                5,
                Categoria.CIENCIAS,
                TipoCapa.DURA,
                "Novo Autor",
                "Nova Editora",
                StatusLivro.INDISPONIVEL
        );

        LivroModel livro = new LivroModel();
        when(livroRepository.save(livro)).thenReturn(livro);

        LivroModel result = livroService.update(livro, dto);

        assertEquals("Novo Título", result.getTitulo());
        assertEquals(StatusLivro.INDISPONIVEL, result.getStatusLivro());
        verify(livroRepository, times(1)).save(livro);
    }

    @Test
    void testDelete() {
        LivroModel livro = new LivroModel();
        doNothing().when(livroRepository).delete(livro);

        livroService.delete(livro);

        verify(livroRepository, times(1)).delete(livro);
    }

    @Test
    void testExistsByTitulo() {
        when(livroRepository.existsByTitulo("Título")).thenReturn(true);
        assertTrue(livroService.existsByTitulo("Título"));
    }

    @Test
    void testBuscarPorIsbn() {
        LivroModel livro = new LivroModel();
        when(livroRepository.findByIsbn("12345")).thenReturn(Optional.of(livro));

        Optional<LivroModel> result = livroService.buscarPorIsbn("12345");

        assertTrue(result.isPresent());
    }

    @Test
    void testExistsByIsbn() {
        when(livroRepository.existsByIsbn("12345")).thenReturn(true);
        assertTrue(livroService.existsByIsbn("12345"));
    }

    @Test
    void testPatchStatus() {
        LivroRecordDto dto = new LivroRecordDto(
                "1234567890",
                "Título",
                "Sub",
                new BigDecimal("50.0"),
                10,
                Categoria.CIENCIAS,
                TipoCapa.DURA,
                "Autor",
                "Editora",
                StatusLivro.INDISPONIVEL
        );

        LivroModel livro = new LivroModel();
        when(livroRepository.save(livro)).thenReturn(livro);

        LivroModel result = livroService.patchStatus(livro, dto);

        assertEquals(StatusLivro.INDISPONIVEL, result.getStatusLivro());
        verify(livroRepository, times(1)).save(livro);
    }
}