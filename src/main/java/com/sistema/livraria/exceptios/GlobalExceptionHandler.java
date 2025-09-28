package com.sistema.livraria.exceptios;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsável por centralizar o tratamento de exceções globais na aplicação.
 * Esta classe utiliza o `@ControllerAdvice` para capturar exceções lançadas durante a execução da aplicação
 * e retornar uma resposta adequada ao cliente com o código de erro e a mensagem de erro.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Logger para registrar mensagens de erro e advertência.
    Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata exceções do tipo NotFoundException.
     * Retorna uma resposta com o código de erro 404 (NOT_FOUND) e a mensagem da exceção.
     *
     * @param exception A exceção capturada.
     * @return ResponseEntity com o código de erro e a mensagem da exceção.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErroRecordResponse> handleNotFoundException(NotFoundException exception){
        var erroRecordResponse = new ErroRecordResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                null
        );
        logger.error("ERRO: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erroRecordResponse);
    }

    /**
     * Trata exceções de validação de dados nos DTOs.
     * Retorna uma resposta com o código de erro 400 (BAD_REQUEST), uma mensagem
     * genérica de erro de validação e os erros detalhados dos campos.
     *
     * @param ex A exceção de validação capturada.
     * @return ResponseEntity com o código de erro e os detalhes dos erros de validação.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRecordResponse> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nomeCampo = ((FieldError) error).getField();
            String mensagemErro = error.getDefaultMessage();
            erros.put(nomeCampo, mensagemErro);
        });

        var erroRecordResponse = new ErroRecordResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ERRO DE VALIDAÇÃO",
                erros
        );

        logger.error("ERRO DE VALIDAÇÃO: {}", erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroRecordResponse);
    }

    /**
     * Trata exceções quando o corpo da requisição está ausente ou mal formatado.
     * Retorna uma resposta com o código de erro 400 (BAD_REQUEST) e uma mensagem explicativa.
     *
     * @param ex A exceção de corpo mal formatado capturada.
     * @return ResponseEntity com o código de erro 400 e a mensagem explicativa.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRecordResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Erro: Corpo da requisição ausente ou mal formatado. Detalhes: {}", ex.getMessage());

        var erroRecordResponse = new ErroRecordResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro: O corpo da requisição está ausente ou mal formatado.",
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroRecordResponse);
    }

    /**
     * Trata exceções de violação de integridade de dados.
     * Retorna uma resposta com o código de erro 409 (CONFLICT) e uma mensagem indicando que a integridade de dados foi violada.
     *
     * @param ex A exceção de violação de integridade capturada.
     * @return ResponseEntity com o código de erro e a mensagem de violação de integridade.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroRecordResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Violação de integridade de dados: {}", ex.getMessage());

        var erroRecordResponse = new ErroRecordResponse(
                HttpStatus.CONFLICT.value(),
                "Erro: Violação de integridade de dados. Verifique se há duplicidade ou relacionamento inválido.",
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erroRecordResponse);
    }

    /**
     * Trata exceções de acesso negado.
     * Retorna uma resposta com o código de erro 403 (FORBIDDEN) e uma mensagem informando que o acesso ao recurso foi negado.
     *
     * @param ex A exceção de acesso negado capturada.
     * @return ResponseEntity com o código de erro e a mensagem de acesso negado.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroRecordResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Acesso negado: {}", ex.getMessage());

        var erroRecordResponse = new ErroRecordResponse(
                HttpStatus.FORBIDDEN.value(),
                "Erro: Você não tem permissão para acessar este recurso.",
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erroRecordResponse);
    }

    /**
     * Trata exceções quando o método HTTP não é suportado para um endpoint específico.
     * Retorna uma resposta com o código de erro 405 (METHOD_NOT_ALLOWED) e uma mensagem explicando o erro.
     *
     * @param ex A exceção de método HTTP não suportado capturada.
     * @return ResponseEntity com o código de erro e a mensagem explicativa.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroRecordResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.warn("Método HTTP não suportado: {}", ex.getMessage());

        var erroRecordResponse = new ErroRecordResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Erro: Método HTTP não permitido para este endpoint.",
                null
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(erroRecordResponse);
    }

//    /**
//     * Trata exceções inesperadas (erros internos do servidor).
//     * Retorna uma resposta com o código de erro 500 (Internal Server Error) e uma mensagem genérica.
//     *
//     * @param ex A exceção inesperada capturada.
//     * @return ResponseEntity com o código de erro 500 e uma mensagem genérica de erro interno.
//     */
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErroRecordResponse> handleInternalServerError(Exception ex) {
//        logger.error("Erro interno do servidor: {}", ex.getMessage());
//
//        var erroRecordResponse = new ErroRecordResponse(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Erro interno do servidor. Por favor, tente novamente mais tarde.",
//                null
//        );
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroRecordResponse);
//    }
}