plugins {
    alias(libs.plugins.moneyconvertor.android.library)
    alias(libs.plugins.moneyconvertor.android.compose)
    alias(libs.plugins.moneyconvertor.android.feature)
}

android {
    namespace = "com.mc.currencyconvertor"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
}