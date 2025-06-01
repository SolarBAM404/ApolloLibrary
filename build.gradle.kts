plugins {
    kotlin("jvm") version "2.2.0-RC"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.run-velocity") version "2.3.1"
}

allprojects {

    group = "me.solar.apollo"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            name = "spigotmc-repo"
        }
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "xyz.jpenilla.run-paper")
    apply(plugin = "xyz.jpenilla.run-velocity")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
        testImplementation(kotlin("test"))
    }

    tasks {
        build {
            dependsOn("shadowJar")
        }

        processResources {
            val props = mapOf("version" to version)
            inputs.properties(props)
            filteringCharset = "UTF-8"
            filesMatching("plugin.yml") {
                expand(props)
            }
        }

        test {
            useJUnitPlatform()
        }
    }

}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
