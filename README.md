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

## Compilar (IntelliJ IDEA + Gradle)

O projeto usa **Gradle** (`build.gradle.kts`). Código-fonte: `src/main/java`.

1. **Abrir o projeto:** *File → Open* na pasta do repositório (deteta `build.gradle.kts`).
2. **JDK para o Gradle:** use **JDK 17+** (ou o JDK que o IntelliJ associar ao Gradle). O **bytecode do plugin** continua **Java 8** (`build.gradle.kts`).
3. **Sync Gradle:** aguarde o ícone do elefante / *Import* terminar.
4. **Gerar o JAR:** painel **Gradle** → *savagebuilds* → *Tasks* → *build* → **`build`**, ou terminal: `gradlew.bat build`.
5. **Saída:** `build/libs/savagebuilds.jar` (`build.bat` copia também `savagebuilds.jar` para a raiz).

### Linha de comandos

```text
gradlew.bat clean build
```

**Dependências:** Spigot API e WorldEdit são `compileOnly` (servidor fornece em runtime). FastBoard e MySQL Connector vão no JAR. Na primeira vez é preciso **internet** (Gradle + dependências).

---

*© SavageMC LTDA — Todos os direitos reservados.*
