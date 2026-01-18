import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.compose)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.metro)
}

android {
  namespace = "app.kaito_dogi.smopin.feature.hoge"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  defaultConfig {
    minSdk = libs.versions.android.minSdk.get().toInt()
  }
  kotlin {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }
}

dependencies {
  implementation(projects.shared.domain)
  implementation(compose.runtime)
  implementation(compose.foundation)
  implementation(compose.material3)
  implementation(compose.ui)
  implementation(compose.components.resources)
  implementation(compose.components.uiToolingPreview)
  implementation(libs.androidx.lifecycle.viewmodelCompose)
  implementation(libs.androidx.lifecycle.runtimeCompose)

  implementation(libs.metroxAndroid)
  implementation(libs.metroxViewModel)
  implementation(libs.metroxViewModelCompose)
  implementation(libs.androidx.core.ktx)
}
