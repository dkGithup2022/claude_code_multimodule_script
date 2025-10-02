/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":modules:model"))
    implementation(project(":modules:exception"))
    implementation(project(":modules:infrastructure"))

    testImplementation(project(":modules:schema"))
    testImplementation(project(":modules:repository-jdbc"))
    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    testImplementation("com.h2database:h2")

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("org.slf4j:slf4j-api")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")

}
