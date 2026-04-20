import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.metro)
}

kotlin {
  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  listOf(
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "SharedLocation"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(projects.shared.common)
      implementation(projects.shared.data)

      implementation(libs.kotlinxCoroutinesCore)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }

    androidMain.dependencies {
      implementation(libs.gmsPlayServicesLocation)
      implementation(libs.kotlinxCoroutinesPlayServices)
    }
  }
}

android {
  namespace = "app.kaito_dogi.smopin.shared.location"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  defaultConfig {
    minSdk = libs.versions.android.minSdk.get().toInt()
  }
}
