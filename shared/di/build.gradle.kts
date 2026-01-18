import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.metro)
}

kotlin {
  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }

  listOf(
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "SharedDI"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.metroxAndroid)
      implementation(libs.metroxViewModel)
      implementation(libs.metroxViewModelCompose)
    }
  }
}

android {
  namespace = "app.kaito_dogi.smopin.shared.di"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  defaultConfig {
    minSdk = libs.versions.android.minSdk.get().toInt()
  }
}
