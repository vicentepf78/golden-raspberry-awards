# Golden Raspberry Awards API

API RESTful desenvolvida com Spring Boot para leitura da lista de indicados e vencedores da categoria **Pior Filme** do Golden Raspberry Awards.

---

## Tecnologias

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database (em memória)
- Maven
- Testes de integração com Spring Boot Test + MockMvc

---

## Objetivo da aplicação

- Ler um arquivo CSV contendo os filmes
- Importar os dados automaticamente ao iniciar a aplicação
- Expor um endpoint REST para identificar:
    - o produtor com **menor intervalo** entre dois prêmios consecutivos
    - o produtor com **maior intervalo** entre dois prêmios consecutivos

---

## Endpoint principal

```http
GET /api/awards/producers/intervals
```

## Exemplo de resposta

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

## Regra de cálculo

Regra de cálculo

A API considera apenas filmes vencedores (winner = yes).

Para cada produtor:

1. os filmes vencedores são processados em ordem crescente de ano;
2. o sistema mantém apenas o último ano de vitória de cada produtor;
3. ao encontrar uma nova vitória do mesmo produtor, o intervalo é calculado imediatamente;
4. os menores e maiores intervalos são atualizados durante o processamento.

Exemplo:

Se um produtor venceu em:

- 1990
- 1991
- 2000

Os intervalos calculados serão:

- 1991 - 1990 = 1
- 2000 - 1991 = 9

## Tratamento do campo `producers`

O campo `producers` pode conter múltiplos produtores na mesma linha.

A aplicação trata corretamente formatos como:
- `Producer A`
- `Producer A, Producer B`
- `Producer A and Producer B`
- `Producer A, Producer B and Producer C`

## Otimizações aplicadas

A implementação foi otimizada para melhorar performance e uso de memória:

- Consulta ao banco utiliza projection, trazendo apenas os campos necessários (year e producers); 
- Utilização de algoritmo de varredura única (single-pass);
- Não há criação de listas intermediárias com todos os intervalos; 
- Cálculo de mínimo e máximo ocorre em tempo real durante o processamento;
- Redução significativa de loops no código.

## Estrutura da solução

- `CsvDataLoader` → responsável por importar o CSV na inicialização
- `Movie` → entidade persistida no banco H2
- `MovieRepository` → acesso aos dados com query otimizada
- `MovieAwardProjection` → projection para buscar apenas os campos necessários
- `ProducerAwardIntervalService` → regra de negócio (cálculo dos intervalos)
- `ProducerAwardIntervalController` → endpoint REST
- Testes de integração → validação completa da aplicação

## Pré-requisitos

- Java 21 instalado
- Maven 3.9+ instalado

## Como executar a aplicação

```bash
mvn spring-boot:run
```

A aplicação sobe em:

```text
http://localhost:8080
```

## Como executar os testes

```bash
mvn test
```

## Estratégia de testes

A aplicação possui dois tipos principais de testes:

### 1. Teste com CSV real
Valida o comportamento com os dados oficiais do desafio.

### 2. Testes de cenários controlados
Valida cenários como:
- empate no menor intervalo;
- empate no maior intervalo;
- produtor com múltiplas vitórias;
- múltiplos produtores por filme;
- nenhum produtor com mais de uma vitória.
- dados fora de ordem;
- tratamento de espaços extras.

Nos testes de cenário:
- o banco H2 é controlado manualmente;
- o carregamento automático do CSV é desabilitado com o profile `test`.
- garante isolamento e previsibilidade dos testes.

## Robustez da solução

A lógica foi implementada de forma genérica, garantindo funcionamento correto independentemente do conjunto de dados.

A API suporta cenários como:
- empates no menor intervalo;
- empates no maior intervalo;
- produtores com múltiplas vitórias;
- produtores com apenas uma vitória;
- filmes com múltiplos produtores;
- dados desordenados;
- espaços extras no CSV.

## Maturidade REST

A API foi implementada seguindo o `nível 2 do Modelo de Maturidade de Richardson`:

- uso de recursos REST bem definidos;
- utilização correta de métodos HTTP (GET);
- respostas em JSON com status HTTP apropriados.

## Observações

- O CSV está localizado em:

```text
src/main/resources/data/Movielist.csv
```

- O banco é recriado em memória a cada execução.
- Não é necessário instalar banco de dados externo.
- A aplicação é totalmente autocontida.