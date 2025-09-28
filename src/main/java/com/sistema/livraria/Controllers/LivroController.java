package com.sistema.livraria.Controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.sistema.livraria.dtos.LivroIsbnRecordDto;
import com.sistema.livraria.dtos.LivroRecordDto;
import com.sistema.livraria.models.LivroModel;
import com.sistema.livraria.services.*;
import com.sistema.livraria.specifications.SpecificationsTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Controlador REST responsável por gerenciar os endpoints relacionados à entidade Livro.
 */
@RestController
@RequestMapping("/livros")
public class LivroController {
    // Logger para registrar informações, erros e fluxos de execução no controller.
    Logger logger = LogManager.getLogger(LivroController.class);

    // Injeção dos serviços que contêm a lógica de negócios dos livros, livrarias, autores e editoras.
    final LivroService livroService;
    final LivrariaService livrariaService;
    final RelatorioService relatorioService;

    /**
     * Construtor do controlador, com injeção dos serviços necessários para o gerenciamento de livros.
     *
     * @param livroService    Serviço responsável pelas operações de livro.
     * @param livrariaService Serviço responsável pelas operações de livraria.
     */
    public LivroController(LivroService livroService, LivrariaService livrariaService, RelatorioService relatorioService) {
        this.livroService = livroService;
        this.livrariaService = livrariaService;
        this.relatorioService = relatorioService;
    }

    /**
     * Endpoint para cadastrar um novo livro, associando-o a um autor e uma editora.
     *
     * @param livroRecordDto DTO com os dados do livro a ser cadastrado.
     * @return O livro cadastrado.
     */
    @PostMapping
    public ResponseEntity<Object> saveLivro(@RequestBody
                                            @Validated(LivroRecordDto.LivroView.Cadastro.class)
                                            @JsonView(LivroRecordDto.LivroView.Cadastro.class)
                                            LivroRecordDto livroRecordDto){
        // Verifica se o título do livro já está em uso.
        if (livroService.existsByTitulo(livroRecordDto.titulo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Esse título já está em uso!");
        }

        // Verifica se o ISBN do livro já está em uso.
        if (livroService.existsByIsbn(livroRecordDto.isbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Esse ISBN já está em uso!");
        }

        logger.debug("POST: saveLivro, dados recebidos: {}", livroRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.save(livroRecordDto));
    }

    /**
     * Endpoint para buscar todos os livros com paginação e possibilidade de filtros dinâmicos.
     *
     * @param spec     Especificação com filtros (título, valor, editora, autor).
     * @param pageable Objeto com as configurações de paginação.
     * @return Página de livros conforme os filtros aplicados.
     */
    @GetMapping
    public ResponseEntity<Page<LivroModel>> getAllLivros(@PageableDefault(page = 0, size = 10, sort = "dataCadastroLivro", direction = Sort.Direction.DESC) // coloque essa conf. abaixo do spec (se estiver acima não funciona
                                                             SpecificationsTemplate.LivroSpec spec,
                                                         Pageable pageable) {
        Page<LivroModel> livroPage = livroService.findAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(livroPage);
    }

    /**
     * Endpoint para buscar um único livro pelo seu ID.
     *
     * @param livroId UUID do livro.
     * @return Objeto do livro encontrado.
     */
    @GetMapping("/livroId/{livroId}")
    public ResponseEntity<Object> getOneLivro(@PathVariable(value = "livroId") UUID livroId) {
        logger.debug("GET: getOneLivro, consulta: {}", livroId);
        return ResponseEntity.status(HttpStatus.OK).body(livroService.findById(livroId));
    }


    /**
     * Endpoint para atualizar os dados de um livro existente.
     *
     * @param livroId        UUID do livro a ser atualizado.
     * @param livroRecordDto DTO com os novos dados do livro.
     * @return Livro atualizado.
     */
    @PutMapping("/{livroId}")
    public ResponseEntity<Object> updateLivro(@PathVariable(value = "livroId") UUID livroId,
                                              @RequestBody
                                              @Validated(LivroRecordDto.LivroView.PutLivro.class)
                                              @JsonView(LivroRecordDto.LivroView.PutLivro.class)
                                              LivroRecordDto livroRecordDto) {
        logger.debug("PUT: updateLivro, livroId recebido: {}", livroId);
        return ResponseEntity.status(HttpStatus.OK).body(livroService.update(livroService.findById(livroId).get(), livroRecordDto));
    }

    @PatchMapping("/status/{livroId}")
    public ResponseEntity<Object> patchStatusLivro(@PathVariable(value = "livroId") UUID livroId,
                                                   @RequestBody
                                                   @Validated(LivroRecordDto.LivroView.PathStatus.class)
                                                   @JsonView(LivroRecordDto.LivroView.PathStatus.class)
                                                   LivroRecordDto livroRecordDto){
        logger.debug("PATCH: patchStatusLivro, livroId recebido: {}", livroId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(livroService.patchStatus(livroService.findById(livroId).get(), livroRecordDto));
    }

    /**
     * Endpoint para deletar um livro pelo seu ID.
     * Verifica se o livro possui autores ou editora vinculados antes de permitir a exclusão.
     *
     * @param livroId UUID do livro a ser deletado.
     * @return Mensagem de sucesso ou falha da exclusão.
     */
    @DeleteMapping("/{livroId}")
    public ResponseEntity<Object> deleteLivro(@PathVariable(value = "livroId") UUID livroId) {
        Optional<LivroModel> livroOptional = livroService.findById(livroId);
        LivroModel livro = livroOptional.get();

        // Chama o serviço para deletar o livro.
        livroService.delete(livroService.findById(livroId).get());
        logger.debug("DELETE: deleteLivro, livroId recebido: {}", livroId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * Endpoint para cadastrar um livro a partir de seu ISBN. O livro é registrado utilizando as informações fornecidas.
     *
     * @param isbn                ISBN do livro a ser registrado.
     * @param livroIsbnRecordDto  DTO com os dados do livro a ser registrado.
     * @return O livro registrado.
     */
    @PostMapping("/isbn/{isbn}")
    public ResponseEntity<Object> buscarLivroPorIsbn(@PathVariable String isbn,
                                                     @RequestBody
                                                     @Validated(LivroIsbnRecordDto.LivroView.Cadastro.class)
                                                     @JsonView(LivroIsbnRecordDto.LivroView.Cadastro.class)
                                                     LivroIsbnRecordDto livroIsbnRecordDto) {
        // Verifica se o ISBN já está cadastrado.
        if (livroService.existsByIsbn(livroIsbnRecordDto.isbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Livro já cadastrado!");
        }

        logger.debug("POST: saveLivro, dados recebidos: {}");
        LivroModel livro = livrariaService.registrarLivroPorIsbn(isbn, livroIsbnRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(livro);
    }

    /**
     * Endpoint para consultar um livro a partir de seu ISBN.
     *
     * @param isbn ISBN do livro a ser consultado.
     * @return O livro consultado.
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Object> consultarLivroPorIsbn(@PathVariable String isbn) {
        logger.debug("POST: saveLivro, dados recebidos: {}");
        LivroModel livro = livrariaService.consultarLivroPorIsbn(isbn);
        return ResponseEntity.status(HttpStatus.CREATED).body(livro);
    }
}