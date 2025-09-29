/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

plugins {
    id("java-library")
}

dependencies {
    // H2 Database
    implementation("com.h2database:h2:2.2.224")

    // Spring Boot JDBC (for database testing)
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc:3.2.0")
}