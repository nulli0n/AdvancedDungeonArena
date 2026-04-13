
plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
}

group = rootProject.group
version = rootProject.version

subprojects {
    dependencies {
        compileOnly(project(":API"))

        compileOnly("su.nightexpress.dungeonarena:API:8.5.1")
    }
}
