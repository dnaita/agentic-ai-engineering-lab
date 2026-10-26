# Agentic AI Engineering Lab — Learning Roadmap

## Goal

Build a production-minded, provider-independent portfolio of agentic AI systems through hands-on implementation, testing, evaluation, and review.

## Engineering principles

- Prefer the simplest architecture that meets the requirement; do not add agents without evidence they help.
- Keep model-provider integrations behind explicit interfaces where practical.
- Keep secrets out of source control and use environment variables for credentials.
- Make behaviour testable with deterministic fakes and mocked model responses.
- Track quality, latency, token usage/cost, retries, and failures.
- Require human approval for consequential or irreversible actions.
- Document design decisions, threat models, and known limitations.
- Use small feature branches and reviewable pull requests.

## Learning milestones

### 0 — Repository and development baseline
- Document supported Python version and reproducible setup.
- Add package/dependency management, formatting, linting, typing, and unit-test configuration.
- Add CI checks that run without paid API credentials.
- Add safe environment configuration examples.

**Exit criteria:** a clean checkout can install, run checks, and execute tests using documented commands.

### 1 — LLM application fundamentals
- Structured outputs and schema validation.
- Provider interface and configuration.
- Timeouts, retries, error handling, and token/cost accounting.
- Tests using deterministic fake providers.

**Deliverable:** a small, tested LLM application that can be evaluated without a live API call.

### 2 — Tool-using agent from first principles
- Explicit tool schemas and input validation.
- Model decision loop, tool execution, and bounded iterations.
- Tool failure handling and execution records.
- Allow-listed tools and least privilege.

**Deliverable:** a tool-using agent with tests for success, malformed arguments, tool errors, and iteration limits.

### 3 — Retrieval and memory
- Document ingestion, chunking, retrieval, citations, and retrieval evaluation.
- Distinguish conversation state from durable memory.
- Access control and data isolation.

**Deliverable:** a document Q&A workflow with measurable retrieval quality and source references.

### 4 — Stateful workflows and frameworks
- Implement a use case directly, then compare a framework implementation.
- Graph state, conditional routing, persistence, interrupts, and resumability.
- Evaluate OpenAI Agents SDK, LangGraph, and Google ADK against a common use case rather than assuming one is best.

**Deliverable:** documented framework comparison with tests and trade-offs.

### 5 — MCP and external tools
- Build an MCP server and client.
- Validate tool inputs and outputs.
- Handle authentication, permissions, failures, and untrusted tool responses.

**Deliverable:** an MCP integration with automated protocol and security tests.

### 6 — Multi-agent coordination
- Compare a single-agent baseline against delegated or multi-agent workflows.
- Define clear agent responsibilities and contracts.
- Handle timeouts, partial results, loops, and conflicting outputs.

**Deliverable:** benchmark showing whether multi-agent coordination improves quality, latency, or cost.

### 7 — Evaluation, safety, and observability
- Golden datasets, regression tests, task-success metrics, and adversarial cases.
- Prompt-injection and unsafe-tool-use tests.
- Traces, structured logs, latency, token usage, and cost reporting.
- Human approval gates and audit records.

**Deliverable:** repeatable evaluation report and documented threat model.

### 8 — Agentic software development capstone
Build a controlled software-engineering assistant that can:
1. Analyse requirements and identify ambiguity.
2. Propose architecture and tasks.
3. Suggest code changes and tests in a sandbox/branch.
4. Run tests and static analysis.
5. Summarize diffs and risks.
6. Require explicit approval before consequential Git operations.
7. Record traceable executions and evaluation results.

**Deliverable:** documented, tested application with CI, evaluation results, security boundaries, and a demo.

## Initial tool choices

Start with Python, pytest, Pydantic, a provider adapter, and GitHub Actions. Use mocks for reliable tests and optional live-provider tests guarded by environment variables. Add a framework only when the initial implementation gives us a meaningful baseline.

Potential provider/framework tracks: OpenAI API and Agents SDK, Anthropic API and Claude Agent SDK, LangGraph, Google ADK, and selected local/open-weight models. Revisit current APIs and pricing before implementing provider-specific code.

## Assessment rubric

- Correctness, tests, reproducibility: 25%
- Architecture and reliability: 20%
- Evaluation quality and measurable results: 15%
- Security and human approval: 15%
- Observability, latency, and cost controls: 10%
- Documentation and design decisions: 10%
- Judgement about when not to use agents: 5%

## Working method

For every milestone:
1. Write acceptance criteria and a short design.
2. Implement on a feature branch.
3. Add positive, negative, and edge-case tests.
4. Run local checks and CI.
5. Document results and unresolved issues.
6. Review changes before merging to `main`.
