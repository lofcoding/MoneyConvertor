plugins {
    id("moneyConvertor-android-library")
    id("moneyConvertor.android.compose")
    id("moneyConvertor.android.room")
    id("moneyConvertor-android.hilt")
}

android {
    namespace = "com.mc.database"
}


dependencies {
    implementation(project(":core:model"))
}