import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.tsuchinoko.t2s.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "t2s.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "t2s.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "t2s.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "t2s.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("hilt") {
            id = "t2s.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidFeature") {
            id = "t2s.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "t2s.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
