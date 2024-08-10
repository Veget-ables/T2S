plugins {
    alias(libs.plugins.t2s.android.library)
    alias(libs.plugins.t2s.hilt)
}

android {
    namespace = "com.tsuchinoko.t2s.core.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.database)
}
