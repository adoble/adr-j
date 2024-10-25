# Writing a Template for a Table of Contents.

Users can write their own templates for a table for contents (TOC) and specify that they should be used for TOCS by using `config` command or directly using the `-template`option, e.g.:

```
adr generate toc -template [My TOC Template]
```

Templates for table of contents use the [handlebars syntax](https://handlebarsjs.com/). Only a subset of this is described here. The fields available when generating TOCs are show in [ADR-J fields for TOCs](#adr-j-fields-for-tocs).  

## Example TOC Template

An example for a TOC template using the handlebars syntax is: 

```markdown
# List of ADRs 

{{#entries}}
* [ADR {{id}}]({{filename}}) : {{title}}
{{/entries}}

Created: {{date}}
```


## Field substitution

`{{field}}` is substituted with the value of `field`, e.g.

```
* Created: {{date}}
```
After substitution this could look like:

```
* Created: 21 Oct 2024
```

Note the the date format depended on the `adr-j`configuration.

## List substitution

To iterate over the list of ADRs use the `{{#entries}}` and `{{/entries}}`  helpers. Any fields within this pair will refer to an ADR. For instance: 

```
{{#entries}}
* ADR {{id}}
{{/entries}}
```
could return a list of the ADRs identifiers:

```
- ADR 1
- ADR 2
- ADR 3
- ADR 4
```


# ADR-J Fields for TOCs

The following fields are used in generating TOCs:

| Name | Type | Description |
| ---- | ---- | ----------- |
| `id` | field| The identifier of the ADR |
| `filename` | field| The file name of the ADR |
| `title` | field| The title of the ADR |
| `date`| field | The current date |
| `entries` | iterator | Iterator over each ADR |



## 

