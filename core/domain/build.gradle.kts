plugins {
    alias(libs.plugins.t2s.jvm.library)
    alias(libs.plugins.t2s.hilt)
}

dependencies {
    implementation(projects.core.model)
}