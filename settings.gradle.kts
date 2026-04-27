rootProject.name = "AdvancedDungeonArena"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(
    ":API",
    ":Core",
    ":NMS",
    ":NMS:MC_1_21_3",
    ":NMS:MC_1_21_8",
    ":NMS:MC_1_21_10",
    ":NMS:MC_1_21_11",
    ":NMS:MC_26_1_1"
)