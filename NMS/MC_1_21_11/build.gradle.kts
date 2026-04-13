import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension

plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    compileOnly(project(":NMS"))
    compileOnly(project(":NMS:SPI"))
    compileOnly(project(":API"))

    the<PaperweightUserDependenciesExtension>().paperDevBundle("1.21.11-R0.1-SNAPSHOT")

}

tasks {
    build {
        dependsOn(reobfJar)
    }

    reobfJar {
        mustRunAfter(shadowJar)
    }
}
