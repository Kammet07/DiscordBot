val kotlinVersion: String by project
val ktorVersion: String by project

plugins {
    kotlin("jvm") version "1.5.10"
    application
    kotlin("plugin.serialization") version "1.5.10"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "15"
    }
}

group = "com.kammet.discord.bot"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.kammet.discord.bot.ApplicationKt")
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.dv8tion:JDA:4.2.1_253")
    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
}
