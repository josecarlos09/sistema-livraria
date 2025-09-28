package com.sistema.livraria.Controllers;

import com.sistema.livraria.services.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios/livros")
@RequiredArgsConstructor
public class RelatorioLivroController {

    private final RelatorioService relatorioService;

    @GetMapping("/generico")
    public ResponseEntity<byte[]> gerarRelatorioGenerico(@RequestParam(value = "titulo", defaultValue = "RELATORIO GERAL") String titulo) {
        byte[] pdf = relatorioService.gerarRelatorioGenerico(titulo);
        return montarRespostaPdf(pdf, "relatorio_generico_livros.pdf");
    }

    @GetMapping("/por-categoria")
    public ResponseEntity<byte[]> gerarRelatorioLivrosPorCategoria() {
        byte[] pdf = relatorioService.gerarRelatorioLivrosPorCategoria();
        return montarRespostaPdf(pdf, "relatorio_livros_por_categoria.pdf");
    }

    @GetMapping("/por-autor")
    public ResponseEntity<byte[]> gerarRelatorioLivrosPorAutor() {
        byte[] pdf = relatorioService.gerarRelatorioLivrosPorAutor();
        return montarRespostaPdf(pdf, "relatorio_livros_por_autor.pdf");
    }

    @GetMapping("/por-valor")
    public ResponseEntity<byte[]> gerarRelatorioLivrosPorValor(@RequestParam(defaultValue = "0.0") double valorMinimo) {
        byte[] pdf = relatorioService.gerarRelatorioLivrosPorValor(valorMinimo);
        return montarRespostaPdf(pdf, "relatorio_livros_por_valor.pdf");
    }

    @GetMapping("/por-editora")
    public ResponseEntity<byte[]> gerarRelatorioLivrosPorEditora() {
        byte[] pdf = relatorioService.gerarRelatorioLivrosPorEditora();
        return montarRespostaPdf(pdf, "relatorio_livros_por_editora.pdf");
    }

    @GetMapping("/por-status")
    public ResponseEntity<byte[]> gerarRelatorioLivrosPorStatus(@RequestParam(value = "status", defaultValue = "DISPONIVEL") String status) {
        byte[] pdf = relatorioService.gerarRelatorioLivroPorStatus(status);
        return montarRespostaPdf(pdf, "relatorio_livros_por_status.pdf");
    }

    @GetMapping("/por-estoque-zerado")
    public ResponseEntity<byte[]> gerarRelatorioLivrosPorEstoqueZerado() {
        byte[] pdf = relatorioService.gerarRelatorioLivrosPorEstoqueZerado();
        return montarRespostaPdf(pdf, "relatorio_livros_por_estoque_zerado.pdf");
    }

    // Método privado auxiliar para evitar repetição de código
    private ResponseEntity<byte[]> montarRespostaPdf(byte[] pdf, String nomeArquivo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(nomeArquivo)
                .build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
