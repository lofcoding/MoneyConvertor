plugins {
    id("moneyConvertor-android-library")
    id("moneyConvertor.android.compose")
    id("moneyConvertor.android.feature")
}

android {
    namespace = "com.mc.currencyconvertor"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
}