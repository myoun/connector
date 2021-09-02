plugins {
    kotlin("jvm") version "1.5.30"
}

group = "kr.myoung2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.javacord:javacord:3.3.2")
    implementation("org.json:json:20190722")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "kr.myoung2.connector.MainKt"
        }
    }
}