plugins {
    id("java")
    id("application")
}

group = "dev.cheercode"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("dev.cheercode.FileAggregatorApp")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-class" to "dev.cheercode.FileAggregatorApp")
    }
}