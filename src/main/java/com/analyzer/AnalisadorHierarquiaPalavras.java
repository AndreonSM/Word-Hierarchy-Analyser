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
    private Map<String, Integer> resultado; 

    @Autowired
    private ResourceLoader resourceLoader;

    public static void main(String[] args) {

        if (args.length <= 3) {
            System.out.println("COMANDO CORRETO: java -jar cli.jar analyze --depth <n> --verbose \"{frase}\"");
            System.exit(0);
            return;
        }
        SpringApplication.run(AnalisadorHierarquiaPalavras.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        long inicioParametros = System.currentTimeMillis();
        int profundidade = 0;
        boolean verbose = false;
        Map<String, Integer> resultado;
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
                    fraseBuilder.append(args[i]).append(" ");
            }
        }

        String frase = fraseBuilder.toString().trim();
        System.out.println(frase);

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

        System.exit(0);
    }

    void carregarHierarquia() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Resource resource = resourceLoader.getResource("classpath:dicts/hierarchy.json");
            InputStream inputStream = resource.getInputStream();
            JsonNode jsonNode = mapper.readTree(inputStream);
            hierarquia = mapper.convertValue(jsonNode, Map.class);
            //System.out.println("Hierarquia carregada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void realizarAnalise(String frase, int profundidade) {
        long inicioVerificacao = System.currentTimeMillis();
        this.resultado = new HashMap<>();

        // Normalizando a frase para que a comparação não seja case-sensitive
        frase = frase.toLowerCase();

        // Dividindo a frase em palavras e removendo pontuações
        String[] palavras = frase.split("[^\\p{L}]+");

        // Buscando cada palavra na hierarquia
        for (String palavra : palavras) {
            //System.out.println(verificarPalavraNaHierarquia(hierarquia, palavra, profundidade, 1, resultado));
            verificarPalavraNaHierarquia(hierarquia, palavra, profundidade, 1, resultado);
        }

        // Exibindo o resultado no formato solicitado
        if (resultado.isEmpty()) {
            System.out.println("0;");
        } else {
            resultado.forEach((categoria, contagem) -> System.out.print(categoria + " = " + contagem + "; "));
        }
        System.out.println("");
        
        tempoVerificacaoFrase = System.currentTimeMillis() - inicioVerificacao;
    }
    
    /**
     * Método para verificar a palavra na hierarquia e respeitar a profundidade especificada.
     */
    private boolean verificarPalavraNaHierarquia(Map<String, Object> node, 
                                                 String palavra, 
                                                 int profundidadeDesejada, 
                                                 int nivelAtual, 
                                                 Map<String, Integer> resultado) {

        for (Map.Entry<String, Object> entry : node.entrySet()) {
            String categoria = entry.getKey();
            Object filhos = entry.getValue();

            // Verifica se o valor é uma lista de palavras
            if (filhos instanceof List) {
                List<String> lista = (List<String>) filhos;
                for (String item : lista) {
                    if (item.equalsIgnoreCase(palavra)) {
                        // Se estamos dentro da profundidade desejada, conta a categoria
                        if (nivelAtual <= profundidadeDesejada) {
                            resultado.put(categoria, resultado.getOrDefault(categoria, 0) + 1);
                        }
                        // Encerra busca ao encontrar a palavra
                        return true; 
                    }
                }
            }
            // Continua verificando subcategorias se o valor for um mapa (i.e., não é o nível mais profundo do JSON
            else if (filhos instanceof Map) {
                boolean encontrada = verificarPalavraNaHierarquia((Map<String, Object>) filhos, palavra, profundidadeDesejada, nivelAtual + 1, resultado);
                if (encontrada && nivelAtual == profundidadeDesejada) {
                    // Se encontramos a palavra em um nível mais profundo, somamos na categoria atual
                    resultado.put(categoria, resultado.getOrDefault(categoria, 0) + 1);
                }
                if (encontrada) {
                    return true; 
                }
            }
        }
        // Encerra busca, como falso, se não puder encontrar nada
        return false; 
    }

    private void exibirMetricas() {
        System.out.println("\nTempo de carregamento dos parâmetros: " + tempoCarregamentoParametros + "ms");
        System.out.println("Tempo de verificação da frase: " + tempoVerificacaoFrase + "ms");
    }
    
    // Método para a classe de testes
    public String getResultado() {
        if (resultado == null || resultado.isEmpty()) {
            return "0;";
        }

        StringBuilder sb = new StringBuilder();
        resultado.forEach((categoria, contagem) -> sb.append(categoria).append(" = ").append(contagem).append("; "));
        return sb.toString().trim(); // Removendo espaços extras no final
    }
    
    public Map<String, Integer> getResultadoMap() {
    	System.out.println(resultado);
        return resultado;
    }
    
    // Método setter para o ResourceLoader (para a classe de testes também)
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}