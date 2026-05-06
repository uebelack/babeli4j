# Babeli4j

[![CI](https://github.com/uebelack/babeli4j/actions/workflows/ci.yml/badge.svg)](https://github.com/uebelack/babeli4j/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=uebelack_babeli4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=uebelack_babeli4j)

AI-powered translation file management for Java projects. Babeli detects missing translations, sorts keys, and generates
translations using LLMs — integrated into your Maven or Gradle build, or used standalone via CLI.

Built on [LangChain4j](https://docs.langchain4j.dev/) with support for Anthropic and Ollama out of the box.

## Features

- **AI-powered translation completion** — automatically generates missing translations using LLMs
- **Automatic detection of translation files** — no configuration needed for standard project layouts
- **Multiple formats** — Properties, JSON, and XML (Android)
- **Build tool integration** — Maven plugin, Gradle plugin, and standalone CLI
- **Pluggable model providers** — Anthropic, Ollama, or add your own

## Quick Start

### Maven

Add the plugin to your `pom.xml`:

```xml

<plugin>
    <groupId>dev.uebelacker.babeli</groupId>
    <artifactId>babeli-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <dependencies>
        <dependency>
            <groupId>dev.uebelacker.babeli</groupId>
            <artifactId>babeli-anthropic</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</plugin>
```

```bash
export ANTHROPIC_API_KEY=sk-...

mvn babeli:validate   # check for issues
mvn babeli:update     # sort keys and generate missing translations
```

### Gradle

```groovy
plugins {
    id 'dev.uebelacker.babeli' version '1.0-SNAPSHOT'
}

dependencies {
    babeli 'dev.uebelacker.babeli:babeli-anthropic:1.0-SNAPSHOT'
}
```

```bash
export ANTHROPIC_API_KEY=sk-...

./gradlew babeliValidate   # check for issues
./gradlew babeliUpdate     # sort keys and generate missing translations
```

### CLI

```bash
babeli validate -p anthropic
babeli update -p anthropic
```

For most projects with standard layouts, that's all you need — Babeli will find your translation files automatically.

## Auto-Detection

Babeli automatically discovers translation files in standard project layouts:

**Java/Kotlin projects:**

```
src/main/resources/messages_en.properties  → English
src/main/resources/messages_de.properties  → German
src/main/resources/messages_fr.properties  → French
```

**Android projects:**

```
src/main/res/values/strings.xml        → base language (en)
src/main/res/values-de/strings.xml     → German
src/main/res/values-fr/strings.xml     → French
```

Multiple bundles (e.g., `messages_*.properties` and `errors_*.properties`) are each detected and handled independently.

## Actions

| Action    | Description                                                                                                      |
|-----------|------------------------------------------------------------------------------------------------------------------|
| `sort`    | Ensures translation keys are sorted alphabetically.                                                              |
| `missing` | Detects missing translation keys across language files. On update, uses AI to generate the missing translations. |

Both actions run by default. To select specific actions, configure them explicitly in your build plugin or pass
`-a sort,missing` via CLI.

## Supported Formats

| Format        | Extensions    | Single-language       | Multi-language                            |
|---------------|---------------|-----------------------|-------------------------------------------|
| Properties    | `.properties` | one file per language | —                                         |
| JSON          | `.json`       | one file per language | single file with nested language keys     |
| XML (Android) | `.xml`        | one file per language | single file with nested language elements |

## Model Providers

### Anthropic

Uses the Anthropic API. Set your API key via environment variable:

```bash
export ANTHROPIC_API_KEY=sk-...       # or BABELI_ANTHROPIC_API_KEY
```

Default model: `claude-sonnet-4-20250514`. Override with `BABELI_ANTHROPIC_MODEL`.

### Ollama

Uses a local Ollama instance at `http://localhost:11434` by default.

```bash
export BABELI_OLLAMA_URL=http://my-server:11434   # optional
```

Default model: `qwen3.6`. Override with `BABELI_OLLAMA_MODEL`.

## CI Integration

When the `CI` environment variable is set, Babeli automatically switches to validation-only mode — translation files are
never modified during CI builds. No special configuration needed.

```bash
# Skip the plugin entirely
mvn verify -Dbabeli.skip=true

# Gradle: disable automatic update
BABELI_DISABLED=true ./gradlew build
```

## Modules

| Module                 | Description                                                                     |
|------------------------|---------------------------------------------------------------------------------|
| `babeli-core`          | Core library — reading, validating, transforming, and writing translation files |
| `babeli-anthropic`     | Anthropic model provider                                                        |
| `babeli-ollama`        | Ollama model provider                                                           |
| `babeli-cli`           | Standalone command-line tool                                                    |
| `babeli-maven-plugin`  | Maven build plugin ([docs](babeli-maven-plugin/README.md))                      |
| `babeli-gradle-plugin` | Gradle build plugin ([docs](babeli-gradle-plugin/README.md))                    |

## Building from Source

Requires Java 21+.

```bash
mvn clean install
```

## Environment Variables

| Variable                   | Description                                            |
|----------------------------|--------------------------------------------------------|
| `ANTHROPIC_API_KEY`        | Anthropic API key                                      |
| `BABELI_ANTHROPIC_API_KEY` | Anthropic API key (takes priority)                     |
| `BABELI_ANTHROPIC_MODEL`   | Override Anthropic model name                          |
| `BABELI_OLLAMA_URL`        | Ollama API endpoint                                    |
| `BABELI_OLLAMA_MODEL`      | Override Ollama model name                             |
| `BABELI_MODEL_PROVIDER`    | Override model provider (`anthropic`, `ollama`)        |
| `BABELI_BASE_LANGUAGE`     | Override base language (default: `en`)                 |
| `CI`                       | When set, switches to validate-only mode               |
| `BABELI_DISABLED`          | When set (Gradle only), disables automatic update task |

## License

MIT