/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

group = "io.example.coupon"
version = "1.0-SNAPSHOT"

allprojects {
    group = "io.example.coupon"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

val javaProjects = listOf(
    ":modules:model",
    ":modules:exception",
    ":modules:service",
    ":modules:infrastructure",
    ":modules:repository-jdbc",
    ":modules:api",
    ":modules:application-api",
    ":modules:schema"
)

configure(subprojects.filter { javaProjects.contains(it.path) }) {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        // Lombok 전역 설정
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")
        testCompileOnly("org.projectlombok:lombok:1.18.30")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.mockito:mockito-junit-jupiter")
        testImplementation("org.assertj:assertj-core")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
