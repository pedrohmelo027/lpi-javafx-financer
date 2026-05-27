# Prompt Otimizado — App Desktop de Controle Financeiro em JavaFX

Desenvolva um aplicativo desktop moderno, simples, rápido e intuitivo utilizando **JavaFX** e **Java 21+** para gerenciamento financeiro pessoal.

O sistema deve possuir arquitetura limpa, código organizado, interface profissional e foco em usabilidade.

---

# Objetivo

Criar um sistema de controle financeiro pessoal com:

- cadastro de receitas e despesas
- visualização de movimentações
- acompanhamento de saldo
- dashboards gráficos
- filtros mensais
- experiência simples e elegante

---

# Requisitos Técnicos

## Stack obrigatória

- Java 21+
- JavaFX
- Maven ou Gradle
- SQLite para persistência local
- Padrão MVC
- CSS para estilização da interface

---

# Regras Gerais

- O sistema terá apenas 1 ator: o usuário
- Interface minimalista e moderna
- Navegação simples
- Componentes reutilizáveis
- Código altamente organizado e desacoplado
- Responsividade básica da interface
- Persistência automática dos dados
- Campos validados
- Máscara monetária em reais (R$)
- Datas no padrão brasileiro

---

# Funcionalidades

# 1. Cadastro de Receita e Despesa

Criar formulário para adicionar movimentações financeiras.

## Campos obrigatórios

- valor
- tipo:
  - entrada
  - saída
- categoria
- data
- descrição opcional

## Categorias fixas

- comida
- transporte
- casa
- lazer
- salário

## Regras

- valor deve ser positivo
- data não pode ser inválida
- saída reduz saldo
- entrada aumenta saldo

## Ações

- botão:
  - “+ Nova Despesa”
  - “+ Nova Receita”

---

# 2. Lista de Movimentações

Criar tabela moderna exibindo:

- data
- categoria
- tipo
- valor
- descrição

## Recursos

- edição de movimentações
- exclusão com confirmação
- ordenação por data
- destaque visual:
  - entradas em verde
  - saídas em vermelho

---

# 3. Dashboard Financeiro

Criar painel principal no topo da aplicação mostrando:

## Cards de resumo

- total de entradas
- total de saídas
- saldo atual

## Regras visuais

- saldo positivo → verde
- saldo negativo → vermelho

---

# 4. Filtro Mensal

Adicionar filtro por:

- mês
- ano

O sistema deve atualizar automaticamente:

- tabela
- saldo
- gráficos
- indicadores

---

# 5. Dashboard Gráfico

Criar dashboards simples, modernos e intuitivos.

## Gráfico Pizza

Exibir distribuição dos gastos por categoria no mês atual.

### Requisitos

- cores diferentes por categoria
- legenda visível
- porcentagem por categoria
- atualização automática

---

## Gráfico de Barras

Comparar gastos entre meses.

### Requisitos

- barras agrupadas por mês
- categorias separadas por cores dentro da mesma barra
- comparação visual clara
- animações suaves opcionais

---

# Interface

## Layout esperado

### Topo

- cards financeiros
- filtro mensal

### Centro

- tabela de movimentações

### Lateral ou inferior

- gráficos

---

# Estilo Visual

Desejo um visual semelhante a dashboards modernos:

- clean
- minimalista
- cores suaves
- cantos arredondados
- sombras leves
- tipografia moderna

Utilize CSS no JavaFX para estilização.

---

# Persistência

Utilizar SQLite local.

## Requisitos

- criação automática do banco
- criação automática das tabelas
- DAO organizado
- operações CRUD completas

---

# Estrutura Esperada

Organize o projeto em camadas:

- model
- view
- controller
- service
- repository/dao
- util

---

# Extras Desejáveis

- animações leves no JavaFX
- atualização em tempo real dos dashboards
- exportação CSV
- tema dark/light
- validações amigáveis
- mensagens de feedback ao usuário

---

# Resultado Esperado

Gerar:

- código completo
- estrutura de pastas
- classes organizadas
- arquivos FXML
- CSS da interface
- integração com SQLite
- aplicação pronta para execução

O resultado deve ser profissional, limpo, escalável e de fácil manutenção.