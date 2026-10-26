"""Smoke tests for the project setup."""

from agentic_ai_engineering_lab import __version__


def test_package_version_is_defined() -> None:
    """The package should expose a version string."""
    assert __version__ == "0.1.0"
