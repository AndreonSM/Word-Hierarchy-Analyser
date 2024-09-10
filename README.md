# Analisador de hierarquia de palavras


## *Being delevoped by*
**Andreon Souza de Medeiros Roseira**

---
---

## Propósito
Esta aplicação foi (e está sendo) feita com o intuito de:
- Ser vista, estudada e analisada a quem interessar;
- Diversão e aprendizado!


## Descrição do projeto

Este repositório consiste em uma aplicação CLI (*Command Line Interface*) que carrega uma árvore hierárquica de palavras, onde cada nível da árvore representa uma profundidade específica - a aplicação deve analisar uma frase fornecida pelo usuário, identificar a profundidade associada a uma palavra mencionada na frase e exibir os itens mais próximos dessa profundidade.

De forma mais específica: com esta aplicação, você abre uma CLI (e.g. Git Bash, Terminal, CMD) e interage com ela através de comandos específicos para executar o que foi explicado no parágrafo anterior.

### A hierarquia

A aplicação possui uma estrutura hierárquica de palavras, onde cada palavra ou grupo de
palavras pode ter subcategorias, semelhante a uma árvore de classificação - tal estrutura
está representada em um arquivo JSON na pasta `dicts` (em `src/resources` ).


## Como comandar em um terminal

Sintaxe (as chaves não devem ir no input - ver exemplos): 

`java -jar {nome-do-jar-feito}.jar analyze –-depth {n} “{frase a ser analisada}” -–verbose`

Exemplos: 

```
java -jar cli.jar analyze --depth 5 "pApAgAiOs tUlipAs" --verbose
```

```
java -jar cli.jar analyze --depth 2 "Eu amo papagaios" --verbose
```

```
java -jar cli.jar analyze --depth 5 "Eu vi gorilas e papagaios"
```

```
java -jar cli.jar analyze --depth 5 "EU tenho preferência por animais carnívoros" --verbose
``` 

```
java -jar cli.jar analyze --depth 1 "Búfalos, pítons e rouxinóis... nada os supera, além dos girassói, orquídeas e ciprestes " --verbose
``` 


---

## Backlog:

### 1 - Desenvolvimento da CLI
- Desenvolvimento da funcionalidade e testes unitários.
    - Está pronto! Claro, ainda é possível algumas melhorias de qualidade de vida (QoL), mas está funcional conforme o requisitado e esperado.


### 2 - Desenvolvimento front-end

***TODO***


## Pré-requisitos

É esperado que tenha JDK 17, Maven e Git. Se não houver qualquer um destes, instale-os!

- - Verifique se o Java SDK está instalado - no cmd/terminal, execute o comando: 

```
java -version
```  

Exemplo de saída esperada do terminal: 

`java version "17.0.9" 2023-10-17 LTS`

- - Verifique se o Apache Maven está instalado - no cmd/terminal, execute o comando: 

```
mvn -v
``` 

Exemplo de saída esperada do terminal:
```
Apache Maven 3.9.6 (bc0240f3c744dd6b6ec2920b3cd08dcc295161ae)
Maven home: C:\bin\apache-maven-3.9.6
Java version: 17.0.9, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-17
Default locale: pt_BR, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

## Execução

- Clone o projeto do github para a sua máquina;
- Faça a build do seu projeto (tanto faz se pelo terminal ou pela IDE), gerando um .jar na pasta target;
- Abra uma CLI **dentro da pasta `target`** e execute comandos de acordo com a sintaxe exposta acima.

