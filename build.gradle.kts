plugins {
    java
    application
    `jvm-test-suite`
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    implementation("info.picocli:picocli:4.7.6")
    implementation("org.fusesource.jansi:jansi:2.4.1")
}

group = "org.doble"
version = "3.2.3-alpha"
description = "adr-j"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

application {
    mainClass = "org.doble.adr.ADR"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("META-INF/maven/org.fusesource.jansi/jansi/pom.xml")
    exclude("META-INF/maven/org.fusesource.jansi/jansi/pom.properties")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.11.0")
            dependencies {
                implementation("org.hamcrest:hamcrest:3.0")
                implementation("com.google.jimfs:jimfs:1.3.0")
            }
            targets {
                all {
                    testTask.configure {
                        testLogging {
                            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                        }
                    }
                }
            }
        }
    }
}

tasks.register<Copy>("releaseJar") {
    group = "ADR-J - Release"
    description = "Creates a JAR release."

    dependsOn(tasks.shadowJar)

    from(layout.buildDirectory.file("libs/adr-j-${version}-all.jar"))
    rename { n -> n.replace("-${version}-all", "") }
    into(layout.buildDirectory.dir("releases"))
    layout.buildDirectory.file("releases/adr-j.jar").map {
        // set executable with read permissions (first true) and for all (false)
        it.asFile.setExecutable(true, false)
    }
}
