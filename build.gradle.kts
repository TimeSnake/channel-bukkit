import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    id("java")
    id("java-base")
    id("java-library")
    id("maven-publish")
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.timesnake"
version = "5.0.0"
var projectId = 42

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://git.timesnake.de/api/v4/groups/7/-/packages/maven")
        name = "timesnake"
        credentials(PasswordCredentials::class)
    }
}

dependencies {
    implementation("de.timesnake:channel-api:5.+")

    compileOnly("de.timesnake:library-basic:2.+")

    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("org.apache.logging.log4j:log4j-api:2.22.1")
    compileOnly("org.apache.logging.log4j:log4j-core:2.22.1")

    compileOnly("com.moandjiezana.toml:toml4j:0.7.3-SNAPSHOT")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

configurations.configureEach {
    resolutionStrategy.dependencySubstitution {
        if (project.parent != null) {
            substitute(module("de.timesnake:channel-api")).using(project(":channel:channel-api"))
            substitute(module("de.timesnake:library-basic")).using(project(":libraries:library-basic"))
        }
    }
}

tasks.register<Copy>("exportAsPlugin") {
    from(layout.buildDirectory.file("libs/${project.name}-${project.version}-all.jar"))
    into(findProperty("timesnakePluginDir") ?: "")

    dependsOn("shadowJar")
}

tasks.withType<PublishToMavenRepository> {
    dependsOn("shadowJar")
}

publishing {
    repositories {
        maven {
            url = uri("https://git.timesnake.de/api/v4/projects/$projectId/packages/maven")
            name = "timesnake"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("plugin.yml") {
            expand(mapOf(Pair("version", project.version)))
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION