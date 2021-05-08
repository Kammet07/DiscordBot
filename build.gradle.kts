plugins {
    kotlin("jvm") version "1.5.0"
    application
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation("io.ktor:ktor-client-core:1.5.3")
    implementation("io.ktor:ktor-client-apache:1.5.3")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.0")
}
