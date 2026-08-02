

# Herramienta ADR-J
![Release](https://img.shields.io/github/v/release/adoble/adr-j)
![Build Status](https://github.com/adoble/adr-j/actions/workflows/gradle.yml/badge.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


Una herramienta de línea de comandos basada en Java para trabajar con [Registros de Decisiones de Arquitectura][ADRs] (ADRs). Más información sobre qué son los ADRs y por qué utilizarlos se puede encontrar en este [artículo](https://adr.github.io/).

ADR-J se basa en las [herramientas basadas en scripts de Nat Pryce](https://github.com/npryce/adr-tools). Ha sido extendida para que:

- Se puedan crear plantillas ADR personalizadas que se ajusten a las directrices de desarrollo internas o a plantillas estándar como el [formato Nyquest](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions) o [MADR](https://www.ozimmer.ch/practices/2022/11/22/MADRTemplatePrimer.html) o **cualquier otra**.

- Se puedan utilizar diferentes formatos de marcado en las plantillas (por ejemplo, **Markdown** y **AsciiDoc**), lo que significa que los ADRs pueden usarse con diferentes herramientas de documentación internas.  

# Inicio rápido

[Instalar la herramienta ADR-J](./doc/usage/INSTALL.md)

Utilice el comando `adr` para gestionar los ADRs. Pruebe ejecutando `adr help`.

Los ADRs se almacenan en su proyecto como archivos de marcado en el directorio `doc/adr`.


1. Cree un directorio ADR en la raíz de su proyecto:

        adr init 

    Esto creará el primer registro ADR indicando que está utilizando ADRs
    para documentar decisiones arquitectónicas y haciendo referencia al
    [artículo de Michael Nygard sobre el tema][ADRs]. El valor predeterminado es usar la plantilla de Nygard, Markdown de GitHub y el directorio relativo `doc/adr`. 
 
    Para usar una plantilla diferente a la predeterminada, especifique la ruta de una plantilla:

       adr init -t ~/standards/madr.md 

    Una guía para escribir plantillas se puede encontrar [aquí](./doc/usage/Writing_Templates.md). Las plantillas de ejemplo se pueden encontrar [aquí](./doc/example_templates).

    Para usar un directorio diferente para los ADRs creados, especifique el directorio:

       adr init doc/architecture/decisions

    Para combinar ambas opciones, utilice:

        adr init -t ~/standards/madr.md doc/architecture/decisions

    Después de la inicialización, se crea un [archivo de propiedades](./doc/usage/properties_file.md) en el directorio `.adr`.


2. Crear Registros de Decisiones de Arquitectura

        adr new Implement as Unix shell scripts

    Esto creará un nuevo archivo ADR numerado y lo abrirá en su
    editor de elección (tal como se especifica en la variable de entorno ADR_EDITOR).

    Para crear un nuevo ADR que reemplace a uno anterior (por ejemplo, el ADR 9), utilice la opción -s.

        adr new -s 9 Use Rust for performance-critical functionality

    Esto creará un nuevo archivo ADR marcado como reemplazo del
    ADR 9. Luego, abre el nuevo ADR en su
    editor de elección.

    Para crear un nuevo ADR que haga referencia a otro ADR, utilice la opción -l.

       adr new -l 4:"Links to" Use JMS interface for messaging

   Esto creará un nuevo ADR que hace referencia al ADR 4 e inserta el mensaje
   "Links to" en el nuevo ADR.  

3. Para más información, consulte las [páginas de manual](doc/man/adr.md) o utilice la ayuda incorporada:

        adr help



# Decisiones de Arquitectura

Las decisiones para esta herramienta se registran como [registros de decisiones de arquitectura en el repositorio del proyecto](doc/adr/).

# Compilación


Este proyecto utiliza Gradle para compilar. Ejecute el siguiente comando para generar `build/release/adr-j.jar`

    gradlew releaseJar

[ADRs]: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions
