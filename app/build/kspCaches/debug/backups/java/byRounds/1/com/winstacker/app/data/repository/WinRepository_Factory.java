package com.winstacker.app.data.repository;

import com.winstacker.app.data.db.StatsDao;
import com.winstacker.app.data.db.WinDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class WinRepository_Factory implements Factory<WinRepository> {
  private final Provider<WinDao> winDaoProvider;

  private final Provider<StatsDao> statsDaoProvider;

  public WinRepository_Factory(Provider<WinDao> winDaoProvider,
      Provider<StatsDao> statsDaoProvider) {
    this.winDaoProvider = winDaoProvider;
    this.statsDaoProvider = statsDaoProvider;
  }

  @Override
  public WinRepository get() {
    return newInstance(winDaoProvider.get(), statsDaoProvider.get());
  }

  public static WinRepository_Factory create(Provider<WinDao> winDaoProvider,
      Provider<StatsDao> statsDaoProvider) {
    return new WinRepository_Factory(winDaoProvider, statsDaoProvider);
  }

  public static WinRepository newInstance(WinDao winDao, StatsDao statsDao) {
    return new WinRepository(winDao, statsDao);
  }
}
