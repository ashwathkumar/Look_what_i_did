package com.winstacker.app;

import com.winstacker.app.data.repository.CategoryRepository;
import com.winstacker.app.data.repository.WinRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class WinsTrackerApplication_MembersInjector implements MembersInjector<WinsTrackerApplication> {
  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<WinRepository> winRepositoryProvider;

  public WinsTrackerApplication_MembersInjector(
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<WinRepository> winRepositoryProvider) {
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.winRepositoryProvider = winRepositoryProvider;
  }

  public static MembersInjector<WinsTrackerApplication> create(
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<WinRepository> winRepositoryProvider) {
    return new WinsTrackerApplication_MembersInjector(categoryRepositoryProvider, winRepositoryProvider);
  }

  @Override
  public void injectMembers(WinsTrackerApplication instance) {
    injectCategoryRepository(instance, categoryRepositoryProvider.get());
    injectWinRepository(instance, winRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.winstacker.app.WinsTrackerApplication.categoryRepository")
  public static void injectCategoryRepository(WinsTrackerApplication instance,
      CategoryRepository categoryRepository) {
    instance.categoryRepository = categoryRepository;
  }

  @InjectedFieldSignature("com.winstacker.app.WinsTrackerApplication.winRepository")
  public static void injectWinRepository(WinsTrackerApplication instance,
      WinRepository winRepository) {
    instance.winRepository = winRepository;
  }
}
