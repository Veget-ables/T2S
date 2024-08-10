plugins {
    alias(libs.plugins.t2s.jvm.library)
    alias(libs.plugins.t2s.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}