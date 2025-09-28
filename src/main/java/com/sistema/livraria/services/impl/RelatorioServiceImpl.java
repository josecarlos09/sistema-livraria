package com.sistema.livraria.services.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sistema.livraria.enums.Categoria;
import com.sistema.livraria.enums.StatusLivro;
import com.sistema.livraria.models.LivroModel;
import com.sistema.livraria.repositorys.LivroRepository;
import com.sistema.livraria.services.RelatorioService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioServiceImpl implements RelatorioService {

    final LivroRepository livroRepository;

    public RelatorioServiceImpl(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    /**
     * Gera um relatório PDF com todos os livros cadastrados na base de dados.
     * O relatório contém título, data de geração, e uma tabela com informações dos livros:
     * título, ISBN, valor, editora, quantidade e autores.
     *
     * @param tituloRelatorio Título personalizado para o cabeçalho do relatório.
     *                         Caso nulo ou vazio, será definido como "RELATÓRIO GERAL".
     * @return Um array de bytes representando o conteúdo do relatório em formato PDF.
     */
    @Override
    public byte[] gerarRelatorioGenerico(String tituloRelatorio) {
        // Define título padrão se não informado
        if (tituloRelatorio == null || tituloRelatorio.trim().isEmpty()) {
            tituloRelatorio = "RELATÓRIO GERAL";
        }

        // Recupera todos os livros do banco de dados
        List<LivroModel> livros = livroRepository.findAll();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 30, 30); // margens

        try {
            // Configura o escritor de PDF com suporte a rodapé personalizado
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(rodapeFixo()); // Define rodapé fixo

            document.open();

            // Cores e fontes utilizadas no relatório
            BaseColor vinhoEscuro = new BaseColor(58, 0, 0);
            BaseColor branco = BaseColor.WHITE;
            BaseColor cinzaClaro = new BaseColor(245, 245, 245);
            BaseColor preto = BaseColor.BLACK;

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, vinhoEscuro);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.DARK_GRAY);
            Font campoFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, branco);
            Font dadosFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, preto);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, preto);

            // Linha e título do relatório
            LineSeparator linhaTopo = new LineSeparator(1.5f, 100, vinhoEscuro, Element.ALIGN_CENTER, -2);
            document.add(linhaTopo);

            Paragraph titulo = new Paragraph(tituloRelatorio, tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5f);
            document.add(titulo);
            document.add(linhaTopo);

            // Data de geração do relatório
            Paragraph data = new Paragraph("Gerado em: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
            data.setAlignment(Element.ALIGN_RIGHT);
            data.setSpacingAfter(8f);
            document.add(data);

            // Criação da tabela de dados dos livros
            PdfPTable tabela = new PdfPTable(6);
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(3f);
            tabela.setWidths(new float[]{3f, 2f, 2f, 2.5f, 1.5f, 2.5f});
            tabela.setSplitLate(false);
            tabela.setSplitRows(true);

            // Cabeçalhos da tabela
            tabela.addCell(celulaTabela("TÍTULO", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("ISBN", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("VALOR", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("EDITORA", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("QTD.", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("AUTORES", campoFont, vinhoEscuro, true));

            // Preenchimento da tabela com os dados de cada livro
            for (LivroModel livro : livros) {

                tabela.addCell(celulaTabela(livro.getTitulo(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getIsbn(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela("R$ " + livro.getValor(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getEditora(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getAutor(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(String.valueOf(livro.getQuantidade()), dadosFont, cinzaClaro, false));
            }

            document.add(tabela);

            // Total de livros no final do relatório
            Paragraph total = new Paragraph("TOTAL DE LIVROS: " + livros.size(), totalFont);
            total.setSpacingBefore(8f);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray(); // retorna o PDF como array de bytes
    }

    /**
     * Gera um relatório em PDF agrupando os livros por categoria.
     *
     * O relatório apresenta:
     * - Um cabeçalho com o título do relatório e a data de geração;
     * - Uma listagem dos livros organizados por categoria, contendo:
     *   título, ISBN, valor, autores, editora e quantidade;
     * - Totais por categoria e um total geral ao final;
     * - Rodapé fixo em todas as páginas.
     *
     * O PDF é gerado utilizando a biblioteca iText e retornado como um array de bytes.
     *
     * @return um array de bytes representando o conteúdo do PDF gerado
     */
    @Override
    public byte[] gerarRelatorioLivrosPorCategoria() {
        // Busca todos os livros do banco de dados
        List<LivroModel> livros = livroRepository.findAll();

        // Stream de saída para armazenar o conteúdo do PDF em memória
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Criação do documento PDF com margens ajustadas
        Document document = new Document(PageSize.A4, 20, 20, 30, 30);

        try {
            // Inicializa o escritor do PDF e associa o stream de saída
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(rodapeFixo()); // Define o rodapé fixo para todas as páginas

            document.open(); // Abre o documento para edição

            // Definição de cores personalizadas
            BaseColor vinhoEscuro = new BaseColor(58, 0, 0);
            BaseColor branco = BaseColor.WHITE;
            BaseColor cinzaClaro = new BaseColor(245, 245, 245);
            BaseColor cinzaBorda = new BaseColor(220, 220, 220);
            BaseColor preto = BaseColor.BLACK;

            // Fontes personalizadas para o conteúdo do relatório
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, vinhoEscuro);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            Font categoriaFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, preto);
            Font campoFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, branco);
            Font dadosFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, preto);
            Font rodapeFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, preto);

            // Linhas decorativas usadas entre seções
            LineSeparator linhaTopo = new LineSeparator(1.5f, 100, vinhoEscuro, Element.ALIGN_CENTER, -2);
            LineSeparator linhaFina = new LineSeparator(0.5f, 100, cinzaBorda, Element.ALIGN_CENTER, -2);

            // Adiciona o título do relatório
            document.add(linhaTopo);
            Paragraph titulo = new Paragraph("RELATÓRIO DE LIVROS POR CATEGORIA", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(8f);
            document.add(titulo);
            document.add(linhaTopo);

            // Adiciona a data atual formatada
            Paragraph data = new Paragraph("Gerado em: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
            data.setAlignment(Element.ALIGN_RIGHT);
            data.setSpacingAfter(10f);
            document.add(data);

            // Agrupa os livros pela categoria
            Map<Categoria, List<LivroModel>> agrupado = livros.stream()
                    .collect(Collectors.groupingBy(LivroModel::getCategoria));

            int totalGeral = 0; // Contador de livros totais

            // Percorre cada categoria e gera uma tabela com os livros
            for (Categoria categoria : agrupado.keySet()) {
                List<LivroModel> livrosCategoria = agrupado.get(categoria);
                totalGeral += livrosCategoria.size();

                // Título da categoria
                Paragraph categoriaTitulo = new Paragraph("Categoria: " + categoria.name(), categoriaFont);
                categoriaTitulo.setSpacingBefore(10f);
                categoriaTitulo.setSpacingAfter(5f);
                document.add(categoriaTitulo);

                // Criação da tabela de livros
                PdfPTable tabela = new PdfPTable(6);
                tabela.setWidthPercentage(100);
                tabela.setSpacingBefore(5f);
                tabela.setWidths(new float[]{3f, 2f, 2f, 2.5f, 2.5f, 1.5f});

                // Cabeçalho da tabela
                tabela.addCell(celulaTabela("TÍTULO", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("ISBN", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("VALOR", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("AUTORES", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("EDITORA", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("QTD.", campoFont, vinhoEscuro, true));

                // Preenchimento da tabela com os dados dos livros
                for (LivroModel livro : livrosCategoria) {

                    tabela.addCell(celulaTabela(livro.getTitulo(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(livro.getIsbn(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela("R$ " + livro.getValor(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(livro.getEditora(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(livro.getAutor(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(String.valueOf(livro.getQuantidade()), dadosFont, cinzaClaro, false));
                }

                // Adiciona a tabela ao documento
                document.add(tabela);

                // Total de livros por categoria
                Paragraph totalCategoria = new Paragraph("Total de livros nesta categoria: " + livrosCategoria.size(), dadosFont);
                totalCategoria.setSpacingBefore(5f);
                document.add(totalCategoria);

                // Linha divisória entre categorias
                document.add(Chunk.NEWLINE);
                document.add(linhaFina);
                document.add(Chunk.NEWLINE);
            }

            // Total geral de livros no final do relatório
            Paragraph total = new Paragraph("TOTAL GERAL DE LIVROS: " + totalGeral, totalFont);
            total.setSpacingBefore(10f);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close(); // Finaliza o documento
        } catch (Exception e) {
            e.printStackTrace(); // Em caso de erro, imprime a exceção
        }

        // Retorna os bytes do PDF gerado
        return outputStream.toByteArray();
    }

    /**
     * Gera um relatório em PDF com a listagem de livros agrupados por autor.
     * Cada seção do relatório representa um autor com sua respectiva lista de livros.
     *
     * @return Um array de bytes representando o conteúdo do PDF gerado.
     */
    @Override
    public byte[] gerarRelatorioLivrosPorAutor() {
        // Recupera todos os livros do banco de dados
        List<LivroModel> livros = livroRepository.findAll();

        // Agrupa os livros por nome do autor
        Map<String, List<LivroModel>> livrosPorAutor = livros.stream()
                .filter(livro -> livro.getAutor() != null) // garante que não quebre
                .collect(Collectors.groupingBy(livro -> livro.getAutor()));

        // Fluxo de saída do PDF em memória
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Cria um novo documento com tamanho A4 e margens definidas
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            // Inicializa o writer e configura o rodapé fixo
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(rodapeFixo()); // Método que retorna um rodapé personalizado

            document.open(); // Abre o documento para edição

            // Definições de cores
            BaseColor vinhoEscuro = new BaseColor(58, 0, 0);
            BaseColor branco = BaseColor.WHITE;
            BaseColor cinzaClaro = new BaseColor(245, 245, 245);
            BaseColor preto = BaseColor.BLACK;
            BaseColor cinzaBorda = new BaseColor(220, 220, 220);

            // Definições de fontes utilizadas no documento
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, vinhoEscuro);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            Font autorFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, preto);
            Font campoFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, branco);
            Font dadosFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, preto);
            Font rodapeFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, preto);

            // Separadores gráficos (linhas horizontais)
            LineSeparator linhaTopo = new LineSeparator(1.5f, 100, vinhoEscuro, Element.ALIGN_CENTER, -2);
            LineSeparator linhaFina = new LineSeparator(0.5f, 100, cinzaBorda, Element.ALIGN_CENTER, -2);

            // Título principal
            document.add(linhaTopo);
            Paragraph titulo = new Paragraph("RELATÓRIO DE LIVROS POR AUTOR", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5f);
            document.add(titulo);
            document.add(linhaTopo);

            // Data de geração do relatório
            Paragraph data = new Paragraph("Gerado em: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
            data.setAlignment(Element.ALIGN_RIGHT);
            data.setSpacingAfter(10f);
            document.add(data);

            int totalGeral = 0; // Contador de livros no total
            boolean primeiraSecao = true;

            // Itera sobre cada autor e seus livros
            for (Map.Entry<String, List<LivroModel>> entry : livrosPorAutor.entrySet()) {
                if (!primeiraSecao) {
                    document.add(Chunk.NEWLINE); // Espaço entre seções
                }
                primeiraSecao = false;

                String autor = entry.getKey();
                List<LivroModel> livrosAutor = entry.getValue();
                totalGeral += livrosAutor.size();

                // Nome do autor como subtítulo
                Paragraph autorTitulo = new Paragraph("Autor: " + autor, autorFont);
                autorTitulo.setSpacingBefore(5f);
                autorTitulo.setSpacingAfter(3f);
                document.add(autorTitulo);

                // Tabela com os dados dos livros
                PdfPTable tabela = new PdfPTable(5);
                tabela.setWidthPercentage(100);
                tabela.setSpacingBefore(3f);
                tabela.setWidths(new float[]{3f, 2f, 2f, 2.5f, 1.5f});

                // Cabeçalho da tabela
                tabela.addCell(celulaTabela("TÍTULO", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("ISBN", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("VALOR", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("EDITORA", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("QTD.", campoFont, vinhoEscuro, true));

                // Dados dos livros
                for (LivroModel livro : livrosAutor) {
                    tabela.addCell(celulaTabela(livro.getTitulo(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(livro.getIsbn(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela("R$ " + livro.getValor(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(livro.getEditora(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(String.valueOf(livro.getQuantidade()), dadosFont, cinzaClaro, false));
                }

                document.add(tabela);

                // Total de livros do autor
                Paragraph totalAutor = new Paragraph("Total de livros deste autor: " + livrosAutor.size(), dadosFont);
                totalAutor.setSpacingBefore(4f);
                document.add(totalAutor);

                // Linha separadora entre autores
                document.add(new Chunk(linhaFina));
            }

            // Total geral de livros no final do documento
            Paragraph total = new Paragraph("TOTAL GERAL DE LIVROS: " + totalGeral, totalFont);
            total.setSpacingBefore(10f);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // Fecha o documento após a finalização
            document.close();
        } catch (Exception e) {
            e.printStackTrace(); // Log de erro simples
        }

        // Retorna o conteúdo do PDF como array de bytes
        return outputStream.toByteArray();
    }

    /**
     * Gera um relatório em PDF com os livros agrupados por editora.
     * Cada grupo contém uma tabela com os detalhes dos livros, incluindo:
     * título, ISBN, valor, categoria, autores e quantidade.
     *
     * @return Um array de bytes representando o PDF gerado.
     */
    @Override
    public byte[] gerarRelatorioLivrosPorEditora() {
        // Recupera todos os livros do banco de dados
        List<LivroModel> livros = livroRepository.findAll();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(rodapeFixo()); // rodapé fixo
            document.open();

            // Cores
            BaseColor vinhoEscuro = new BaseColor(58, 0, 0);
            BaseColor branco = BaseColor.WHITE;
            BaseColor cinzaClaro = new BaseColor(245, 245, 245);
            BaseColor cinzaBorda = new BaseColor(220, 220, 220);
            BaseColor preto = BaseColor.BLACK;

            // Fontes
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, vinhoEscuro);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            Font editoraFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, preto);
            Font campoFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, branco);
            Font dadosFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, preto);
            Font rodapeFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, preto);

            // Linhas decorativas
            LineSeparator linhaTopo = new LineSeparator(1.5f, 100, vinhoEscuro, Element.ALIGN_CENTER, -2);
            LineSeparator linhaFina = new LineSeparator(0.5f, 100, cinzaBorda, Element.ALIGN_CENTER, -2);

            // Título do relatório
            document.add(linhaTopo);
            Paragraph titulo = new Paragraph("RELATÓRIO DE LIVROS POR EDITORA", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5f);
            document.add(titulo);
            document.add(linhaTopo);

            // Data
            Paragraph data = new Paragraph("Gerado em: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
            data.setAlignment(Element.ALIGN_RIGHT);
            data.setSpacingAfter(10f);
            document.add(data);

            // Agrupa livros pelo nome da editora
            Map<String, List<LivroModel>> agrupado = livros.stream()
                    .collect(Collectors.groupingBy(l -> l.getEditora() != null ? l.getEditora() : "Desconhecida"));

            int totalGeral = 0;
            boolean primeiraSecao = true;

            for (Map.Entry<String, List<LivroModel>> entry : agrupado.entrySet()) {
                if (!primeiraSecao) {
                    document.add(Chunk.NEWLINE);
                }
                primeiraSecao = false;

                String nomeEditora = entry.getKey();
                List<LivroModel> livrosEditora = entry.getValue();
                totalGeral += livrosEditora.size();

                Paragraph tituloEditora = new Paragraph("Editora: " + nomeEditora, editoraFont);
                tituloEditora.setSpacingBefore(5f);
                tituloEditora.setSpacingAfter(3f);
                document.add(tituloEditora);

                PdfPTable tabela = new PdfPTable(6);
                tabela.setWidthPercentage(100);
                tabela.setSpacingBefore(3f);
                tabela.setWidths(new float[]{3f, 2f, 1.5f, 2.2f, 2.5f, 1.2f});
                tabela.setSplitLate(false);
                tabela.setKeepTogether(false);

                // Cabeçalho
                tabela.addCell(celulaTabela("TÍTULO", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("ISBN", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("VALOR", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("CATEGORIA", campoFont, vinhoEscuro, true));
                tabela.addCell(celulaTabela("AUTOR", campoFont, vinhoEscuro, true)); // singular
                tabela.addCell(celulaTabela("QTD.", campoFont, vinhoEscuro, true));

                // Preenche tabela
                for (LivroModel livro : livrosEditora) {
                    String autor = (livro.getAutor() != null) ? livro.getAutor() : "Desconhecido";

                    tabela.addCell(celulaTabela(livro.getTitulo(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(livro.getIsbn(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela("R$ " + livro.getValor(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(livro.getCategoria().name(), dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(autor, dadosFont, cinzaClaro, false));
                    tabela.addCell(celulaTabela(String.valueOf(livro.getQuantidade()), dadosFont, cinzaClaro, false));
                }

                document.add(tabela);

                Paragraph totalEditora = new Paragraph("Total de livros desta editora: " + livrosEditora.size(), dadosFont);
                totalEditora.setSpacingBefore(4f);
                document.add(totalEditora);

                document.add(new Chunk(linhaFina));
            }

            Paragraph totalFinal = new Paragraph("TOTAL DE LIVROS GERAIS: " + totalGeral, totalFont);
            totalFinal.setSpacingBefore(7f);
            totalFinal.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalFinal);

            document.add(Chunk.NEWLINE);
            document.add(new Chunk(new LineSeparator(0.5f, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, 5)));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    /**
     * Gera um relatório em PDF com todos os livros cujo valor é maior ou igual ao valor mínimo fornecido.
     * O relatório apresenta os dados dos livros ordenados do maior para o menor valor.
     *
     * @param valorMinimo valor mínimo para filtrar os livros.
     * @return um array de bytes representando o conteúdo do PDF gerado.
     */
    @Override
    public byte[] gerarRelatorioLivrosPorValor(double valorMinimo) {
        // Converte o valor mínimo de double para BigDecimal para comparações precisas
        BigDecimal valorMinimoDecimal = BigDecimal.valueOf(valorMinimo);

        // Filtra os livros pelo valor mínimo e ordena do mais caro para o mais barato
        List<LivroModel> livros = livroRepository.findAll().stream()
                .filter(livro -> livro.getValor() != null && livro.getValor().compareTo(valorMinimoDecimal) >= 0)
                .sorted(Comparator.comparing(LivroModel::getValor).reversed())
                .collect(Collectors.toList());

        // Título do relatório com o valor mínimo formatado
        String titulo = String.format("RELATÓRIO DE LIVROS POR VALOR\n(a partir de R$ %.2f)", valorMinimo);

        // Prepara o PDF em memória
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50); // margens do documento

        try {
            // Cria o writer do documento e adiciona o rodapé personalizado
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(rodapeFixo());

            document.open();

            // Define as cores utilizadas
            BaseColor vinhoEscuro = new BaseColor(58, 0, 0);
            BaseColor cinzaClaro = new BaseColor(245, 245, 245);
            BaseColor preto = BaseColor.BLACK;
            BaseColor branco = BaseColor.WHITE;

            // Define as fontes utilizadas
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, vinhoEscuro);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            Font campoFontBranco = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, branco); // cabeçalho da tabela
            Font dadosFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, preto);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, preto);

            // Linha decorativa para o topo do documento
            LineSeparator linhaTopo = new LineSeparator(1.5f, 100, vinhoEscuro, Element.ALIGN_CENTER, -2);

            // Adiciona o título centralizado com espaçamento
            document.add(linhaTopo);
            Paragraph tituloRelatorio = new Paragraph(titulo, tituloFont);
            tituloRelatorio.setAlignment(Element.ALIGN_CENTER);
            tituloRelatorio.setSpacingAfter(8f);
            document.add(tituloRelatorio);
            document.add(linhaTopo);

            // Adiciona a data de geração do relatório
            Paragraph data = new Paragraph("Gerado em: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
            data.setAlignment(Element.ALIGN_RIGHT);
            data.setSpacingAfter(10f);
            document.add(data);

            // Cria a tabela com 5 colunas e define as larguras relativas
            PdfPTable tabela = new PdfPTable(5);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new float[]{3f, 2f, 2f, 2.5f, 1.2f});
            tabela.setHeaderRows(1); // fixa o cabeçalho da tabela nas páginas

            // Cabeçalhos da tabela com fundo vinho e texto branco
            tabela.addCell(celulaTabela("TÍTULO", campoFontBranco, vinhoEscuro, true));
            tabela.addCell(celulaTabela("ISBN", campoFontBranco, vinhoEscuro, true));
            tabela.addCell(celulaTabela("VALOR", campoFontBranco, vinhoEscuro, true));
            tabela.addCell(celulaTabela("EDITORA", campoFontBranco, vinhoEscuro, true));
            tabela.addCell(celulaTabela("QTD.", campoFontBranco, vinhoEscuro, true));

            // Preenche as linhas da tabela com os dados dos livros
            for (LivroModel livro : livros) {
                tabela.addCell(celulaTabela(truncate(livro.getTitulo(), 50), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getIsbn(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela("R$ " + livro.getValor(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getEditora(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(String.valueOf(livro.getQuantidade()), dadosFont, cinzaClaro, false));
            }

            document.add(tabela);

            // Adiciona o total de livros exibidos no relatório
            Paragraph total = new Paragraph("TOTAL DE LIVROS: " + livros.size(), totalFont);
            total.setSpacingBefore(10f);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

        } catch (Exception e) {
            e.printStackTrace(); // loga qualquer erro ocorrido durante a geração
        } finally {
            document.close(); // fecha o documento para garantir a finalização correta
        }

        // Retorna o conteúdo do PDF em formato de array de bytes
        return outputStream.toByteArray();
    }

    /**
     * Gera um relatório em PDF com os livros filtrados por status (ex: DISPONIVEL, INDISPONIVEL, etc.).
     * O PDF contém cabeçalho com data, tabela com os dados dos livros e o total de registros.
     *
     * @param filtroStatus o status pelo qual os livros serão filtrados
     * @return um array de bytes representando o conteúdo do PDF gerado
     */
    @Override
    public byte[] gerarRelatorioLivroPorStatus(String filtroStatus) {
        // Define o status padrão caso o parâmetro esteja nulo ou vazio
        if (filtroStatus == null || filtroStatus.trim().isEmpty()) {
            filtroStatus = "DISPONIVEL";
        }

        // Busca os livros com base no status fornecido
        List<LivroModel> livros = livroRepository.findByStatusLivro(StatusLivro.valueOf(filtroStatus));

        // Criação do PDF em memória
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 50, 50); // Define margens do documento

        try {
            // Inicializa o writer e adiciona o rodapé personalizado
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(rodapeFixo()); // método auxiliar com rodapé padrão

            document.open();

            // --- Cores e fontes personalizadas ---
            BaseColor vinhoEscuro = new BaseColor(58, 0, 0);
            BaseColor cinzaClaro = new BaseColor(245, 245, 245);
            BaseColor preto = BaseColor.BLACK;

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, vinhoEscuro);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            Font campoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE); // Fonte branca para cabeçalho da tabela
            Font dadosFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, preto);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, preto);

            // Linha decorativa superior
            LineSeparator linhaTopo = new LineSeparator(1.5f, 100, vinhoEscuro, Element.ALIGN_CENTER, -2);

            // --- Cabeçalho do relatório ---
            document.add(linhaTopo);

            Paragraph titulo = new Paragraph("RELATÓRIO DE STATUS: " + filtroStatus, tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(8f);
            document.add(titulo);

            document.add(linhaTopo);

            // Adiciona a data de geração
            Paragraph data = new Paragraph("Gerado em: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
            data.setAlignment(Element.ALIGN_RIGHT);
            data.setSpacingAfter(15f);
            document.add(data);

            // --- Criação da tabela de livros ---
            PdfPTable tabela = new PdfPTable(6); // 6 colunas
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(5f);
            tabela.setWidths(new float[]{3f, 2f, 2f, 2.5f, 1.5f, 2f}); // Define larguras relativas das colunas

            // Cabeçalhos da tabela
            tabela.addCell(celulaTabela("TÍTULO", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("ISBN", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("VALOR", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("EDITORA", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("QTD.", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("STATUS", campoFont, vinhoEscuro, true));

            // Dados dos livros
            for (LivroModel livro : livros) {
                tabela.addCell(celulaTabela(truncate(livro.getTitulo(), 50), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getIsbn(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela("R$ " + livro.getValor(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getEditora(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(String.valueOf(livro.getQuantidade()), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getStatusLivro().toString(), dadosFont, cinzaClaro, false));
            }

            document.add(tabela);

            // Total de livros
            Paragraph total = new Paragraph("TOTAL DE LIVROS: " + livros.size(), totalFont);
            total.setSpacingBefore(15f);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // Linha final
            document.add(Chunk.NEWLINE);
            document.add(new Chunk(new LineSeparator(0.7f, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -2)));

        } catch (Exception e) {
            e.printStackTrace(); // Loga qualquer erro ocorrido durante a geração do PDF
        } finally {
            document.close(); // Fecha o documento, liberando o recurso
        }

        // Retorna o conteúdo do PDF como array de bytes
        return outputStream.toByteArray();
    }

    @Override
    public byte[] gerarRelatorioLivrosPorEstoqueZerado() {
        List<LivroModel> livrosZerados = livroRepository.findByQuantidade(0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(rodapeFixo());
            document.open();

            // Cores e fontes
            BaseColor vinhoEscuro = new BaseColor(58, 0, 0);
            BaseColor branco = BaseColor.WHITE;
            BaseColor cinzaClaro = new BaseColor(245, 245, 245);
            BaseColor preto = BaseColor.BLACK;

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, vinhoEscuro);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.DARK_GRAY);
            Font campoFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, branco);
            Font dadosFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, preto);
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, preto);

            // Título
            LineSeparator linhaTopo = new LineSeparator(1.5f, 100, vinhoEscuro, Element.ALIGN_CENTER, -2);
            document.add(linhaTopo);

            Paragraph titulo = new Paragraph("RELATÓRIO DE LIVROS COM ESTOQUE ZERADO", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5f);
            document.add(titulo);
            document.add(linhaTopo);

            // Data
            Paragraph data = new Paragraph("Gerado em: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
            data.setAlignment(Element.ALIGN_RIGHT);
            data.setSpacingAfter(8f);
            document.add(data);

            // Tabela
            PdfPTable tabela = new PdfPTable(8);
            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(3f);
            tabela.setWidths(new float[]{3f, 2f, 2f, 2.5f, 2.5f, 2f, 2f, 3f});
            tabela.setSplitLate(false);
            tabela.setSplitRows(true);

            // Cabeçalhos
            tabela.addCell(celulaTabela("TÍTULO", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("ISBN", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("VALOR", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("EDITORA", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("CATEGORIA", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("CAPA", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("QTD.", campoFont, vinhoEscuro, true));
            tabela.addCell(celulaTabela("AUTOR", campoFont, vinhoEscuro, true));

            // Dados
            for (LivroModel livro : livrosZerados) {
                String autor = (livro.getAutor() != null) ? livro.getAutor() : "Desconhecido";
                String editora = (livro.getEditora() != null) ? livro.getEditora() : "Desconhecida";

                tabela.addCell(celulaTabela(livro.getTitulo(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getIsbn(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela("R$ " + livro.getValor(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(editora, dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getCategoria().name(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(livro.getTipoCapa().toString(), dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela("0", dadosFont, cinzaClaro, false));
                tabela.addCell(celulaTabela(autor, dadosFont, cinzaClaro, false));
            }

            document.add(tabela);

            // Total
            Paragraph total = new Paragraph("TOTAL DE LIVROS COM ESTOQUE ZERADO: " + livrosZerados.size(), totalFont);
            total.setSpacingBefore(8f);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }


    /**
     * Cria uma célula personalizada para a tabela do relatório PDF.
     *
     * @param texto     O texto a ser exibido na célula.
     * @param fonte     A fonte usada para o texto da célula.
     * @param corFundo  A cor de fundo da célula.
     * @param isHeader  Indica se a célula é de cabeçalho (true) ou de conteúdo (false).
     * @return PdfPCell A célula formatada pronta para ser adicionada à tabela.
     */
    private PdfPCell celulaTabela(String texto, Font fonte, BaseColor corFundo, boolean isHeader) {
        // Cria a célula com o texto e a fonte especificada
        PdfPCell cell = new PdfPCell(new Phrase(texto, fonte));

        // Define a cor de fundo da célula
        cell.setBackgroundColor(corFundo);

        // Define os espaçamentos internos (padding) da célula
        cell.setPaddingTop(8f);     // Espaço superior
        cell.setPaddingBottom(8f);  // Espaço inferior
        cell.setPaddingLeft(10f);   // Espaço à esquerda
        cell.setPaddingRight(10f);  // Espaço à direita

        // Define a cor da borda da célula
        cell.setBorderColor(BaseColor.GRAY);

        // Alinha verticalmente o conteúdo ao meio da célula
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Alinhamento horizontal:
        // - Centralizado se for cabeçalho
        // - À esquerda se for conteúdo comum
        cell.setHorizontalAlignment(isHeader ? Element.ALIGN_CENTER : Element.ALIGN_LEFT);

        return cell;
    }

    /**
     * Função auxiliar para truncar textos longos que ultrapassem um determinado número de caracteres.
     * Se o texto for maior que o limite especificado, ele será cortado e finalizado com reticências ("...").
     *
     * @param text   O texto original a ser truncado.
     * @param length O comprimento máximo permitido para o texto.
     * @return Uma versão truncada do texto, com reticências se exceder o limite.
     */
    private String truncate(String text, int length) {
        // Se o texto for nulo, retorna uma string vazia
        if (text == null) return "";

        // Se o texto ultrapassar o tamanho limite, corta e adiciona "..."
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }

    /**
     * Função que cria um rodapé fixo para as páginas do relatório gerado.
     * Adiciona uma linha fina e um texto ao final de cada página, indicando que o relatório foi gerado automaticamente.
     *
     * @return Um objeto PdfPageEventHelper que implementa o evento de rodapé para as páginas.
     */
    private PdfPageEventHelper rodapeFixo() {
        return new PdfPageEventHelper() {
            // Definição da fonte para o texto do rodapé (fonte Helvetica, tamanho 10, itálico, cor cinza)
            Font rodapeFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

            // Cor do cinza claro para a linha do rodapé
            BaseColor cinzaClaro = new BaseColor(211, 211, 211); // light gray

            /**
             * Este método é chamado no final de cada página para adicionar o rodapé.
             * Adiciona uma linha fina e o texto do rodapé ao final da página.
             *
             * @param writer  O escritor do PDF que permite a manipulação de conteúdo.
             * @param document O documento do PDF em que o rodapé será inserido.
             */
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent(); // Obtém o conteúdo direto da página

                // Desenha a linha fina acima do rodapé
                cb.setLineWidth(0.7f); // Define a largura da linha
                cb.setColorStroke(cinzaClaro); // Define a cor da linha
                float xStart = document.left(); // Posição inicial (margem esquerda)
                float xEnd = document.right(); // Posição final (margem direita)
                float y = document.bottom() - 2; // Posição da linha no eixo Y (um pouco acima da margem inferior)

                cb.moveTo(xStart, y); // Move para o ponto de início da linha
                cb.lineTo(xEnd, y); // Desenha a linha até o ponto final
                cb.stroke(); // Finaliza a linha

                // Texto do rodapé
                Phrase rodape = new Phrase("Relatório gerado automaticamente pelo sistema da Livraria Sola Scriptura", rodapeFont);

                // Alinha o texto ao centro abaixo da linha, ajustando a posição com base nas margens
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        rodape,
                        (xStart + xEnd) / 2, // Alinhamento centralizado
                        y - 10, // Posição do texto ligeiramente abaixo da linha
                        0);
            }
        };
    }
}