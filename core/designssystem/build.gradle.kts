plugins {
    alias(libs.plugins.moneyconvertor.android.library)
    alias(libs.plugins.moneyconvertor.android.compose)
}

android {
    namespace = "com.mc.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}