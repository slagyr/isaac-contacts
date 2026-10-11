# 🍏 Isaac Contacts 📇

Contacts for [Isaac](https://github.com/slagyr/isaac): names the party behind
a comm handle, so the same person is one contact on every comm.

Depends on [isaac-foundation](https://github.com/slagyr/isaac-foundation) and
[isaac-agent](https://github.com/slagyr/isaac-agent).

[![Contacts](https://github.com/slagyr/isaac-contacts/actions/workflows/ci-tests.yml/badge.svg)](https://github.com/slagyr/isaac-contacts/actions/workflows/ci-tests.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Clojure](https://img.shields.io/badge/Clojure-1.11%2B-blue?logo=clojure)](https://clojure.org)
[![Babashka](https://img.shields.io/badge/Babashka-1.3%2B-red?logo=clojure)](https://babashka.org)
[![Java](https://img.shields.io/badge/Java-21%2B-orange?logo=openjdk)](https://openjdk.org/)

## Vocabulary

- **Party** — whoever is on the other end of a message, person or machine.
- **Handle** — how one comm names a party: `{:kind :handle :comm … :id …}`.
- **Contact** — a party known by name, with the handles that belong to them.
- **Identifier** — a module that names the party behind a handle. This module
  is one.

## What's here

- Module `isaac.contacts` (`isaac.contacts.module/create-module`). A skeleton;
  the contacts table and the identifier are tracked by isaac-o7tm.

## Development

```bash
bb spec       # speclj specs
bb features   # gherclj scenarios
bb ci         # lint + spec + features
```

Add `-A:dev-local` style overrides (see `deps.edn`) to test against sibling
checkouts of isaac-foundation and isaac-agent.
