/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":modules:model"))
    implementation(project(":modules:infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:3.2.0")
    implementation("org.springframework:spring-context:6.1.1")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("com.h2database:h2:2.2.224")
    testRuntimeOnly("com.h2database:h2:2.2.224")
}
