plugins {
    kotlin("jvm") version "2.2.0-RC"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.run-velocity") version "2.3.1"
    id("maven-publish")
}

allprojects {

    group = "me.solarbam.apollo"
    version = "1.0-SNAPSHOT"


    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc"
        }
        maven("https://oss.sonatype.org/content/groups/public/") {
            name = "sonatype"
        }
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "xyz.jpenilla.run-paper")
    apply(plugin = "xyz.jpenilla.run-velocity")
    apply(plugin="maven-publish")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("net.kyori:adventure-api:4.21.0")
        implementation("net.kyori:adventure-platform-bukkit:4.4.0")
        implementation("net.kyori:adventure-text-minimessage:4.21.0")
        compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
        testImplementation(kotlin("test"))

        // Lombok
        implementation("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/SolarBAM404/ApolloLibrary")
                credentials {
                    username = System.getenv("GITHUB_USERNAME")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
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
