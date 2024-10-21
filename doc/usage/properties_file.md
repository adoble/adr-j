
# ADR-J Properties File 

After initialisation, a properties file (`adr.properties`) containing the current configuration is created in the directory `.adr`.

This consists of a header with the creation date followed by property value pairs seperated with a '=' sign, e.g. 

    initialTemplateFile=templates/madr_initial.md

## Properties 

`dateFormat` How the dates in the ADRs are represented. Currently only ISO_LOCAL_DATE is supported. 

`initialTemplateFile` Path to the template file used to create the initial ADR.

`templateFile` Path to the template file used to create new ADRs.

`docPath`  Relative path to the location where ADRs are stored.

`authorEmail`  The email of the initial author of the ADR.

`tocTemplateFile`   The template file used to create a table of contents (TOC)
