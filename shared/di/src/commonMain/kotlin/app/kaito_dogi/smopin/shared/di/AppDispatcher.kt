package app.kaito_dogi.smopin.shared.di

import dev.zacsweers.metro.Qualifier

@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.FIELD,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.TYPE,
)
@Qualifier
annotation class AppDispatcher(val dispatcher: AppDispatchers)

enum class AppDispatchers {
  Default,
  IO,
}
