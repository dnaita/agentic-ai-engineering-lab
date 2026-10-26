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

- Python 3.12 or newer
- Git
- A terminal and editor
- No model API key is required for Lab 0

## Local setup

Clone the repository:

```bash
git clone https://github.com/dnaita/agentic-ai-engineering-lab.git
cd agentic-ai-engineering-lab
git switch lab-0-development-baseline
python3 -m venv .venv
source .venv/bin/activate
python -m pip install --upgrade pip
python -m pip install -e ".[dev]"
```

On Windows PowerShell, activate the environment with `.venv\Scripts\Activate.ps1`.

## Run the quality checks

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
