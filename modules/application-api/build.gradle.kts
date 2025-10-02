/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

plugins {
    id("org.springframework.boot") version "3.2.0"
}

dependencies {
    implementation(project(":modules:model"))
    implementation(project(":modules:exception"))
    implementation(project(":modules:infrastructure"))
    implementation(project(":modules:service"))
    implementation(project(":modules:repository-jdbc"))
    implementation(project(":modules:api"))
    implementation(project(":modules:schema"))

    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:3.2.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
}
