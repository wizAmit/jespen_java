/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.10.2/userguide/building_java_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application;

    // Apply GraalVM Native Image plugin
    id("org.graalvm.buildtools.native") version "0.10.3";
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.1")
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.10.3")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "io.jespen.Runner"
}

tasks.jar {
    manifest.attributes["Main-Class"] = "io.jespen.Runner"

    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("jespen-runner")
            mainClass.set("io.jespen.Runner")
            buildArgs.add("-O4")
            buildArgs.add("-H:-ReduceImplicitExceptionStackTraceInformation")
        }
        // named("test") {
        //     buildArgs.add("-O0")
        // }
    }
    binaries.all {
        buildArgs.add("--verbose")
    }
}


