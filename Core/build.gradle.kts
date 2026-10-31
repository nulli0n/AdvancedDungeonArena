import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://repo.auroramc.gg/releases")
    maven("https://nexus.neetgames.com/repository/maven-public")
}

dependencies {
    implementation(project(":API"))
    implementation(project(":NMS"))
    implementation(project(":nms-26-2"))
    
    compileOnly("su.nightexpress.sunlight:Core:3.17.0")
    compileOnly("su.nightexpress.sunlight:API:3.17.0")
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
    compileOnly("su.nightexpress.combatpets:API:2.8.1") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    compileOnly("gg.auroramc:AuroraLevels:2.0.0")
    compileOnly("gg.auroramc:Aurora:2.1.6")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("thin") 
}

// Configure the shadowJar task to bundle the necessary dependencies.
tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("AdvancedDungeonArena")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("")

    /* dependencies {
        include(project(":crates-api"))
        include(project(":integration-protocollib"))
        include(project(":integration-packetevents"))
    } */
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    processResources {
        // Replicates maven <filtering>true</filtering> for plugin.yml
        // Replaces ${version} with the project version.
        val currentVersion = project.version.toString()

        filesMatching("*plugin.yml") {
            expand(mapOf("version" to currentVersion))
        }
    }
}

// Equivalent of the maven-source-plugin.
//java {
//    withSourcesJar()
//    withJavadocJar()
//}

// Ensure that building the project runs the shadowJar task.
tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}