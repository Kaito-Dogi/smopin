plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.compose) apply false
  alias(libs.plugins.composeCompiler) apply false
  alias(libs.plugins.kotlinAndroid) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false

  // 新規に追加する場合はここから
  alias(libs.plugins.googleServices) apply false
  alias(libs.plugins.ktlintGradle) apply false
  alias(libs.plugins.metro) apply false
}

subprojects {
  // https://github.com/jlleitschuh/ktlint-gradle?tab=readme-ov-file#applying-to-subprojects
  // Version should be inherited from parent
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
