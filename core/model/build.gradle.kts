plugins {
    id("moneyConvertor-jvm-library")
    id("kotlinx-serialization")
}

dependencies {
    implementation(libs.kotlin.serialization.json)
}