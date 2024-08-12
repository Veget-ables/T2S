plugins {
    alias(libs.plugins.t2s.android.library.compose)
    alias(libs.plugins.t2s.android.feature)
}

android {
    namespace = "com.tsuchinoko.t2s.feature.schedule"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)

    implementation(libs.accompanist.permissions)
    implementation(libs.google.play.services.auth)
}