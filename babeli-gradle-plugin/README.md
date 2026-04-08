# Babeli Gradle Plugin

A Gradle plugin for managing and validating translation files using [babeli4j](https://github.com/uebelack/babeli4j).

## Setup

Add the plugin to your `build.gradle`:

```groovy
plugins {
    id 'dev.uebelacker.babeli' version '1.0-SNAPSHOT'
}
```

## Configuration

Configure the plugin using the `babeli` extension block:

### Per-language translation files (properties or XML)

```groovy
babeli {
    actions = ['sort', 'missing']
    translationFile 'en', file('src/main/resources/messages_en.properties')
    translationFile 'de', file('src/main/resources/messages_de.properties')
    translationFile 'fr', file('src/main/resources/messages_fr.properties')
}
```

### Multi-language JSON file

```groovy
babeli {
    actions = ['sort', 'missing']
    multiLanguageFile = file('src/main/resources/translations.json')
}
```

### XML (Android-style) translation files

```groovy
babeli {
    actions = ['sort']
    translationFile 'en', file('src/main/res/values/strings.xml')
    translationFile 'de', file('src/main/res/values-de/strings.xml')
}
```

## Tasks

| Task | Description |
|------|-------------|
| `babeliValidate` | Validates translation files against the configured actions. Fails the build if errors are found. |
| `babeliUpdate` | Updates translation files by applying the configured actions (e.g., sorting keys, completing missing translations). |

```bash
# Validate translation files
./gradlew babeliValidate

# Update translation files
./gradlew babeliUpdate
```

## Actions

| Action | Description |
|--------|-------------|
| `sort` | Ensures translation keys are sorted alphabetically. |
| `missing` | Detects missing translation keys across language files. |
| `complete` | Uses AI to automatically generate missing translations (requires AI configuration). |

## Options

| Property | Type | Description |
|----------|------|-------------|
| `actions` | `List<String>` | Actions to apply (e.g., `['sort', 'missing']`). Defaults to all registered actions. |
| `translationFile` | method | Registers a per-language translation file: `translationFile 'en', file('path')` |
| `multiLanguageFile` | `File` | Path to a single multi-language JSON file. |
| `baseLanguage` | `String` | The base/reference language (default: `en`). |
| `workingDirectory` | `File` | Working directory for file resolution. |

### AI configuration (for `complete` action)

| Property | Type | Description |
|----------|------|-------------|
| `modelProvider` | `String` | AI model provider (e.g., `ollama`). |
| `model` | `String` | Model name to use for translations. |
| `apiKey` | `String` | API key for the model provider. |
| `apiUrl` | `String` | API endpoint URL. |

Example with AI completion:

```groovy
babeli {
    actions = ['sort', 'missing', 'complete']
    baseLanguage = 'en'
    translationFile 'en', file('messages_en.properties')
    translationFile 'de', file('messages_de.properties')
    modelProvider = 'ollama'
    model = 'llama3'
    apiUrl = 'http://localhost:11434'
}
```

## CI Integration

Use `babeliValidate` in your CI pipeline to ensure translation files are properly maintained:

```bash
./gradlew babeliValidate
```

The task will fail the build if any configured action detects issues (unsorted keys, missing translations, etc.).
