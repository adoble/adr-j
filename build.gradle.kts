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
    // Dependency versions can be found at {project root}/gradle/libs.versions.toml 
    implementation(libs.picocli)
    implementation(libs.jansi)
    implementation(libs.commonstext)
    implementation(libs.handlebars)
    implementation(libs.slf4j)
    implementation(libs.commonsio)
}

group = "org.doble"
version = "3.4.1"
description = "adr-j"

java {
    toolchain {
        // Development Java version is the latest LTS version
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
    // Binary compatability with LTS version - 2 
    options.apply {
        release = 11
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
    filePermissions {
        unix("rw-r--r--")
    }
    dirPermissions {
        unix("rwxr-xr-x")
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
