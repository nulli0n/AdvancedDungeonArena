plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.2.1"
}

group = "su.nightexpress.dungeonarena"
version = "8.5.1"

dependencies {
    implementation(project(":API"))
    implementation(project(path = ":Core", configuration = "shadow"))
    implementation(project(path = ":NMS:MC_1_21_3", configuration = "reobf"))
    implementation(project(path = ":NMS:MC_1_21_8", configuration = "reobf"))
    implementation(project(path = ":NMS:MC_1_21_10", configuration = "reobf"))
    implementation(project(path = ":NMS:MC_1_21_11", configuration = "reobf"))
    implementation(project(path = ":NMS:MC_26_1_1", configuration = "shadow"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()

        maven("https://jitpack.io")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.kyori.net/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://repo.codemc.org/repository/nms/")
        maven("https://repo.nightexpressdev.com/releases/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("su.nightexpress.nightcore:main:2.15.2")
        compileOnly("su.nightexpress.nightcore:utils:2.15.2")
    }

    configurations.all {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }

    java {
        withSourcesJar()
    }

    tasks {
        compileJava {
            options.isDeprecation = true
            options.encoding = "UTF-8"
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}