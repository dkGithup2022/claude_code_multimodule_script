/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")

// 라이브러리 모듈이므로 bootJar 대신 jar 사용
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier = ""
}

dependencies {
    implementation(project(":modules:model"))
    implementation(project(":modules:exception"))
    implementation(project(":modules:infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}