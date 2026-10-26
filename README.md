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

Install Python and Git with Homebrew. If VS Code is not already installed, you can install it with Homebrew too. Skip any package you already have installed.

```bash
brew update
# Refresh Homebrew's package definitions before installing or upgrading tools.

brew install python@3.13 git
# Install Python 3.13 and Git. Skip this if both are already installed and set up.

brew install --cask visual-studio-code
# Install Visual Studio Code. Skip this if VS Code is already installed.
```

Clone the repository and switch to the Lab 0 branch:

```bash
git clone https://github.com/dnaita/agentic-ai-engineering-lab.git
# Download the repository to a new local folder and configure its Git remote.

cd agentic-ai-engineering-lab
# Move into the repository directory; run the remaining project commands from here.

git switch lab-0-development-baseline
# Check out the Lab 0 working branch so you do not make these changes directly on main.
```

Create and activate a virtual environment using the Python installed by Homebrew. The `brew --prefix` command finds the correct Homebrew installation path on both Apple Silicon and Intel Macs.

```bash
"$(brew --prefix python@3.13)/bin/python3.13" --version
# Confirm that the expected Homebrew-managed Python 3.13 executable is available.

"$(brew --prefix python@3.13)/bin/python3.13" -m venv .venv
# Create an isolated Python environment in the repository's .venv folder.

source .venv/bin/activate
# Activate that environment in this terminal so Python packages install locally to this project.

python -m pip install --upgrade pip
# Upgrade pip inside the virtual environment, not Homebrew's global Python.

python -m pip install -e ".[dev]"
# Install this project in editable mode plus development tools (pytest, Ruff, and MyPy).
```

Open the project in VS Code:

```bash
code .
# Open the current repository folder in Visual Studio Code.
```

If the `code` command is not available, open VS Code and choose **File → Open Folder...**, then select the repository folder. You can enable the command from VS Code's Command Palette by searching for **Shell Command: Install 'code' command in PATH** if that command is offered by your installation.

### Configure VS Code

1. Install Microsoft's **Python** extension (publisher: Microsoft) from the Extensions panel. It provides Python interpreter selection, test discovery, and debugging support.
2. Open the Command Palette with `Cmd+Shift+P`, run **Python: Select Interpreter**, and select `.venv/bin/python` inside this repository. This tells VS Code to use the project's isolated environment.
3. Open **Terminal → New Terminal**. Activate the virtual environment if it is not active already:

   ```bash
   source .venv/bin/activate
   # Activate the project's virtual environment in the VS Code terminal.
   ```

4. Run the commands in this README from the VS Code integrated terminal. The project tools live in `.venv`, not in Homebrew's global Python environment.

Homebrew manages system-level tools such as Python, Git, and VS Code. Use pip **inside the virtual environment** for this project's Python dependencies.

## Run the quality checks

Run these commands from the repository root with `.venv` activated, either in Terminal or the VS Code integrated terminal:

```bash
ruff check .
# Find lint issues such as common bugs, unused imports, and style violations.

ruff format --check .
# Check whether Python files match Ruff's formatter without changing any files.

mypy
# Run static type checking to catch type inconsistencies before execution.

pytest
# Run the automated test suite and report any failures.
```

## Configuration and secrets

```bash
cp .env.example .env
# Copy the safe placeholder template to a local .env file for future provider labs.
```

Keep actual credentials in local environment variables or a supported secrets manager. Never commit real API keys. The `.env` file is ignored by Git; `.env.example` should contain placeholder values only. Lab 0 does not require or load credentials.

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
