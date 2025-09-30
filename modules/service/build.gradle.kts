/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

plugins {
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
}

dependencies {
    implementation(project(":modules:model"))
    implementation(project(":modules:exception"))
    implementation(project(":modules:infrastructure"))

    // Spring dependencies for @Service annotation
    implementation("org.springframework:spring-context")

    // SLF4J for logging (already provided by Lombok via @Slf4j)
    implementation("org.slf4j:slf4j-api")
}