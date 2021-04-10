plugins {
    kotlin("jvm") version "1.4.32"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

group = "com.kammet.discord.bot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("dev.kord:kord-core:0.7.0-SNAPSHOT")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("io.ktor:ktor-client-core:1.5.3")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
}
