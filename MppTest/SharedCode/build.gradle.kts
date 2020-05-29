import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("kotlinx-serialization")
    id("com.android.library")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(Versions.min_sdk)
        targetSdkVersion(Versions.target_sdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

kotlin {
    //select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
    if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
    ::iosArm64
    else
    ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "SharedCode"
            }
        }
    }

    jvm("android")

    val serializationVersion = "0.20.0"
    val ktorVersion = "1.3.2"

    sourceSets["commonMain"].dependencies {
        implementation(kotlin("stdlib-common", Versions.kotlin))
        //implementation(Deps.SqlDelight.runtime)
        implementation(Deps.Ktor.commonCore)
        implementation(Deps.Ktor.commonJson)
        //implementation(Deps.Ktor.commonLogging)
        implementation(Deps.Coroutines.common)
//        implementation(Deps.stately)
//        implementation(Deps.multiplatformSettings)
//        implementation(Deps.koinCore)
        implementation(Deps.Ktor.commonSerialization)
        //api(Deps.kermit)
    }


//    sourceSets["commonTest"].dependencies {
//        implementation(Deps.multiplatformSettingsTest)
//        implementation(Deps.KotlinTest.common)
//        implementation(Deps.KotlinTest.annotations)
//        implementation(Deps.koinTest)
//    }

    sourceSets["androidMain"].dependencies {
        implementation(kotlin("stdlib", Versions.kotlin))
        //implementation(Deps.SqlDelight.driverAndroid)
//        implementation(Deps.Ktor.jvmCore)
//        implementation(Deps.Ktor.jvmJson)
        //implementation(Deps.Ktor.jvmLogging)
        implementation(Deps.Coroutines.jdk)
        //implementation(Deps.Coroutines.android)
        implementation(Deps.Ktor.androidSerialization)
//        implementation(Deps.Ktor.androidCore)
    }

    sourceSets["androidTest"].dependencies {
//        implementation(Deps.KotlinTest.jvm)
//        implementation(Deps.KotlinTest.junit)
//        implementation(Deps.AndroidXTest.core)
//        implementation(Deps.AndroidXTest.junit)
//        implementation(Deps.AndroidXTest.runner)
//        implementation(Deps.AndroidXTest.rules)
        //implementation(Deps.Coroutines.test)
        implementation("org.robolectric:robolectric:4.3")
    }

    sourceSets["iosMain"].dependencies {
        //implementation(Deps.SqlDelight.driverIos)
//        implementation(Deps.Ktor.ios)
//        implementation(Deps.Ktor.iosCore)
//        implementation(Deps.Ktor.iosJson)
//        implementation(Deps.Ktor.iosLogging)
//        implementation(Deps.Coroutines.native) {
//            version {
//                strictly("1.3.5-native-mt")
//            }
//        }
        implementation(Deps.Ktor.iosSerialization)
    }

}

val packForXcode by tasks.creating(Sync::class) {
    val targetDir = File(buildDir, "xcode-frameworks")

    /// selecting the right configuration for the iOS
    /// framework depending on the environment
    /// variables set by Xcode build
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets
            .getByName<KotlinNativeTarget>("ios")
            .binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    from({ framework.outputDirectory })
    into(targetDir)

    /// generate a helpful ./gradlew wrapper with embedded Java path
    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\n"
                + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                + "cd '${rootProject.rootDir}'\n"
                + "./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)