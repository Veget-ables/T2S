plugins {
    alias(libs.plugins.t2s.android.library)
    alias(libs.plugins.t2s.hilt)
}

android {
    namespace = "com.tsuchinoko.t2s.core.network"
}
dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)

    // Google Service API
    implementation(libs.google.api.client.android)
    implementation(libs.google.oauth.client.jetty)
    implementation(libs.google.api.calendar)
    implementation(libs.google.play.services.auth)
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
}
