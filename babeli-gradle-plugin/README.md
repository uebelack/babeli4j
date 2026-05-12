# Babeli Gradle Plugin

A Gradle plugin that uses AI to automatically manage localization files in your project. It detects missing
translations, sorts keys, and generates translations using LLMs — all integrated into your Gradle build lifecycle.

Built on [LangChain4j](https://docs.langchain4j.dev/) for the AI layer, the plugin ships with support for the
Anthropic API and local models via Ollama, and can easily be extended with additional providers.

Part of the [babeli4j](https://github.com/uebelack/babeli4j) project.

## Features

- **AI-powered translation completion** — automatically generates missing translations using LLMs
- **Automatic detection of translation files** — no configuration needed for standard project layouts
- Supports Properties, JSON, and XML (Android) translation formats
- Validates and updates translation files as part of your Gradle build
- Model provider system (Anthropic, Ollama, or add your own)

## Auto-Detection

The plugin automatically discovers translation files in your project. For most projects, you only need to apply the
plugin and add a model provider dependency — no `babeli { }` configuration block required.

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

Apply the plugin and add a model provider dependency. The model provider is required for AI-powered translation
generation.

### Using Anthropic

#### Groovy DSL (`build.gradle`)

```groovy
plugins {
    id 'dev.uebelacker.babeli' version '1.0.0'
}

dependencies {
    babeli 'dev.uebelacker.babeli:babeli-anthropic:1.0.0'
}
```

#### Kotlin DSL (`build.gradle.kts`)

```kotlin
plugins {
    id("dev.uebelacker.babeli") version "1.0.0"
}

dependencies {
    babeli("dev.uebelacker.babeli:babeli-anthropic:1.0.0")
}
```

The Anthropic provider reads the API key from the `ANTHROPIC_API_KEY` or `BABELI_ANTHROPIC_API_KEY` environment
variable.

### Using Ollama

#### Groovy DSL (`build.gradle`)

```groovy
plugins {
    id 'dev.uebelacker.babeli' version '1.0.0'
}

dependencies {
    babeli 'dev.uebelacker.babeli:babeli-ollama:1.0.0'
}
```

#### Kotlin DSL (`build.gradle.kts`)

```kotlin
plugins {
    id("dev.uebelacker.babeli") version "1.0.0"
}

dependencies {
    babeli("dev.uebelacker.babeli:babeli-ollama:1.0.0")
}
```

Ollama connects to `http://localhost:11434` by default. Override with the `BABELI_OLLAMA_URL` environment variable.

For projects with standard layouts (see Auto-Detection above), that's all you need — the plugin will find your
translation files automatically and apply the default actions.

## Configuration

For non-standard project layouts or advanced use cases, configure the plugin explicitly:

### Per-Language Translation Files

#### Groovy DSL

```groovy
babeli {
    actions = ['sort', 'missing']
    baseLanguage = 'en'
    translationFile 'en', file('src/main/resources/messages_en.properties')
    translationFile 'de', file('src/main/resources/messages_de.properties')
    translationFile 'fr', file('src/main/resources/messages_fr.properties')
}
```

#### Kotlin DSL

```kotlin
babeli {
    actions = listOf("sort", "missing")
    baseLanguage = "en"
    translationFile("en", file("src/main/resources/messages_en.properties"))
    translationFile("de", file("src/main/resources/messages_de.properties"))
    translationFile("fr", file("src/main/resources/messages_fr.properties"))
}
```

### Multi-Language JSON File

#### Groovy DSL

```groovy
babeli {
    actions = ['sort', 'missing']
    multiLanguageFile = file('src/main/resources/translations.json')
}
```

#### Kotlin DSL

```kotlin
babeli {
    actions = listOf("sort", "missing")
    multiLanguageFile = file("src/main/resources/translations.json")
}
```

### XML (Android) Translation Files

#### Groovy DSL

```groovy
babeli {
    actions = ['sort']
    translationFile 'en', file('src/main/res/values/strings.xml')
    translationFile 'de', file('src/main/res/values-de/strings.xml')
}
```

#### Kotlin DSL

```kotlin
babeli {
    actions = listOf("sort")
    translationFile("en", file("src/main/res/values/strings.xml"))
    translationFile("de", file("src/main/res/values-de/strings.xml"))
}
```

### Full Example

#### Groovy DSL

```groovy
babeli {
    actions = ['sort', 'missing']
    baseLanguage = 'en'
    translationFile 'en', file('messages_en.properties')
    translationFile 'de', file('messages_de.properties')
    modelProvider = 'anthropic'
    model = 'claude-sonnet-4-20250514'
}
```

#### Kotlin DSL

```kotlin
babeli {
    actions = listOf("sort", "missing")
    baseLanguage = "en"
    translationFile("en", file("messages_en.properties"))
    translationFile("de", file("messages_de.properties"))
    modelProvider = "anthropic"
    model = "claude-sonnet-4-20250514"
}
```

## Tasks

| Task             | Description                                                                                                         |
|------------------|---------------------------------------------------------------------------------------------------------------------|
| `babeliValidate` | Validates translation files against the configured actions. Fails the build if errors are found.                    |
| `babeliUpdate`   | Updates translation files by applying the configured actions (e.g., sorting keys, completing missing translations). |

```bash
# Validate translation files
./gradlew babeliValidate

# Update translation files
./gradlew babeliUpdate
```

The `babeliValidate` task is automatically added to the `check` lifecycle task, and `babeliUpdate` is added to
`processResources`/`preBuild`.

## Actions

| Action    | Description                                                                                                                                      |
|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `sort`    | Ensures translation keys are sorted alphabetically.                                                                                              |
| `missing` | Detects missing translation keys across language files. When running `babeliUpdate`, uses AI to automatically generate the missing translations. |

## Options

Environment variables, where applicable, override configuration values.

| Property            | Type           | Environment Variable                            | Default                                                    | Description                                                                     |
|---------------------|----------------|-------------------------------------------------|------------------------------------------------------------|---------------------------------------------------------------------------------|
| `actions`           | `List<String>` | —                                               | All registered actions                                     | Actions to apply (e.g., `['sort', 'missing']`).                                 |
| `translationFile`   | method         | —                                               | Auto-detected                                              | Registers a per-language translation file: `translationFile 'en', file('path')` |
| `multiLanguageFile` | `File`         | —                                               | —                                                          | Path to a single multi-language JSON file.                                      |
| `charset`           | `String`       | -                                               | UTF-8                                                      | Character encoding for reading/writing translation files.                       |
| `baseLanguage`      | `String`       | —                                               | `en`                                                       | The base/reference language.                                                    |
| `workingDirectory`  | `File`         | —                                               | Project directory                                          | Working directory for file resolution.                                          |
| `modelProvider`     | `String`       | `BABELI_MODEL_PROVIDER`                         | —                                                          | AI model provider (`anthropic`, `ollama`). Required.                            |
| `model`             | `String`       | `BABELI_ANTHROPIC_MODEL`, `BABELI_OLLAMA_MODEL` | `claude-sonnet-4-20250514` (Anthropic), `qwen3.6` (Ollama) | Model name to use for translations.                                             |
| `apiKey`            | `String`       | `ANTHROPIC_API_KEY`, `BABELI_ANTHROPIC_API_KEY` | —                                                          | API key for the model provider.                                                 |
| `apiUrl`            | `String`       | `BABELI_OLLAMA_URL`                             | `http://localhost:11434` (Ollama)                          | API endpoint URL.                                                               |
| `skip`              | `boolean`      | `BABELI_SKIP`                                   | `false`                                                    | When set to true babeli execution is skipped.                                   |
| —                   | —              | `CI`                                            | —                                                          | When set, disables automatic `babeliUpdate` during builds.                      |

