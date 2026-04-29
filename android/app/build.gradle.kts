import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.compose)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.googleServices)
  alias(libs.plugins.metro)
}

android {
  namespace = "app.kaito_dogi.smopin"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "app.kaito_dogi.smopin"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"
    }
    getByName("release") {
      // TODO: リリース前に難読化対応を完了し、true にする
      isMinifyEnabled = false
    }
  }

  flavorDimensions += "env"
  productFlavors {
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
      localPropertiesFile.inputStream().use(block = localProperties::load)
    }

    create("dev") {
      dimension = "env"
      applicationIdSuffix = ".dev"
      versionNameSuffix = "-dev"

      manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = localProperties.getProperty("GOOGLE_MAPS_API_KEY_DEV") ?: System.getenv("GOOGLE_MAPS_API_KEY_DEV")
    }
    create("prod") {
      dimension = "env"

      manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = localProperties.getProperty("GOOGLE_MAPS_API_KEY_PROD") ?: System.getenv("GOOGLE_MAPS_API_KEY_PROD")
    }
  }

  buildFeatures {
    buildConfig = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlin {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }
}

dependencies {
  implementation(projects.android.feature.map)
  implementation(projects.shared.common)
  implementation(projects.shared.data)
  implementation(projects.shared.database.firestore)
  implementation(projects.shared.domain)
  implementation(projects.shared.location)

  implementation(compose.runtime)
  implementation(compose.foundation)
  implementation(compose.material3)
  implementation(compose.ui)
  implementation(compose.components.resources)
  implementation(compose.components.uiToolingPreview)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.lifecycle.viewmodelCompose)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.core.ktx)

  debugImplementation(compose.uiTooling)
  debugImplementation(libs.androidx.ui.testManifest)

  androidTestImplementation(compose.uiTest)
  androidTestImplementation(libs.androidx.testExt.junit)
  androidTestImplementation(libs.androidx.ui.testJunit4)
  androidTestImplementation(libs.androidx.uiautomator)

  implementation(platform(libs.firebaseBom))
  implementation(libs.firebaseFirestore)
  implementation(libs.gitliveFirebaseFirestore)
  implementation(libs.metroAndroid)
  implementation(libs.metroViewModel)
  implementation(libs.metroViewModelCompose)
}
