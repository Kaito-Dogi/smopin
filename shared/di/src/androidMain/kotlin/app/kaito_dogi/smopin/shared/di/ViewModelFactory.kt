package app.kaito_dogi.smopin.shared.di

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import kotlin.reflect.KClass

@ContributesBinding(scope = AppScope::class)
@SingleIn(scope = AppScope::class)
@Inject
class ViewModelFactory(
  override val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>,
  override val assistedFactoryProviders:
  Map<KClass<out ViewModel>, Provider<ViewModelAssistedFactory>>,
  override val manualAssistedFactoryProviders:
  Map<KClass<out ManualViewModelAssistedFactory>, Provider<ManualViewModelAssistedFactory>>,
) : MetroViewModelFactory()
