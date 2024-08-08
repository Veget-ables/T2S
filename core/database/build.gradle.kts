plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.tsuchinoko.core.database"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}