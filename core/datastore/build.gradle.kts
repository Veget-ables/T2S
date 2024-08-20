plugins {
    alias(libs.plugins.t2s.android.library)
    alias(libs.plugins.t2s.hilt)
}

android {
    namespace = "com.tsuchinoko.t2s.core.datastore"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.androidx.datastore)
}
