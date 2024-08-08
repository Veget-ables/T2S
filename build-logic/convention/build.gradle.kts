import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.tsuchinoko.t2s.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}


gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "t2s.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "t2s.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
