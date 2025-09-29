/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":modules:model"))
    implementation(project(":modules:exception"))
    implementation(project(":modules:infrastructure"))

    // Spring Framework
    implementation("org.springframework:spring-context:6.1.0")
    implementation("org.springframework.boot:spring-boot-starter:3.2.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
}