/*
 * Copyright 2024 searchkim Inc. - All Rights Reserved.
 */

rootProject.name = "quick-multimodule"

// 향후 모듈들이 여기에 추가됩니다
include(":modules:model")
include(":modules:exception")
include(":modules:service")
include(":modules:infrastructure")
include(":modules:repository-jdbc")
include(":modules:api")
// include(":modules:application-api")
// include(":modules:schema")

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