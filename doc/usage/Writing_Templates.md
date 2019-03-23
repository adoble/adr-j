# Writing templates
Users can write their own templates for the architecture decision records and specify that they should be used for ADRs using the `init` and `config` commands.

Templates use their own markup for specifiing the substitutions the `adr-j` tool uses.

## Field substitution

`{{field}}` is substituted with the value of `field`, e.g.

```
* Status: {{status}}
```
After substitution this could look like:

```
* Status: APPROVED
```

## List substitution

Lists are specified using `{{{list_field}}}`. This is  substituted with the each value of the field and also copies any markup and content on the same line, e.g.:

```
* {{{supercedes}}}
```

could be replaced with:

```
* Supersedes ADR 004 - Use asynchronous communication
* Supercedes ADR 010 - Use a relational database
* Supercedes ADR 013 - Use JMS based queueing system
```
## ADR-J fields

|  Name      | Type  | Description                           |
|------------|-------|---------------------------------------|
| id         | field | The id of the ADR                     |
| name       | field | The name of the ADR                   |
| date       | field | The data the ADR was created          |
| supersedes | list  | Which ADRs are superseded by this ADR |
| link       | list  | Links to associated ADRs              |
