/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":modules:model"))
    implementation(project(":modules:exception"))
    implementation(project(":modules:service"))

    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation("org.springframework:spring-context:6.1.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")
}
