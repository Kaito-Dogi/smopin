import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.compose)
  alias(libs.plugins.composeCompiler)
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
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlin {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }
}

dependencies {
  implementation(projects.android.feature.hoge)
  implementation(projects.shared.common)
  implementation(projects.shared.data)
  implementation(projects.shared.database.firestore)
  implementation(projects.shared.domain)
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

  implementation(libs.metroxAndroid)
  implementation(libs.metroxViewModel)
  implementation(libs.metroxViewModelCompose)
}
