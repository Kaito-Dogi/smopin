package app.kaito_dogi.smopin.shared.common

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

enum class AppDispatchers {
  Default,
  IO,
}

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

@BindingContainer
object AppDispatcherBindingContainer {

  @Provides
  @AppDispatcher(dispatcher = AppDispatchers.Default)
  fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

  @Provides
  @AppDispatcher(dispatcher = AppDispatchers.IO)
  fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
