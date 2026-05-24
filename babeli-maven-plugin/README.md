# Babeli Maven Plugin

A Maven plugin that uses AI to automatically manage localization files in your project. It detects missing
translations, sorts keys, and generates translations using LLMs — all integrated into your Maven build lifecycle.

Built on [LangChain4j](https://docs.langchain4j.dev/) for the AI layer, the plugin ships with support for the
Anthropic API and local models via Ollama, and can easily be extended with additional providers.

Part of the [babeli4j](https://github.com/uebelack/babeli4j) project.

## Features

- **AI-powered translation completion** — automatically generates missing translations using LLMs
- **Automatic detection of translation files** — no configuration needed for standard project layouts
- Supports Properties, JSON, and XML (Android) translation formats
- Validates and updates translation files as part of your Maven build
- Model provider system (Anthropic, Ollama, or add your own)

## Auto-Detection

The plugin automatically discovers translation files in your project. For most projects, you only need to add the
plugin and a model provider dependency — no explicit file configuration required.

### Android Projects

If your project follows the standard Android resource layout, translation files are detected automatically:

```
src/main/res/values/strings.xml        → base language (en)
src/main/res/values-de/strings.xml     → German
src/main/res/values-fr/strings.xml     → French
```

### Java/Kotlin Projects (Message Bundles)

Properties files in the standard resources directory are detected automatically:

```
src/main/resources/messages_en.properties  → English
src/main/resources/messages_de.properties  → German
src/main/resources/messages_fr.properties  → French
```

Multiple bundles (e.g., `messages_*.properties` and `errors_*.properties`) are each detected and handled independently.

## Setup

Add the plugin to your `pom.xml` with a model provider dependency. The model provider is required for AI-powered
translation generation.

### Using Anthropic

```xml

<plugin>
    <groupId>dev.uebelacker.babeli</groupId>
    <artifactId>babeli-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>validate</goal>
                <goal>update</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>dev.uebelacker.babeli</groupId>
            <artifactId>babeli-anthropic</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</plugin>
```

The Anthropic provider reads the API key from the `ANTHROPIC_API_KEY` or `BABELI_ANTHROPIC_API_KEY` environment
variable.

### Using Ollama

```xml

<plugin>
    <groupId>dev.uebelacker.babeli</groupId>
    <artifactId>babeli-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>validate</goal>
                <goal>update</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>dev.uebelacker.babeli</groupId>
            <artifactId>babeli-ollama</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</plugin>
```

Ollama connects to `http://localhost:11434` by default. Override with the `BABELI_API_URL` environment variable.

For projects with standard layouts (see Auto-Detection above), that's all you need — the plugin will find your
translation files automatically and apply the default actions.

## Configuration

For non-standard project layouts or advanced use cases, configure the plugin explicitly:

### Per-Language Translation Files

```xml

<plugin>
    <groupId>dev.uebelacker.babeli</groupId>
    <artifactId>babeli-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <actions>
            <action>sort</action>
            <action>missing</action>
        </actions>
        <baseLanguage>en</baseLanguage>
        <files>
            <en>src/main/resources/messages_en.properties</en>
            <de>src/main/resources/messages_de.properties</de>
            <fr>src/main/resources/messages_fr.properties</fr>
        </files>
    </configuration>
</plugin>
```

### Multi-Language JSON File

```xml

<configuration>
    <actions>
        <action>sort</action>
        <action>missing</action>
    </actions>
    <file>src/main/resources/translations.json</file>
</configuration>
```

### XML (Android) Translation Files

```xml

<configuration>
    <actions>
        <action>sort</action>
    </actions>
    <files>
        <en>src/main/res/values/strings.xml</en>
        <de>src/main/res/values-de/strings.xml</de>
    </files>
</configuration>
```

### Full Example

```xml

<plugin>
    <groupId>dev.uebelacker.babeli</groupId>
    <artifactId>babeli-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <actions>
            <action>sort</action>
            <action>missing</action>
        </actions>
        <baseLanguage>en</baseLanguage>
        <files>
            <en>src/main/resources/messages_en.properties</en>
            <de>src/main/resources/messages_de.properties</de>
        </files>
        <modelProvider>anthropic</modelProvider>
        <model>claude-sonnet-4-20250514</model>
    </configuration>
    <executions>
        <execution>
            <id>validate</id>
            <goals>
                <goal>validate</goal>
            </goals>
        </execution>
        <execution>
            <id>update</id>
            <goals>
                <goal>update</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>dev.uebelacker.babeli</groupId>
            <artifactId>babeli-anthropic</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</plugin>
```

## Goals

| Goal              | Default Phase        | Description                                                                                                         |
|-------------------|----------------------|---------------------------------------------------------------------------------------------------------------------|
| `babeli:validate` | `verify`             | Validates translation files against the configured actions. Fails the build if errors are found.                    |
| `babeli:update`   | `generate-resources` | Updates translation files by applying the configured actions (e.g., sorting keys, completing missing translations). |

```bash
# Validate translation files
mvn babeli:validate

# Update translation files
mvn babeli:update
```

## Actions

| Action    | Description                                                                                                                                       |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `sort`    | Ensures translation keys are sorted alphabetically.                                                                                               |
| `missing` | Detects missing translation keys across language files. When running `babeli:update`, uses AI to automatically generate the missing translations. |

## Options

Environment variables, where applicable, override configuration values.

| Parameter          | Property                  | Environment Variable                            | Default                                                    | Description                                                               |
|--------------------|---------------------------|-------------------------------------------------|------------------------------------------------------------|---------------------------------------------------------------------------|
| `files`            | —                         | —                                               | Auto-detected                                              | Map of language codes to translation file paths.                          |
| `file`             | `babeli.file`             | —                                               | —                                                          | Path to a single multi-language translation file.                         |
| `charset`          | `babeli.charset`          | —                                               | `UTF-8`                                                    | Character encoding for reading/writing files.                             |
| `baseLanguage`     | `babeli.baseLanguage`     | —                                               | `en`                                                       | The base/reference language.                                              |
| `workingDirectory` | `babeli.workingDirectory` | —                                               | `${project.basedir}`                                       | Working directory for file resolution.                                    |
| `actions`          | —                         | —                                               | All registered actions                                     | Actions to apply (e.g., `sort`, `missing`).                               |
| `modelProvider`    | `babeli.modelProvider`    | `BABELI_MODEL_PROVIDER`                         | —                                                          | AI model provider (`anthropic`, `ollama`). Required for `missing` action. |
| `model`            | `babeli.model`            | `BABELI_MODEL`                                  | `claude-sonnet-4-20250514` (Anthropic), `qwen3.6` (Ollama) | Model name to use for translations.                                       |
| `apiKey`           | `babeli.apiKey`           | `ANTHROPIC_API_KEY`, `BABELI_ANTHROPIC_API_KEY` | —                                                          | API key for the model provider.                                           |
| `apiUrl`           | `babeli.apiUrl`           | `BABELI_API_URL`                                | `http://localhost:11434` (Ollama)                          | API endpoint URL.                                                         |
| `skip`             | `babeli.skip`             | —                                               | `false`                                                    | Skip plugin execution entirely.                                           |
