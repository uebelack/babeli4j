# Babeli4j

[![CI](https://github.com/uebelack/babeli4j/actions/workflows/ci.yml/badge.svg)](https://github.com/uebelack/babeli4j/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=uebelack_babeli4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=uebelack_babeli4j)

<p align="center">
  <img src="https://github.com/uebelack/babeli/blob/main/babeli.png?raw=true" style="height: 150px;" alt="Babeli"/>
</p>

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
    <version>1.1.0</version>
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
            <version>1.1.0</version>
        </dependency>
    </dependencies>
</plugin>
```

```bash
export ANTHROPIC_API_KEY=sk-...

mvn babeli:validate   # check for issues
mvn babeli:update     # sort keys and generate missing translations

# add a new key, missing languages are generated
mvn babeli:add -Dbabeli.key=common.button.ok -Dbabeli.translations=en=OK,de=Okay
```

### Gradle

```groovy
plugins {
    id 'dev.uebelacker.babeli' version '1.1.0'
}

dependencies {
    babeli 'dev.uebelacker.babeli:babeli-anthropic:1.1.0'
}
```

```bash
export ANTHROPIC_API_KEY=sk-...

./gradlew babeliValidate   # check for issues
./gradlew babeliUpdate     # sort keys and generate missing translations

# add a new key, missing languages are generated
./gradlew babeliAdd --key=common.button.ok --translation=en=OK --translation=de=Okay
```

If the project contains several resource bundles, pick one with `-Dbabeli.bundle=<name>`
(Maven) or `--bundle=<name>` (Gradle). The plugins take the key and its translations as
parameters; only the CLI `babeli add` prompts for them interactively. Values may contain commas
(`-Dbabeli.translations="en=Yes, please,de=Ja, bitte"`): a comma only starts a new translation when a
language code and `=` follow it. Adding a key that already exists, or a translation for a language the
bundle doesn't have, fails without changing any file.

### CLI

```bash
babeli validate -p anthropic
babeli update -p anthropic
babeli add -p anthropic
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

Default model: `claude-sonnet-5-5`. Override with `BABELI_MODEL`.

### Ollama

Uses a local Ollama instance at `http://localhost:11434` by default.

```bash
export BABELI_API_URL=http://my-server:11434   # optional
```

Default model: `qwen3.6`. Override with `BABELI_MODEL`.

### Copilot CLI

Uses the [GitHub Copilot CLI](https://github.com/github/copilot-cli) installed on your machine
(`copilot -p <prompt>`), so no API key is needed. Select it with `-p copilot-cli` or
`BABELI_MODEL_PROVIDER=copilot-cli`.

```bash
export BABELI_COPILOT_PATH=/opt/homebrew/bin/copilot   # optional, defaults to `copilot` on the PATH
```

## Modules

| Module                 | Description                                                                     |
|------------------------|---------------------------------------------------------------------------------|
| `babeli-core`          | Core library — reading, validating, transforming, and writing translation files |
| `babeli-anthropic`     | Anthropic model provider                                                        |
| `babeli-ollama`        | Ollama model provider                                                           |
| `babeli-copilot-cli`   | GitHub Copilot CLI model provider                                               |
| `babeli-cli`           | Standalone command-line tool                                                    |
| `babeli-maven-plugin`  | Maven build plugin ([docs](babeli-maven-plugin/README.md))                      |
| `babeli-gradle-plugin` | Gradle build plugin ([docs](babeli-gradle-plugin/README.md))                    |

## Environment Variables

| Variable                   | Description                                                    |
|----------------------------|----------------------------------------------------------------|
| `ANTHROPIC_API_KEY`        | Anthropic API key                                              |
| `BABELI_ANTHROPIC_API_KEY` | Anthropic API key (takes priority)                             |
| `BABELI_MODEL`             | Override model name                                            |
| `BABELI_API_URL`           | API endpoint                                                   |
| `BABELI_MODEL_PROVIDER`    | Override model provider (`anthropic`, `ollama`, `copilot-cli`) |
| `BABELI_COPILOT_PATH`      | Path to the `copilot` executable (default: `copilot`)          |
| `BABELI_BASE_LANGUAGE`     | Override base language (default: `en`)                         |
| `BABELI_SKIP`              | When set babeli execution is skipped                           |

## Building from Source

Requires Java 21+ (releases are built with Java 25).

```bash
./mvnw clean install
```

`install` (not just `verify`) because the reactor also builds the Gradle plugin, which resolves
`babeli-core` from the local Maven repository.

## Releasing

Releases are built by GitHub Actions ([`release.yml`](.github/workflows/release.yml)) when a `v*` tag
is pushed:

1. Set the version and update the changelog:
   ```bash
   ./mvnw versions:set -DnewVersion=1.2.3 -DprocessAllModules -DgenerateBackupPoms=false
   git-cliff --tag v1.2.3 -o CHANGELOG.md
   ```
   Commit, then `git tag v1.2.3 && git push origin main v1.2.3`.
2. The workflow checks the tag against the project version, runs all tests, uploads the signed
   artifacts to the [Central Portal](https://central.sonatype.com) and creates the GitHub release.
3. Check the deployment on central.sonatype.com → *Publish* → Deployments and click **Publish**.
4. As soon as `babeli-core` is available on Maven Central, the workflow publishes the Gradle plugin
   to the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/dev.uebelacker.babeli).
5. Move to the next snapshot version with `./mvnw versions:set -DnewVersion=1.2.4-SNAPSHOT ...`.

Required repository secrets: `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_PASSWORD` (Central Portal user
token), `MAVEN_GPG_PRIVATE_KEY`, `MAVEN_GPG_PASSPHRASE`, `GRADLE_PUBLISH_KEY`, `GRADLE_PUBLISH_SECRET`.

## License

MIT
