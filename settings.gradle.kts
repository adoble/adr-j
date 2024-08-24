val expectedJavaVersion = JavaVersion.VERSION_21
val actualJavaVersion = JavaVersion.current()
require(actualJavaVersion.isCompatibleWith(expectedJavaVersion)) {
    "The build requires Java ${expectedJavaVersion.majorVersion} or higher. Currently executing with Java ${actualJavaVersion.majorVersion}."
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "adr-j"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
