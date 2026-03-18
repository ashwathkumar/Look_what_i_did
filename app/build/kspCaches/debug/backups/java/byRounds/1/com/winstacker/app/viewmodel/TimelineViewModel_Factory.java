package com.winstacker.app.viewmodel;

import com.winstacker.app.data.repository.CategoryRepository;
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
public final class TimelineViewModel_Factory implements Factory<TimelineViewModel> {
  private final Provider<WinRepository> winRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public TimelineViewModel_Factory(Provider<WinRepository> winRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.winRepositoryProvider = winRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public TimelineViewModel get() {
    return newInstance(winRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static TimelineViewModel_Factory create(Provider<WinRepository> winRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new TimelineViewModel_Factory(winRepositoryProvider, categoryRepositoryProvider);
  }

  public static TimelineViewModel newInstance(WinRepository winRepository,
      CategoryRepository categoryRepository) {
    return new TimelineViewModel(winRepository, categoryRepository);
  }
}
