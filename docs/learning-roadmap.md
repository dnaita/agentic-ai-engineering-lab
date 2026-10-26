# Agentic AI Engineering Lab — Java-First Learning Roadmap

## Goal

Build a production-minded, provider-independent portfolio of agentic AI systems using **Java 25, Spring Boot, Maven, and VS Code** as the main implementation environment. Python remains an optional supporting language for comparing Python-first AI libraries and understanding examples from courses.

The final capstone is an Agentic SDLC assistant that analyses requirements, proposes changes, runs checks in a controlled workspace, and requests human approval before consequential operations.

## Technology choices

- **Primary language:** Java 25
- **Application foundation:** Spring Boot 4.x
- **AI integration:** Spring AI, introduced after provider-independent fundamentals
- **Build and dependency management:** Maven
- **Testing:** JUnit Jupiter, Spring Boot Test, and Jakarta Bean Validation tests
- **Local IDE and tools:** macOS, Homebrew, Visual Studio Code
- **CI:** GitHub Actions
- **Provider options:** OpenAI, Anthropic, and local/open-weight models when practical
- **Supporting frameworks for comparison:** LangChain4j, framework-independent Java implementations, and selected Python-first tools

Choose currently supported compatible versions when each framework is introduced. Do not add a framework unless a baseline implementation or learning objective gives us a reason to compare it.

## Engineering principles

- Prefer the simplest architecture that meets the requirement; do not add agents without evidence they help.
- Separate business logic from provider-specific API calls.
- Use explicit data contracts and validate untrusted model outputs.
- Keep secrets out of source control.
- Make core tests deterministic and runnable without paid API access.
- Track quality, latency, token usage/cost, retries, and failures.
- Require human approval for consequential or irreversible actions.
- Document design decisions, threat models, and known limitations.
- Work on small feature branches and use reviewable pull requests.

## Learning milestones

### 0 — Java development baseline
- Configure JDK 25, Maven, and VS Code on macOS.
- Understand Maven project structure and the role of `pom.xml`.
- Build a minimal Spring Boot application.
- Add GitHub Actions checks for Java build/tests while keeping the original Python baseline as an optional reference check.

**Exit criteria:** a clean checkout can run `mvn -B verify` inside `java/` and CI verifies the Java build.

### 1 — Data contracts and validation
- Learn Java records, generic collections, annotations, and constructors.
- Use Jakarta Bean Validation to define input and output constraints.
- Understand that constraints are enforced when a validator is invoked, not merely because an annotated object exists.
- Write tests for valid data, missing values, blank text, bounds, and edge cases.

**Deliverable:** a tested `TaskPlan` contract with explicit constraints.

### 2 — LLM application fundamentals without a live provider
- Define a provider-neutral model interface.
- Use typed request/response contracts.
- Implement a deterministic fake provider for tests.
- Add configuration, timeouts, bounded retries, error categories, and execution metadata.

**Deliverable:** a small LLM application core that can be tested without API keys or paid model calls.

### 3 — OpenAI and Anthropic integrations
- Connect to each provider behind the same application interface.
- Use environment-based secrets and explicit model configuration.
- Handle structured outputs, timeouts, rate limits, retries, and malformed responses.
- Compare behaviour, portability, latency, and cost on a common test set.

**Deliverable:** provider adapters and a repeatable comparison report. Live integration tests run only when credentials are configured.

### 4 — Tool-using agent from first principles
- Define tools with explicit input/output schemas.
- Implement the model decision loop and bounded execution.
- Validate tool arguments, restrict available operations, and record tool results.
- Test failures, malformed arguments, loops, timeouts, and iteration limits.

**Deliverable:** a deterministic tool-using agent with negative and edge-case tests.

### 5 — Retrieval, embeddings, and memory
- Document ingestion and chunking.
- Embeddings, retrieval, source attribution, and retrieval quality evaluation.
- Separate conversation state from durable memory.
- Enforce data isolation and authorization boundaries.

**Deliverable:** a document research workflow with measurable retrieval quality and source references.

### 6 — Spring AI and Java framework comparisons
- Reimplement a known use case with Spring AI.
- Compare a direct provider integration with Spring AI and LangChain4j.
- Examine structured output, tool calling, chat memory, and observability abstractions.
- Compare maintainability, testing, portability, and operational complexity.

**Deliverable:** a short architecture decision record backed by a working example and tests.

### 7 — MCP, stateful workflows, and multi-agent coordination
- Build an MCP client and server integration.
- Implement stateful workflows, persistence, conditional transitions, and human approval.
- Compare a single-agent baseline with delegated or multi-agent designs.
- Handle timeouts, partial results, conflicting responses, and unsafe tool outputs.

**Deliverable:** an MCP-enabled workflow with tests and a benchmark showing whether multiple agents add measurable value.

### 8 — Evaluation, security, and observability
- Golden test datasets, task-success metrics, regression tests, and adversarial examples.
- Prompt-injection and unsafe-tool-use testing.
- Structured logs and traces, latency, token usage, and cost measurement.
- Least privilege, secrets handling, human approval, and audit records.

**Deliverable:** a repeatable evaluation report and documented threat model.

### 9 — Agentic SDLC capstone
Build a controlled software-engineering assistant that can:
1. Analyse a requirement and identify ambiguities.
2. Propose architecture, acceptance criteria, and implementation tasks.
3. Suggest code changes in a disposable workspace or feature branch.
4. Run unit tests and static analysis.
5. Summarize diffs, risks, and failed checks.
6. Require explicit approval before consequential Git or deployment actions.
7. Record traceable executions and evaluation results.

**Deliverable:** documented application with CI, evaluation results, security boundaries, cost/latency observations, and a reproducible demo.

## Optional Python track

Learn only enough Python to read AI tutorials, inspect Python-first frameworks, and reproduce selected experiments. The Python baseline already in the repository is retained as reference material. We will compare a Python implementation with its Java counterpart only where doing so illuminates a framework or ecosystem difference.

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
1. Define the problem, acceptance criteria, and a short design.
2. Implement on a feature branch.
3. Add positive, negative, and edge-case tests.
4. Run local checks and CI.
5. Document results and unresolved issues.
6. Review changes before merging to `main`.
