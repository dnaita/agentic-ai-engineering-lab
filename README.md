# Agentic AI Engineering Lab

A hands-on, production-minded learning portfolio for agentic AI engineering.

**Primary track: Java 25 + Spring Boot + Spring AI.** Python remains an optional comparison track for reading AI tutorials and exploring Python-first libraries. We will focus on concepts that transfer across languages: tool use, structured data, workflows, memory, RAG, MCP, evaluation, observability, security, and controlled execution.

The full curriculum is in [docs/learning-roadmap.md](docs/learning-roadmap.md).

## Engineering principles

- Build a tested baseline before introducing an agent framework.
- Keep model-provider integrations replaceable where practical.
- Treat model output and tool results as untrusted input.
- Use deterministic tests that work without paid API calls.
- Measure task quality, latency, cost, retries, and failure rates.
- Require explicit human approval for consequential operations.
- Document trade-offs, threats, limitations, and evaluation results.

## Prerequisites

- macOS with Homebrew
- Java 25 (this project targets Java 25)
- Apache Maven
- Git
- Visual Studio Code

Lab 0 does not require an AI provider key.

## 1. Install system tools with Homebrew

Run only the installs you need. Homebrew manages system tools; Maven manages Java project dependencies.

```bash
brew update
# Refresh Homebrew's package metadata.

brew install openjdk@25 maven
# Install JDK 25 and Apache Maven. Skip packages already installed.

brew install --cask visual-studio-code
# Install VS Code if it is not already installed.
```

Homebrew may not automatically link a versioned JDK into the system Java path. Configure the current terminal to use the Homebrew JDK:

```bash
export JAVA_HOME="$(brew --prefix openjdk@25)/libexec/openjdk.jdk/Contents/Home"
# Set JAVA_HOME to the JDK installed by Homebrew.

export PATH="$JAVA_HOME/bin:$PATH"
# Prefer this JDK's java and javac executables in this terminal.

java -version
# Confirm the active Java runtime version.

mvn -version
# Confirm Maven is installed and see which Java runtime Maven uses.
```

These exports apply only to the current terminal. To make them persistent in the default macOS zsh shell, add both export lines to `~/.zshrc`, then open a new terminal. If you already manage Java versions with another tool, keep your existing setup and ensure `java -version` and `mvn -version` both report Java 25.

## 2. Get the code

### First-time clone

```bash
git clone https://github.com/dnaita/agentic-ai-engineering-lab.git
# Download the repository and configure its origin remote.

cd agentic-ai-engineering-lab
# Enter the repository directory.
```

### Existing local clone

```bash
git fetch origin
# Download the latest branch and commit references from GitHub.

git switch --track origin/java-primary-track
# Create a local branch tracking the Java-track pull request branch.
```

If you already have a local `java-primary-track` branch, use `git switch java-primary-track` and then `git pull --ff-only`.

## 3. Open the project in VS Code

```bash
code .
# Open the current repository folder in Visual Studio Code.
```

If `code` is unavailable, open VS Code and choose **File → Open Folder...**. To install the command-line launcher, open the Command Palette (`Cmd+Shift+P`) and look for **Shell Command: Install 'code' command in PATH**.

The repository includes recommended extensions in `.vscode/extensions.json`. When prompted, install the Java extensions. At minimum, use Microsoft's **Extension Pack for Java** for language support, test discovery, debugging, and Maven integration.

## 4. Build and test the Java project

From the repository root:

```bash
cd java
# Enter the Java Maven module; its pom.xml defines the Java dependencies and build.

mvn -B verify
# Compile the application, run unit tests, and verify the Maven build.

mvn spring-boot:run
# Start the Spring Boot application locally.
```

Stop the application with `Ctrl+C`. The starter application does not call an AI provider yet.

In VS Code, open the Command Palette (`Cmd+Shift+P`) and select **Java: Configure Java Runtime** if you need to check the JDK used by the Java extension. The project source and CI target Java 25.

## 5. Current Java project layout

- `java/pom.xml` — Spring Boot, Java version, and Maven dependencies
- `java/src/main/java/com/dnaita/agenticailab/` — application code
- `java/src/main/java/com/dnaita/agenticailab/contracts/TaskPlan.java` — first validated data contract
- `java/src/test/java/` — unit and validation tests
- `docs/learning-roadmap.md` — milestones and assessment rubric
- `.github/workflows/ci.yml` — Java and Python-reference CI checks
- `pyproject.toml`, `src/`, `tests/` — the original Python baseline, retained as optional reference material

## Milestones

- [ ] Lab 0: Java toolchain, Maven build, tests, and CI
- [ ] Lab 1: structured data contracts and validation
- [ ] Lab 2: provider interface and mock model
- [ ] Lab 3: OpenAI and Anthropic model integrations
- [ ] Lab 4: tool-using agent from first principles
- [ ] Lab 5: retrieval, embeddings, and memory
- [ ] Lab 6: Spring AI and framework comparisons
- [ ] Lab 7: MCP, stateful workflows, and multi-agent coordination
- [ ] Lab 8: evaluation, security, observability, and human approval
- [ ] Lab 9: production-minded Agentic SDLC capstone

Python is a supporting skill, not a prerequisite for the primary project. We will use it when a particular tutorial or tool provides a clear learning advantage.
