# Writing templates

Users can write their own templates for the architecture decision records and specify that they should be used for ADRs using the `init` and `config` commands.

Templates use their own markup for specifying the substitutions the `adr-j` tool uses.

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

Lists are specified using `{{{listname.listfield}}}`. This is  substituted with  each value of the field and also copies any markup and content on the same line, e.g.:

```
* Supersedes [ADR {{{superseded.id}}}]({{{superseded.file}}})
```

could be replaced with:

```
* Supersedes [ADR 4](0004-use-asynchronous-communication.md)
* Supersedes [ADR 10](0010-use-a-relational-database.md)
* Supersedes [ADR 13](0013-use-jms-based-queueing-system.md)
```

Every list has a list field `id`. This has to be present. All list fields need to be on the same line, e.g. the following is **not** valid:

```
Link: {{link.comment}} See:
 {{{link.file}}}
```
The reasons the above is **not** valid are:
* No link.id field
* Fields are on separate lines.  

## ADR-J fields

### `id`

 Type: field

 Description: The identifier of the ADR


### `name`

Type: field

Description: The name of the ADR   

### `date`

Type: field

Description: The data the ADR was created                 

### `author`

Type: field

Description: The initial author of the ADR; it is determined in the following order:
 1. the value of the environment variable `ADR_AUTHOR`
 2. the value of the JVM system property "user.name"

### `supersedes.id`

Type: list

Description: Identifier of the ADR superseded by this ADR


### `supersedes.file`

Type: list

Description: File name of the ADR superseded by this ADR  

### `link.id`

Type: list
Description: Identifier of the linked ADR

### `link.file`

TYpe: list

Description: File name of the linked ADR     

### `link.comment`

Type: list

Description: Comment  about the linked ADR

### `template.comment`

Type: field

Description: Certain commands (such as link) insert additional, but hidden, information into the ADRs as comments. 
These are required for when ADR-J modifies or reads the ADR after it has been created. 
To do this, they need to know how text is commented out in the template's markup language. This 
(valueless) field is used to do this, by surrounding it with the text used for commenting. 

For instance, a markdown template would use HTML comments:

`<! –– {{template.comment}} ––>`

An ASCIIDOC template would use: 

`// {{template.comment}}`

      
