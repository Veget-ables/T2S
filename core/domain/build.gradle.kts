plugins {
    alias(libs.plugins.t2s.android.library)
    alias(libs.plugins.t2s.hilt)
}

android {
    namespace = "com.tsuchinoko.t2s.core.domain"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.data)
}
