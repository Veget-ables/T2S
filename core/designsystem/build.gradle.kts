plugins {
    alias(libs.plugins.t2s.android.library)
    alias(libs.plugins.t2s.android.library.compose)
}

android {
    namespace = "com.tsuchinoko.t2s.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.material3)
}
