# Changelog

## [1.1.0] - 2026-11-13

### Features

- feat(add-command): expose add in the maven and gradle plugins
- feat(add-command): allow graceful exit from add command
- feat: add new command "add" to add new translation keys

### Bug fixes

- fix(maven-plugin): declare the Maven API as provided
- fix(cli): enable native access for jline in the CLI jar
- fix(anthropic): default to claude-sonnet-5-5
- fix(translation): strip whitespace around the model's answer
- fix(add-command): refuse existing keys and unknown languages
- fix(properties): keep comments in place when sorting keys
- fix: address SonarCloud findings and cover new code with tests
- fix(tests): create target/test/properties before listing it in setUp
- fix(copilot-cli): align parent POM version with 1.0.2-SNAPSHOT

### Refactoring

- refactor: renamed ChatModel to Ai
- refac(add-command): moved main logic to Babeli core

### Dependencies

- build(deps): depend on langchain4j-core instead of langchain4j in babeli-core
- build(deps): update the Gradle plugin build to Gradle 9.9.1 and JUnit 6
- build(deps): update jline to 4.4.7
- build(deps): update langchain4j to 1.23.0 via its BOM
- build(deps): update dependencies and plugins (patch/minor)
- build(deps-dev): bump org.jacoco:jacoco-maven-plugin (#26)
- build(deps): bump actions/checkout from 6 to 7 (#36)
- build(deps): bump actions/cache from 5 to 6 (#39)
- build(deps): bump org.junit.jupiter:junit-jupiter-api (#40)
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin (#42)
- build(deps): bump dev.langchain4j:langchain4j-anthropic (#44)
- build(deps): bump dev.langchain4j:langchain4j from 1.14.1 to 1.17.2 (#45)
- build(deps-dev): bump org.junit.jupiter:junit-jupiter (#41)
- build(deps-dev): bump org.sonatype.central:central-publishing-maven-plugin (#38)
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin (#33)
- build(deps): bump dev.langchain4j:langchain4j-ollama (#32)
- build(deps): bump dev.langchain4j:langchain4j-anthropic (#35)
- build(deps): bump org.apache.maven.plugins:maven-javadoc-plugin (#34)
- build(deps): bump gradle/actions from 6.1.0 to 6.2.0 (#31)
- build(deps-dev): bump org.apache.maven.plugins:maven-surefire-plugin (#30)
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin (#29)
- build(deps-dev): bump org.sonatype.central:central-publishing-maven-plugin (#27)
- build(deps): bump org.apache.maven:maven-plugin-api (#21)
- build(deps-dev): bump org.apache.maven.plugins:maven-gpg-plugin (#23)
- build(deps): bump org.apache.maven.plugins:maven-source-plugin (#22)
- build(deps): bump org.apache.maven:maven-core from 3.9.15 to 3.9.16 (#24)
- build(deps-dev): bump org.junit.jupiter:junit-jupiter (#25)

### Documentation

- docs: describe the tag-based release and build with the Maven Wrapper
- docs: document the add goal/task and the Copilot CLI provider

### Tests

- test: enforce a coverage gate and cover the CLI options and error paths
- test: load Mockito as a Java agent instead of self-attaching
- test(add-command): cover comment handling when adding to a properties file
- test(add-command): cover the interactive add command
- test: address SonarCloud findings in the new tests
- test: cover remaining new code in Babeli and AddService

### CI

- ci: release from v* tags to Maven Central and the Gradle Plugin Portal
- ci: remove Dependabot
- ci: test on Java 21 and 25, build with the Maven Wrapper

### Style

- style: update Spotless to 3.11.0 and reformat with its google-java-format
- style: let Spotless remove unused imports
- 🎨 apply code formatting with 2-space indentation

### Chores

- build: compile with --release 21 instead of source/target
- build: add Maven Wrapper and manage shared versions in the parent
- 🔧 update dependency version and format POM file
- chore: prepare snapshot 1.0.2

### Other

- Add Copilot CLI chat model provider
- Revert "refactor: renamed ChatModel to Ai"

## [1.0.1] - 2026-05-28

### Refactoring

- refactor: migrate properties handling to Apron library
- refactor: standardize environment variable names

### Chores

- chore: bump version to 1.0.1-SNAPSHOT and update documentation

## [1.0.0] - 2026-05-21

### Features

- ✨ add charset configuration option
- ✨ add Maven plugin for AI-powered translation management
- ✨ add XML file reader and writer support
- ✨ add configurable AI model settings
- feat(cli): created first version of babeli cli
- feat(cli): created first version of babeli cli
- ✨ add auto-configuration for Android and Java properties projects
- ✨ add Anthropic chat model provider
- ✨ add Ollama model provider integration
- ✨ add XML file reader and writer support
- ✨ implement single language JSON file reading
- feat: add JSON file reader with multi-language support

### Refactoring

- refac: renamed disabled flag to skip
- 🔨 introduce logging abstraction layer
- 🔨 optimize TranslationService instantiation in MissingAction
- 🔨 add glossary model and configuration support to GlossaryService
- refactor: remove ModelService and add test coverage
- 🔨 rename ModelProvider to ChatModelProvider and extract factory
- refactor: replace test service interfaces with Mockito mocks
- refactor: introduce BabeliContext and remove babeli-ai module
- 🔨 implement registry pattern and enhance core functionality
- refactor: convert Configuration to mutable class with defaults
- refactor: improve Action interface and add test infrastructure
- 🔨 restructure action and service registry architecture
- 🔨 introduce translation service architecture and unified model
- 🔨 remove extension method and improve file writer structure

### Dependencies

- build(deps-dev): bump commons-io:commons-io from 2.15.1 to 2.22.0 (#13)
- build(deps): bump dev.langchain4j:langchain4j-anthropic (#17)
- build(deps): bump org.junit.jupiter:junit-jupiter-api (#20)
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin (#19)
- build(deps): bump dev.langchain4j:langchain4j-ollama (#18)
- build(deps): bump dev.langchain4j:langchain4j from 1.13.0 to 1.14.1 (#15)
- build(deps-dev): bump org.apache.maven.plugins:maven-surefire-plugin (#14)
- build(deps): bump gradle/actions from 4.4.4 to 6.1.0 (#4)
- build(deps-dev): bump org.apache.maven.plugins:maven-shade-plugin (#6)
- build(deps): bump actions/checkout from 4 to 6 (#5)
- build(deps): bump actions/setup-java from 4 to 5 (#3)
- build(deps): bump com.google.code.gson:gson from 2.13.2 to 2.14.0 (#10)
- build(deps): bump actions/cache from 4 to 5
- build(deps-dev): bump org.junit.jupiter:junit-jupiter
- build(deps): bump dev.langchain4j:langchain4j-anthropic
- build(deps): bump dev.langchain4j:langchain4j-ollama

### Documentation

- 📝 add project documentation and license

### Tests

- 🧪 add skip functionality tests and improve logging coverage
- 🧪 suppress SonarQube warnings and remove unused imports
- test
- 🧪 add exception handling tests for PropertiesFileWriter
- 🧪 add test infrastructure and CompleteAction test

### Chores

- build: prepare project for Maven Central publication
- 🔧 add SCM configuration and update JUnit version
- 🔧 update version to 1.0.0-SNAPSHOT
- 🔧 add dependabot configuration for automated dependency updates
- chore: upgrade JDK to 25 and improve error messaging
- build: replace Coveralls with SonarCloud for code analysis

### Other

- 🛠️ add central publishing maven plugin
- 🛠️ add Dependabot auto-merge workflow
- Based on the changes, I can see this is a significant refactoring that includes:
- To refactor the Maven project structure from  to  and add a new module , the following steps and considerations are essential to ensure a smooth transition and maintain project integrity:
- first commit

