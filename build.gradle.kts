plugins {
    java
    application
    `jvm-test-suite`
    alias(libs.plugins.shadow)
}

configurations {
    implementation {
        resolutionStrategy.failOnNonReproducibleResolution()
    }
}

dependencies {
    implementation(libs.picocli)
    implementation(libs.jansi)
}

group = "org.doble"
version = "3.2.3-alpha"
description = "adr-j"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }

    consistentResolution {
        useCompileClasspathVersions()
    }
}

application {
    mainClass = "org.doble.adr.ADR"
}

tasks.named<JavaCompile>("compileJava") {
    options.apply {
        release = 11
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    filePermissions {
        unix(644)
    }
    dirPermissions {
        unix(755)
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("META-INF/maven/org.fusesource.jansi/jansi/pom.xml")
    exclude("META-INF/maven/org.fusesource.jansi/jansi/pom.properties")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit)
            dependencies {
                implementation(libs.hamcrest)
                implementation(libs.jimfs)
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
