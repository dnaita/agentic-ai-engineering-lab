# Agentic AI Engineering Lab

A hands-on, provider-independent learning lab for building reliable, secure, evaluated agentic AI systems.

The goal is to learn by implementing small systems, testing their failure modes, comparing frameworks, and gradually building an enterprise-grade Agentic SDLC assistant. The roadmap lives in [docs/learning-roadmap.md](docs/learning-roadmap.md).

## Learning principles

- Build a simple baseline before adopting a framework.
- Make core tests deterministic and runnable without paid model API access.
- Keep provider-specific code behind clear interfaces where practical.
- Treat model output and tool results as untrusted input.
- Measure task quality, latency, cost, and failure rate.
- Require human approval for consequential actions.
- Record design decisions and known limitations.

## Prerequisites

- macOS
- Homebrew
- Python 3.12 or newer
- Git
- Visual Studio Code
- No model API key is required for Lab 0

## Local setup (macOS + Homebrew + VS Code)

Install Python and Git with Homebrew. If VS Code is not already installed, you can install it with Homebrew too. Skip anything you already have.

```bash
brew update
brew install python@3.13 git
brew install --cask visual-studio-code
```

Clone the repository and switch to the Lab 0 branch:

```bash
git clone https://github.com/dnaita/agentic-ai-engineering-lab.git
cd agentic-ai-engineering-lab
git switch lab-0-development-baseline
```

Create and activate a virtual environment using the Python installed by Homebrew. Using `brew --prefix` makes the Python path work on both Apple Silicon and Intel Macs:

```bash
"$(brew --prefix python@3.13)/bin/python3.13" --version
"$(brew --prefix python@3.13)/bin/python3.13" -m venv .venv
source .venv/bin/activate
python -m pip install --upgrade pip
python -m pip install -e ".[dev]"
```

Open the project in VS Code:

```bash
code .
```

If the `code` command is not available, open VS Code and choose **File → Open Folder...**, then select the repository folder.

### Configure VS Code

1. Install Microsoft's **Python** extension (publisher: Microsoft) from the Extensions panel.
2. Open the Command Palette with `Cmd+Shift+P`, run **Python: Select Interpreter**, and select the interpreter at `.venv/bin/python` inside this repository.
3. Open **Terminal → New Terminal**. Activate the virtual environment if it is not active already:

   ```bash
   source .venv/bin/activate
   ```

4. Use the integrated terminal for the commands in this README. The project tools are installed in `.venv`, not globally through Homebrew.

Homebrew manages system-level tools such as Python, Git, and VS Code. Install this project's Python dependencies with pip **inside the virtual environment**, not into Homebrew's global Python environment.

## Run the quality checks

Run these commands from the repository root with `.venv` activated, either in Terminal or the VS Code integrated terminal:

```bash
ruff check .
ruff format --check .
mypy
pytest
```

## Configuration and secrets

`cp .env.example .env` creates a local starting point for later provider labs. Keep actual credentials in local environment variables or a supported secrets manager. Never commit real API keys. The current Lab 0 tests do not load or require `.env`.

## Repository map

- `docs/learning-roadmap.md` — milestones, deliverables, and assessment rubric
- `src/agentic_ai_engineering_lab/` — package source
- `tests/` — automated tests
- `.github/workflows/ci.yml` — pull request and push quality checks
- `.env.example` — placeholder-only environment template

## Milestones

- [ ] Lab 0: reproducible development baseline and CI
- [ ] Lab 1: LLM fundamentals and provider adapter
- [ ] Lab 2: tool-using agent from first principles
- [ ] Lab 3: retrieval and memory
- [ ] Lab 4: stateful workflows and framework comparison
- [ ] Lab 5: MCP tools and integrations
- [ ] Lab 6: multi-agent coordination benchmark
- [ ] Lab 7: evaluation, safety, and observability
- [ ] Lab 8: Agentic SDLC capstone

See the [full learning roadmap](docs/learning-roadmap.md) for exit criteria.
