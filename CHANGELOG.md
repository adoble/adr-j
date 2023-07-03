# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and from version 4.0.0 this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased]

### Added
* CI build now done with GitHub actions

### Changed
* The default git and GitHub branch is now named `main`.
* Upgraded to use Java 17.
* Upgraded to use version 8.2 of Gradle.
* Removed deprecated Java methods.

### Fixed
* Fixed [issue 43](https://github.com/adoble/adr-j/issues/43) allowing a user to add other files and directories in the same directory as the ADRs (thanks [maxandersen](https://github.com/maxandersen) for pointing this out). The behaviour is now that files and directories that are not "well formed" ADRs are ignored.
* Fixed [issue #39](https://github.com/adoble/adr-j/issues/39) in handling reverse links with ASCCIDOC templates (thanks [Torsten Keiber](https://github.com/tkleiber))
* Fixed [issue #37](https://github.com/adoble/adr-j/issues/37)

### Documentation Fixes
* Fixed [issue #42](https://github.com/adoble/adr-j/issues/42). ADR 2 now superseded by ADR 8. 
* Fixed [issue #41](https://github.com/adoble/adr-j/issues/42). ADR 4 now superseded by ADR 9. Readme updated. 



## [3.2] - 2021-02-01

### Added

* No new functionality added. This release is to tidy up some loose ends after a pause in development.

### Changed
* Refactored date/time handling
* Now uses environment variables prefixed with "ADR_" ([iss #4](https://github.com/adoble/adr-j/issues/4)))  (thanks [Dymytro Kovalchuk](https://github.com/dimasmith))
* Removed dependency to JCenter ([iss #35](https://github.com/adoble/adr-j/issues/35))  (thanks [Marcel van den Brink](https://github.com/Leviter))




## [3.1] - 2019-12-30

### Added
- Simple manual pages in markdown format
- Command config command added together with subcommands docPath, templateFile, author and dateFormat ([iss #13](https://github.com/adoble/adr-j/issues/13))
- ISO date formats can be set using the dateFormat property in adr.properties ([iss #28](https://github.com/adoble/adr-j/issues/28)) (thanks [Sebastian Davids](https://github.com/sdavids))
- Add edit sub command ([iss #19](https://github.com/adoble/adr-j/issues/19))
- Add author field substitution ([iss #25](https://github.com/adoble/adr-j/issues/25)) (thanks [Sebastian Davids](https://github.com/sdavids))
- Add version sub-command (currently gives version as 3.1.0).
- Picocli framework now as [external dependency](https://picocli.info/#_add_as_external_dependency) (thanks [Oliver Kopp](https://github.com/koppor))
- Add enablement for future versions to use [GraalVM native images](https://www.graalvm.org/docs/reference-manual/aot-compilation/) ([iss #16](https://github.com/adoble/adr-j/issues/16)) (thanks [Oliver Kopp](https://github.com/koppor))
- Add `CHANGELOG.md` following [keep a changelog](https://keepachangelog.com/en/1.0.0/) (([iss #17](https://github.com/adoble/adr-j/issues/17)))  (thanks [Oliver Kopp](https://github.com/koppor)).

### Changed
- Now using [picocli](https://picocli.info/) 4.1.2
- Minor fixes (([iss #20](https://github.com/adoble/adr-j/issues/20))) (thanks [Oliver Kopp](https://github.com/koppor))
- Unit tests enabled in gradle build (([iss #29](https://github.com/adoble/adr-j/issues/29))) (thanks [Sebastian Davids](https://github.com/sdavids))

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
