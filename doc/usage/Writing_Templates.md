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
* Fields are on seperate lines.  

## ADR-J fields

  Name      | List Field | Type  | Description                                  
------------|------------|------------------------------------------------------
 id         |            | field | The id of the ADR                            
 name       |            | field | The name of the ADR                          
 date       |            | field | The data the ADR was created                 
 supersedes |  id        | list  | Identifier of the ADR superseded by this ADR
 supersedes |  file      | list  | File name of the ADR superseded by this ADR  
 link       |  id        | list  | Identifier of the linked ADR                 
 link       |  file      | list  | File name of the linked ADR                  
 link       |  comment   | list  | Comment  about the linked ADR                
