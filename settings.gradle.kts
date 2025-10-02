/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

rootProject.name = "coupon"

// 모듈들 선언 (modules 자체는 프로젝트가 아님)
include(":modules:model")
include(":modules:exception")
include(":modules:infrastructure")
include(":modules:service")
include(":modules:repository-jdbc")
include(":modules:api")
include(":modules:schema")
include(":modules:application-api")

// modules 디렉토리의 실제 위치 매핑
project(":modules:model").projectDir = file("modules/model")
project(":modules:exception").projectDir = file("modules/exception")
project(":modules:infrastructure").projectDir = file("modules/infrastructure")
project(":modules:service").projectDir = file("modules/service")
project(":modules:repository-jdbc").projectDir = file("modules/repository-jdbc")
project(":modules:api").projectDir = file("modules/api")
project(":modules:schema").projectDir = file("modules/schema")
project(":modules:application-api").projectDir = file("modules/application-api")

pluginManagement {
    buildscript {
        repositories {
            gradlePluginPortal()
        }
    }

    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
