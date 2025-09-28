package com.sistema.livraria.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.livraria.dtos.LivroIsbnRecordDto;
import com.sistema.livraria.enums.Formato;
import com.sistema.livraria.enums.StatusLivro;
import com.sistema.livraria.exceptios.NotFoundException;
import com.sistema.livraria.models.LivroModel;
import com.sistema.livraria.repositorys.LivroRepository;
import com.sistema.livraria.services.LivrariaService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Implementação dos serviços relacionados à livraria,
 * com integração à API Open Library para busca de livros por ISBN.
 */
@Service
public class LivrariaServiceImpl implements LivrariaService {

    Logger logger = LogManager.getLogger(LivrariaServiceImpl.class);

    final LivroRepository livroRepository;
    final RestTemplate restTemplate;


    /**
     * Construtor que inicializa os repositórios e o RestTemplate.
     *
     * @param livroRepository Repositório de livros
     * @param restTemplate Template para chamadas HTTP externas
     */
    public LivrariaServiceImpl(LivroRepository livroRepository, RestTemplate restTemplate) {
        this.livroRepository = livroRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Consulta um livro existente no banco de dados a partir do ISBN informado.
     *
     * @param isbn Código ISBN do livro (10 ou 13 dígitos)
     * @return Livro encontrado
     * @throws NotFoundException se o ISBN for inválido ou o livro não existir
     */
    @Transactional
    @Override
    public LivroModel consultarLivroPorIsbn(String isbn) {
        String isbnLimpo = isbn.replaceAll("[\\s-]", ""); // remove espaços e hífens
        logger.info("ISBN: " + isbnLimpo);

        if (!isbnLimpo.matches("\\d{10}|\\d{13}")) {
            throw new NotFoundException("Formato de ISBN inválido. Use 10 ou 13 dígitos numéricos.");
        }

        Optional<LivroModel> livroOptional = livroRepository.findByIsbn(isbnLimpo);
        if (livroOptional.isEmpty()) {
            throw new NotFoundException("Livro com ISBN: " + isbnLimpo + " não encontrado.");
        }
        return livroOptional.get();
    }

    /**
     * Registra um livro no sistema a partir de seu ISBN, buscando dados via API externa.
     *
     * @param isbn Código ISBN do livro
     * @param livroIsbnRecordDto DTO contendo informações adicionais para o cadastro
     * @return Livro salvo com os dados vindos da API e os fornecidos no DTO
     */
    @Transactional
    @Override
    public LivroModel registrarLivroPorIsbn(String isbn, LivroIsbnRecordDto livroIsbnRecordDto) {
        LivroModel livro = livroRepository.findByIsbn(isbn)
                .orElseGet(() -> consultarERegistrarLivro(isbn));

        // Preenche dados adicionais fornecidos manualmente
        livro.setQuantidade(livroIsbnRecordDto.quantidade());
        livro.setValor(livroIsbnRecordDto.valor());
        livro.setCategoria(livroIsbnRecordDto.categoria());
        livro.setTipoCapa(livroIsbnRecordDto.tipoCapa());

        return livro;
    }

    /**
     * Consulta a API Open Library para buscar os dados de um livro por ISBN
     * e registra esse livro no banco de dados.
     *
     * @param isbn Código ISBN do livro
     * @return LivroModel com dados preenchidos e persistido no banco
     * @throws NotFoundException em caso de erro ou dados ausentes
     */
    private LivroModel consultarERegistrarLivro(String isbn) {
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
        logger.info("URL da requisição: {}", url); // Log da URL

        try {
            // Faz a requisição à API externa
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Log da resposta para verificar o que a API retornou
            logger.info("Resposta da API Open Library: {}", response.getBody());

            // Verifica se a resposta foi bem-sucedida
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode livroNode = root.path("ISBN:" + isbn);

                if (livroNode.isMissingNode()) {
                    logger.error("Livro não encontrado para o ISBN: {}", isbn);
                    throw new NotFoundException("Livro inexistente para o ISBN fornecido.");
                }

                // Criação do objeto LivroModel com dados da API
                var livro = new LivroModel();
                livro.setIsbn(isbn);
                livro.setTitulo(livroNode.path("title").asText("")); // Título do livro
                livro.setSubtitulo(livroNode.path("subtitle").asText("")); // Subtítulo do livro
                livro.setNumeroPaginas(livroNode.path("number_of_pages").asInt(0)); // Número de páginas
                livro.setDataPublicacao(livroNode.path("publish_date").asText("")); // Data de publicação
                livro.setFormato(Formato.FISICO); // Formato do livro (Físico)
                livro.setStatusLivro(StatusLivro.DISPONIVEL); // Status do livro (Disponível)
                livro.setDataCadastroLivro(LocalDateTime.now(ZoneId.of("America/Recife"))); // Data de cadastro
                livro.setDataAtualizacaoLivro(LocalDateTime.now(ZoneId.of("America/Recife"))); // Data de atualização

                // Define a URL da capa do livro, se existir
                if (livroNode.has("cover")) {
                    livro.setCapaUrl(livroNode.path("cover").path("medium").asText(""));
                }

                return livroRepository.save(livro); // Salva o livro no banco de dados
            } else {
                logger.error("Erro ao buscar livro na API externa. Status: {}", response.getStatusCode());
                throw new NotFoundException("Erro ao buscar livro na API externa");
            }
        } catch (NotFoundException e) {
            logger.error("Livro inexistente para o ISBN: {}. Detalhes: {}", isbn, e.getMessage());
            throw new NotFoundException("Livro inexistente para o ISBN fornecido.");
        } catch (Exception e) {
            logger.error("Erro ao processar resposta da API externa: {}", e.getMessage());
            throw new NotFoundException("Erro ao processar resposta da API externa");
        }
    }
}