plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization")
}

object SqlDelight {
    private const val version = "1.5.3"
    const val runtime = "com.squareup.sqldelight:runtime:$version"
    const val android = "com.squareup.sqldelight:android-driver:$version"
    const val native = "com.squareup.sqldelight:native-driver:$version"
}

sqldelight {
    database("AndroidExampleDatabase") {
        packageName = "com.felipe.db"
        sourceFolders = listOf("sqldelight")
    }
}

kotlin {
    android()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "generic_model"
        }
    }

    sourceSets {
        val ktorVersion = "2.0.3"
        val klockVersion = "2.4.13"

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("com.soywiz.korlibs.klock:klock:2.4.13") // Common
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
                implementation(SqlDelight.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation(SqlDelight.android)
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                implementation(SqlDelight.native)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 32
    }
}
dependencies {
    implementation("androidx.test:core-ktx:1.4.0")
    testImplementation("junit:junit:4.12")
}
