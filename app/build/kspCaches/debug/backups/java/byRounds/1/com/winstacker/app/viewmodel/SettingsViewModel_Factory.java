package com.winstacker.app.viewmodel;

import com.winstacker.app.data.repository.UserPreferencesRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserPreferencesRepository> preferencesRepositoryProvider;

  public SettingsViewModel_Factory(
      Provider<UserPreferencesRepository> preferencesRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(preferencesRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<UserPreferencesRepository> preferencesRepositoryProvider) {
    return new SettingsViewModel_Factory(preferencesRepositoryProvider);
  }

  public static SettingsViewModel newInstance(UserPreferencesRepository preferencesRepository) {
    return new SettingsViewModel(preferencesRepository);
  }
}
