plugins {
    java
}

group = "com.savagemc"
version = "3.0-SavageMC"
description = "Plugin de construcao SavageMC - WorldEdit, cargos, tags, scoreboard"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.1-SNAPSHOT") {
        isTransitive = false
    }
    implementation("fr.mrmicky:fastboard:2.1.5")
    implementation("com.mysql:mysql-connector-j:8.0.33")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(projectDir) {
        include("plugin.yml", "config.yml")
    }
    from("$projectDir/craftscripts") {
        into("craftscripts")
    }
}

// JAR com FastBoard + MySQL (sem Shadow plugin — compatível com Gradle 9)
tasks.jar {
    archiveFileName.set("savagebuilds.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.assemble {
    dependsOn(tasks.jar)
}

tasks.build {
    dependsOn(tasks.jar)
}
