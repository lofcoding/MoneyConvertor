plugins {
    id("moneyConvertor-android-library")
    id("moneyConvertor.android.compose")
}

android {
    namespace = "com.mc.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}