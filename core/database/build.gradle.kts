plugins {
    alias(libs.plugins.t2s.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.tsuchinoko.t2s.core.database"
}

dependencies {
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}
