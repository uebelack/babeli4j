# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Full build (clean, compile, test, install)
mvn clean install

# Run all tests
mvn test

# Run a single test class
mvn test -pl babeli-core -Dtest=SortActionTest

# Run a single test method
mvn test -pl babeli-core -Dtest=SortActionTest#name

# Check code formatting (Spotless with Google Java Format)
mvn spotless:check

# Apply code formatting
mvn spotless:apply
```

## Architecture

Babeli4j is a translation file management system — a multi-module Maven project (Java 21).

### Modules

- **babeli-core**: Core library for reading, validating, transforming, and writing translation files (properties and JSON formats)
- **babeli-ai**: AI-powered translation completion using LangChain4j/Ollama; depends on babeli-core

### Key Patterns

**Action system**: The central extensibility mechanism. Actions implement `Action` interface with `name()`, `validate()`, and `update()` methods. They self-register via static initializer blocks into `ActionRegistry` (a static HashMap). Current actions: `SortAction`, `MissingAction`, `GlossaryAction`, `CompleteAction` (AI).

**Reader/Writer interfaces**: `FileReader` reads files by extension into `SingleLanguageTranslationFile`. `FileWriter` writes both single-language and multi-language formats. Implementations: `PropertiesFileReader`, `PropertiesFileWriter`, `JsonFileWriter`.

**Data model**: All model classes are Java records (immutable): `Translation`, `SingleLanguageTranslationFile`, `MultiLanguageTranslationFile`, `GlossaryEntry`, `GlossaryFile`, `Error`.

### Package Structure

All code lives under `dev.uebelacker.babeli.core` (even the AI module uses this base package with `.ai.actions` suffix).

## Conventions

- Java records for all data/model classes
- AssertJ for test assertions, JUnit 5 for test framework
- Test fixtures centralized in `Fixtures` class and `ConfigurationFactory`
- Stop-word files for glossary filtering are in `babeli-core/src/main/resources/stop-words/`
