plugins {
    alias(libs.plugins.t2s.android.library)
    alias(libs.plugins.t2s.android.library.compose)
}

android {
    namespace = "com.tsuchinoko.t2s.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)
    api(projects.core.common)
}
