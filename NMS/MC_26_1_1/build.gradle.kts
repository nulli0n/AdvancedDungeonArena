import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension

plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    implementation(project(":NMS:SPI"))
    compileOnly(project(":API"))

    the<PaperweightUserDependenciesExtension>().paperDevBundle("26.1.1.build.29-alpha")
}

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(25))
}

paperweight {
    reobfArtifactConfiguration =
        io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

//configurations.matching { it.name == "reobf" }.configureEach {
//    outgoing.artifacts.clear()
//}

tasks.matching { it.name == "reobfJar" }.configureEach {
    enabled = false
}
