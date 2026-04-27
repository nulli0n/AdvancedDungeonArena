
repositories {
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.auroramc.gg/releases")
    maven("https://nexus.neetgames.com/repository/maven-public")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
}

dependencies {
    compileOnly(project(":API"))

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    compileOnly("su.nightexpress.sunlight:Core:3.16.0")
    compileOnly("su.nightexpress.sunlight:API:3.16.0")

    compileOnly("io.lumine:Mythic-Dist:5.10.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.0")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    compileOnly("net.essentialsx:EssentialsX:2.21.2") {
        exclude(group = "com.mojang", module = "brigadier")
        exclude(group = "io.papermc", module = "paperlib")
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }

    compileOnly("com.github.NEZNAMY:TAB-API:5.2.5")

    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.049") {
        exclude(group = "com.sk89q.worldguard")
    }

    compileOnly("io.lumine:MythicLib-dist:1.6.2-SNAPSHOT")
    compileOnly("net.Indyuce:MMOCore-API:1.12.1-SNAPSHOT")

    compileOnly("su.nightexpress.combatpets:API:2.5.0")

    compileOnly("gg.auroramc:AuroraLevels:2.0.0")
    compileOnly("gg.auroramc:Aurora:2.1.6")
}

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version.toString()
        )

        inputs.properties(props)

        filesMatching("**plugin.yml") {
            expand(props)
        }
    }
}