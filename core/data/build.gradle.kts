plugins {
    alias(libs.plugins.t2s.android.library)
}

android {
    namespace = "com.tsuchinoko.t2s.core.data"
}

dependencies {
    implementation(projects.core.model)
}
