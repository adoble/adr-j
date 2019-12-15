# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and from version 4.0.0 this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Simple config command added
- ISO date formats can be set using the dateFormat property in adr.properties
- Add edit sub command
- Add author field substitution
- Add version sub-command (currently gives version as 3.1.0).
- Picocli framework now as [external dependency](https://picocli.info/#_add_as_external_dependency)
- Add enablement for future versions to use [GraalVM native images](https://www.graalvm.org/docs/reference-manual/aot-compilation/)
- Add `CHANGELOG.md` following [keep a changelog](https://keepachangelog.com/en/1.0.0/).

## [3.0] - 2019-06-29

### Added

- Users can specify their own templates [#6](https://github.com/adoble/adr-j/issues/6)
- Support of arbitrary languages for markdown files (e.g. [AsciiDoc](http://asciidoc.org/))

## [2.1] - 2019-06-04

### Changed

- Switch to [picocli](https://picocli.info/) for command line parsing. See also [ADR-0006](https://github.com/adoble/adr-j/blob/master/doc/adr/0006-use-command-line-processing-package.md).

## [2.0] - 2019-03-17

### Added

- Add support for unix (launch script, terminal support)

## [1.0] - 2019-02-12

Initial release

[Unreleased]: https://github.com/adoble/adr-j/compare/v3.0...master
[3.0]: https://github.com/adoble/adr-j/compare/v2.1...v3.0
[2.1]: https://github.com/adoble/adr-j/compare/v2.0...v2.1
[2.0]: https://github.com/adoble/adr-j/compare/v1.0...v2.0
[1.0]: https://github.com/adoble/adr-j/releases/tag/v1.0
