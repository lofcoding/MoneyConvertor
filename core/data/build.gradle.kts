plugins {
    id("moneyConvertor-android-library")
    id("moneyConvertor.android.compose")
    id("moneyConvertor-android.hilt")
    id("moneyConvertor.android.room")
}

android {
    namespace = "com.mc.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    implementation(libs.androidx.work)
    implementation(libs.hilt.ext.work)
}