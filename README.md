# SavageMC

> **SavageMC LTDA** — Servidor de Minecraft competitivo e arcade desenvolvido do zero em Java.  
> Produzido por: **henqks**, **desenhar** e **zrafa**

---

## Sobre o projeto

SavageMC é uma plataforma de servidor Minecraft (1.8–1.21) construída sobre BungeeCord + Bukkit (Spigot 1.8_R3). O ecossistema cobre desde a infraestrutura de proxy até gamemodes competitivos, com sistema de contas, clãs, ranks, cosméticos e mais — desenvolvido internamente.

**Este repositório** contém o plugin **SavageMC Builds** (`savagebuilds.jar`): extensão do WorldEdit para o servidor de construção, com comandos extras (gradiente, xadrez, terraform, schematics, etc.), cargos (Admin / Staff / Builder), tags no chat, scoreboard, integração MySQL e configuração unificada com o WorldEdit.

## Domínio

**Site:** [savagemc.net](https://savagemc.net)  
**Discord:** [discord.gg/savagemc](https://discord.gg/savagemc)

---

## Requisitos (Builds)

- Spigot / Bukkit **1.8** (bytecode Java 8)
- **WorldEdit** no `plugins/` (este plugin estende `WorldEditPlugin`)
- Ver `COMANDOS.txt` e `STAFF.txt` para a lista completa de comandos

## Instalação

1. Coloque `savagebuilds.jar` em `plugins/`
2. Coloque o **WorldEdit** compatível com 1.8 no mesmo diretório
3. Reinicie o servidor
4. A pasta de dados do plugin segue o nome do plugin no `plugin.yml` (ex.: `plugins/Builds/`)

## Funcionalidades principais

| Área | Conteúdo |
|------|----------|
| Construção | `/bs` e `//` — gradiente, xadrez, listras, outline, colorswap, paleta, measure, clear, undo/redo |
| Avançado (Staff) | randomfill, terraform, heightmap, esferas, cilindros, pirâmide, elipsoide, schematics |
| Social | Cargos, tags, `/top`, `/topblocos`, comandos de brincadeira (`savagebuilds.fun`) |
| Dados | MySQL opcional — execute `database.sql` se ativar no `config.yml` |

## Permissões (resumo)

| Permissão | Uso |
|-----------|-----|
| `savagebuilds.build` | Builder — WorldEdit e ferramentas base |
| `savagebuilds.staff` | Staff — ferramentas avançadas + extras |
| `savagebuilds.admin` | Admin — acesso ampliado |
| `savagebuilds.bypass` | Entrar sem cargo (se configurado) |

Em `config.yml`, `open-to-all: true` abre o servidor a todos; caso contrário, exige cargo ou bypass.

## Compilar

No Windows, execute `build.bat` na raiz do projeto. O JAR gerado é `savagebuilds.jar`.

---

*© SavageMC LTDA — Todos os direitos reservados.*
