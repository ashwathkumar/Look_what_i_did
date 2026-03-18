package com.winstacker.app.viewmodel;

import android.content.Context;
import com.winstacker.app.data.repository.CategoryRepository;
import com.winstacker.app.data.repository.WinRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AddWinViewModel_Factory implements Factory<AddWinViewModel> {
  private final Provider<WinRepository> winRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<Context> contextProvider;

  public AddWinViewModel_Factory(Provider<WinRepository> winRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider, Provider<Context> contextProvider) {
    this.winRepositoryProvider = winRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public AddWinViewModel get() {
    return newInstance(winRepositoryProvider.get(), categoryRepositoryProvider.get(), contextProvider.get());
  }

  public static AddWinViewModel_Factory create(Provider<WinRepository> winRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider, Provider<Context> contextProvider) {
    return new AddWinViewModel_Factory(winRepositoryProvider, categoryRepositoryProvider, contextProvider);
  }

  public static AddWinViewModel newInstance(WinRepository winRepository,
      CategoryRepository categoryRepository, Context context) {
    return new AddWinViewModel(winRepository, categoryRepository, context);
  }
}
