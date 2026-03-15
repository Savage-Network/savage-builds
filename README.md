# SpartaMC Builds

Plugin de construção para servidores Minecraft (Spigot/Bukkit 1.8). Baseado no WorldEdit com ferramentas extras de build, sistema de cargos, tags e prefixos no chat.

**SpartaMC** — [spartamc.com.br](https://spartamc.com.br)

---

## Funcionalidades

- **Comandos de construção** — Gradiente, xadrez, listras, contorno, troca de cores, paletas
- **Ferramentas avançadas** — Preenchimento aleatório, terraform, heightmap, círculos
- **Schematics** — Salvar e carregar construções
- **Sistema de cargos** — Admin, Staff, Builder com permissões hierárquicas
- **Tags/Prefixos** — Prefixos coloridos no chat (ADMIN, STAFF, BUILDER, LIRU, MEMBRO)
- **Acesso restrito** — Apenas jogadores com cargo podem entrar no servidor

---

## Instalação

1. Coloque o arquivo `SpartaBuilds.jar` na pasta `plugins` do servidor
2. Reinicie o servidor
3. O plugin cria automaticamente a pasta `plugins/SpartaBuilds/` com arquivos de configuração

**Requisitos:** Spigot ou Bukkit 1.8+

---

## Comandos principais

| Comando | Descrição |
|---------|-----------|
| `/bs` ou `//` | Comando principal (aliases: builds) |
| `/sb` | Ajuda completa e lista de comandos |

---

## Comandos de construção (BUILDER)

| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `/bs gradient <bloco1> <bloco2> [v\|h\|d]` | Gradiente entre blocos (v=vertical, h=horizontal, d=profundidade) | `// gradient 35:0 35:15 v` |
| `/bs checkerboard <bloco1> <bloco2> [tam]` | Padrão xadrez | `// checkerboard stone brick 2` |
| `/bs stripes <bloco1> <bloco2> [larg] [eixo]` | Listras alternadas | `// stripes wool:0 wool:15 2 y` |
| `/bs outline <bloco>` | Arestas da seleção | `// outline stone` |
| `/bs colorswap <cor_de> <cor_para>` | Troca cores em blocos coloridos (0–15) | `// colorswap 11 14` |
| `/bs palette [wool\|clay\|glass]` | Paleta de cores de referência | `// palette wool` |
| `/bs measure` | Medir dimensões da seleção | `// measure` |
| `/bs clear` | Limpar seleção | `// clear` |
| `/bs undo [n]` | Desfazer alterações | `// undo 5` |
| `/bs redo [n]` | Refazer alterações | `// redo 3` |

### Cores (colorswap)

`0` branco · `1` laranja · `2` magenta · `3` azul claro · `4` amarelo · `5` verde claro · `6` rosa · `7` cinza · `8` cinza claro · `9` ciano · `10` roxo · `11` azul · `12` marrom · `13` verde · `14` vermelho · `15` preto

---

## Comandos avançados (STAFF)

| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `/bs randomfill <bloco> <peso> ...` | Preenchimento aleatório com pesos | `// randomfill 1 60 4 25 98 15` |
| `/bs terraform [camadas]` | Naturalizar terreno | `// terraform 3` |
| `/bs heightmap <raise\|lower\|smooth> [força]` | Moldar altura do terreno | `// heightmap smooth 10` |
| `/bs circle <bloco> <raio> [preenchido]` | Círculo horizontal | `// circle stone 15 true` |
| `/bs save <nome>` | Salvar schematic | `// save castelo` |
| `/bs load <nome>` | Carregar schematic | `// load castelo` |
| `/bs list` | Listar schematics | `// list` |

---

## Sistema de tags

| Comando | Descrição |
|---------|-----------|
| `/tag` | Menu interativo — clique nas tags para trocar |
| `/tag <nome>` | Definir prefixo (membro, builder, staff, admin, liru) |

**Tags disponíveis:**
- **MEMBRO** — Sem prefixo (padrão)
- **BUILDER** — Verde
- **STAFF** — Azul
- **ADMIN** — Vermelho
- **LIRU** — Nome amarelo itálico

---

## Gerenciamento (ADMIN)

| Comando | Descrição |
|---------|-----------|
| `/setcargo <nick> <admin\|staff\|builder\|none>` | Definir cargo de um jogador online |

O cargo concede permissões automaticamente. Use `none` para remover.

---

## Permissões

| Permissão | Descrição |
|-----------|-----------|
| `spartabuilds.build` | Construtor — comandos básicos |
| `spartabuilds.staff` | Staff — comandos avançados + build |
| `spartabuilds.admin` | Admin — acesso total |

**Importante:** Apenas jogadores com cargo ou permissão podem entrar no servidor.

---

## Dicas

- Use o **bastão de madeira** (padrão) para selecionar regiões
- Blocos: ID ou `ID:data` (ex: `35:14` = lã vermelha)
- `//` é atalho rápido para `/bs`
- Use `/sb` para ajuda completa no jogo

---

## Estrutura de arquivos

```
plugins/SpartaBuilds/
├── config.yml    (WorldEdit)
├── tags.yml      (tags dos jogadores)
└── cargos.yml    (cargos definidos via /setcargo)
```

---

SpartaMC Builds · Versão 3.0-SpartaMC
