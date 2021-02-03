# 7. Use specific environment variables for editors

Date: Jan 29, 2021

## Status

Proposed


## Context

Users of the ADT tool might want to edit ADRs in the editor that's not the default system editor.

ADT should introduce an additional `ADR_EDITOR` and `ADR_VISUAL` variables, so users may choose editors for ADRs.

The enhancement proposal is in [project issues](https://github.com/adoble/adr-j/issues/4)

## Decision

We will read editor command from additional `ADR_EDITOR` and `ADR_VISUAL` variables.
If custom variables are not set, we will fall back to reading `EDITOR` and `VISUAL` variables.

We will extract editor command resolving code from ADR class and move it to dedicated class.
That will improve testability and make further modifications easier.

We will reflect adding new variables in the docs.

## Consequences

* The ADR tool behavior remains backward-compatible.
* Editor command resolving will move out of launcher class.
