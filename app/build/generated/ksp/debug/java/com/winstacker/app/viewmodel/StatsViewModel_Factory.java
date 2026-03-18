package com.winstacker.app.viewmodel;

import com.winstacker.app.data.repository.WinRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class StatsViewModel_Factory implements Factory<StatsViewModel> {
  private final Provider<WinRepository> winRepositoryProvider;

  public StatsViewModel_Factory(Provider<WinRepository> winRepositoryProvider) {
    this.winRepositoryProvider = winRepositoryProvider;
  }

  @Override
  public StatsViewModel get() {
    return newInstance(winRepositoryProvider.get());
  }

  public static StatsViewModel_Factory create(Provider<WinRepository> winRepositoryProvider) {
    return new StatsViewModel_Factory(winRepositoryProvider);
  }

  public static StatsViewModel newInstance(WinRepository winRepository) {
    return new StatsViewModel(winRepository);
  }
}
