plugins {
    alias(libs.plugins.t2s.android.library.compose)
    alias(libs.plugins.t2s.android.feature)
}

android {
    namespace = "com.tsuchinoko.t2s.feature.schedule"
}

dependencies {
    implementation(libs.generativeai)
    implementation(libs.accompanist.permissions)

    // Calendar API
    implementation(libs.google.api.client.android)
    implementation(libs.google.oauth.client.jetty)
    implementation(libs.google.api.calendar)
    implementation(libs.google.play.services.auth)
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
}