package com.analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SpringBootApplication
public class AnalisadorHierarquiaPalavras implements CommandLineRunner {

    private Map<String, Object> hierarquia;
    private long tempoCarregamentoParametros;
    private long tempoVerificacaoFrase;

    @Autowired
    private ResourceLoader resourceLoader;

    public static void main(String[] args) {
        SpringApplication.run(AnalisadorHierarquiaPalavras.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length < 3) {
            System.out.println("COMANDO CORRETO: java -jar cli.jar analyze --depth <n> --verbose (opcional) \"{phrase}\"");
            return;
        }

        long inicioParametros = System.currentTimeMillis();
        int profundidade = 0;
        boolean verbose = false;
        StringBuilder fraseBuilder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--depth":
                    if (i + 1 < args.length) {
                        profundidade = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--verbose":
                    verbose = true;
                    break;
                default:
                    // Concatenando todos os outros argumentos que não são flags ou valores de flags
                    fraseBuilder.append(args[i]).append(" ");
            }
        }

        String frase = fraseBuilder.toString().trim();

        if (frase.isEmpty()) {
            System.out.println("Por favor, forneça uma frase para análise.");
            return;
        }

        tempoCarregamentoParametros = System.currentTimeMillis() - inicioParametros;

        carregarHierarquia();
        realizarAnalise(frase, profundidade);

        if (verbose) {
            exibirMetricas();
        }
    }


    private void carregarHierarquia() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Resource resource = resourceLoader.getResource("classpath:dicts/hierarchy.json");
            InputStream inputStream = resource.getInputStream();
            JsonNode jsonNode = mapper.readTree(inputStream);
            hierarquia = mapper.convertValue(jsonNode, Map.class);
            System.out.println("Hierarquia carregada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void realizarAnalise(String frase, int profundidade) {
        long inicioVerificacao = System.currentTimeMillis();
        Map<String, Integer> resultado = new HashMap<>();

        // Normalizando a frase para que a comparação não seja case-sensitive
        frase = frase.toLowerCase();

        // Dividindo a frase em palavras e removendo pontuações
        String[] palavras = frase.split("\\W+");

        // Buscando cada palavra dada no args através do método
        for (String palavra : palavras) {
            verificarPalavraNaHierarquia(hierarquia, palavra, profundidade, 1, resultado);
        }

        // Output'ando no formato sugerido do desafio
        if (resultado.isEmpty()) {
            System.out.println("0;");
        } else {
            resultado.forEach((categoria, contagem) -> System.out.print(categoria + " = " + contagem + "; "));
        }

        tempoVerificacaoFrase = System.currentTimeMillis() - inicioVerificacao;
    }


    private void verificarPalavraNaHierarquia(Map<String, Object> node, 
    										String palavra, 
    										int profundidadeAtual, 
    										int nivelAtual, 
    										Map<String, Integer> resultado) { 
    	if (nivelAtual > profundidadeAtual) {
            return;
        }

        for (Map.Entry<String, Object> entry : node.entrySet()) {
            String categoria = entry.getKey();
            Object filhos = entry.getValue();

            // System.out.println("Verificando categoria: " + categoria + " no nível " + nivelAtual);
            // System.out.println("Filhos: " + filhos + ".");

            // Verifica se o valor é uma lista de palavras; se sim, busca a palavra em questão
            if (filhos instanceof List) {
                
            	// System.out.println("***** FILHOS INSTANCEOF LIST");
                List<String> lista = (List<String>) filhos;
                for (String item : lista) {
                    if (item.equalsIgnoreCase(palavra)) {
                        // System.out.println("Palavra encontrada: " + palavra + " em categoria: " + categoria + " no nível " + nivelAtual);
                        resultado.put(categoria, resultado.getOrDefault(categoria, 0) + 1);
                        break;  
                    }
                    
                }
                
            } 
            // Continua verificando subníveis se o valor for um mapa (subcategoria)
            else if (filhos instanceof Map) {
            	// System.out.println("*****8 FILHOS INSTANCEOF MAP");
            	verificarPalavraNaHierarquia((Map<String, Object>) filhos, palavra, profundidadeAtual, nivelAtual + 1, resultado);
            } 
            // TODO: ver se esse if é necessário mesmo
            // Caso o valor seja uma palavra diretamente, verificar se corresponde 
            else if (filhos instanceof String) {
            	// System.out.println("Verificando palavras no fundo!!");
                if (filhos.equals(palavra.toLowerCase())) {
                    // System.out.println("Palavra encontrada: " + palavra + " diretamente na categoria: " + categoria + " no nível " + nivelAtual);
                    resultado.put(categoria, resultado.getOrDefault(categoria, 0) + 1);
                }
            }
        }
    }

    private void exibirMetricas() {
        System.out.println("\nTempo de carregamento dos parâmetros: " + tempoCarregamentoParametros + "ms");
        System.out.println("Tempo de verificação da frase: " + tempoVerificacaoFrase + "ms");
    }
}
