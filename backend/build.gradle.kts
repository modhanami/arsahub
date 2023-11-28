import cz.habarta.typescript.generator.JsonLibrary
import cz.habarta.typescript.generator.TypeScriptFileType
import cz.habarta.typescript.generator.TypeScriptOutputKind
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.hibernate.orm") version "6.2.7.Final"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.10"
    id("com.google.cloud.tools.jib") version "3.4.0"
    id("cz.habarta.typescript-generator") version "3.2.1263"
//    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.arsahub"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.h2database:h2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("com.corundumstudio.socketio:netty-socketio:2.0.3")
    implementation("net.pwall.json:json-kotlin-schema:0.42")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
//    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.networknt:json-schema-validator:1.0.87")
//    implementation("com.github.gavlyukovskiy:datasource-proxy-spring-boot-starter:1.9.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.liquibase:liquibase-core")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator")
//    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.testcontainers:testcontainers:1.19.2")
    testImplementation("org.testcontainers:junit-jupiter:1.19.2")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

hibernate {
    enhancement {
        enableAssociationManagement.set(true)
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
}

jib {
    to {
        image = "modhanami/arsahub-backend-dev"
        setCredHelper("wincred")
    }
}

tasks {
    generateTypeScript {
        jsonLibrary = JsonLibrary.jackson2
        classPatterns = listOf("com.arsahub.backend.dtos.*")
        outputKind = TypeScriptOutputKind.module
        outputFileType = TypeScriptFileType.implementationFile
        outputFile = "generated-types.ts"
    }
}

tasks.register("generateTypesForFrontend") {
    dependsOn("generateTypeScript")
    doLast {
        copy {
            from("generated-types.ts")
            into("../frontend/types")
        }
    }
}
