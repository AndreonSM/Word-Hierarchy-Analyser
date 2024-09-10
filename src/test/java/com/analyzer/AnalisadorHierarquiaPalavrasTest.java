package com.analyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AnalisadorHierarquiaPalavrasTest {

    private AnalisadorHierarquiaPalavras analisador;
    private ResourceLoader resourceLoader;

    @BeforeEach
    public void setUp() throws Exception {
        // Mockando o ResourceLoader e Resource
        resourceLoader = mock(ResourceLoader.class);
        Resource resource = mock(Resource.class);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dicts/hierarchy.json");

        // Mockando o comportamento do ResourceLoader para retornar o arquivo JSON correto
        when(resourceLoader.getResource(anyString())).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);

        // Inicializando o analisador e injetando o ResourceLoader
        analisador = new AnalisadorHierarquiaPalavras();
        analisador.setResourceLoader(resourceLoader);

        // Carregar hierarquia antes dos testes
        analisador.carregarHierarquia();
    }

    @Test
    public void testAnaliseProfundidade3() {
        // Testando a análise com profundidade 3
        analisador.realizarAnalise("Eu amo papagaios", 2);
        
    }

    @Test
    public void testAnaliseProfundidade1() {
        // Testando a análise com profundidade 1
        analisador.realizarAnalise("Eu amo papagaios e tulipas ", 3);
        
        
    }

    @Test
    public void testTextoComMaisDe5000Caracteres() {
        // Criando um texto longo com mais de 5000 caracteres
        String textoLongo = "tulipas papagaios búfalos ".repeat(450);

        // Testando a análise com profundidade 1
        analisador.realizarAnalise(textoLongo, 1);
        
        
    }
}
