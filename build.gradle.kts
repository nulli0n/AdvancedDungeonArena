plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.23" apply(false)
    id("maven-publish")
}

allprojects {
    group = "su.nightexpress.dungeonarena"
    version = "8.8.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.nightexpressdev.com/releases")
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
        withSourcesJar()
        // withJavadocJar()
    }

    // Set the project encoding.
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    dependencies {
        compileOnly("su.nightexpress.nightcore:main:2.16.6")
        compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                
                artifactId = project.name
            }
        }
        repositories {
            maven {
                name = "nightexpress"
                url = uri("https://repo.nightexpressdev.com/releases")
                credentials {
                    username = System.getenv("REPOSILITE_USER")
                    password = System.getenv("REPOSILITE_PASSWORD")
                }
            }
        }
    }
}