package com.winstacker.app.di;

import com.winstacker.app.data.db.StatsDao;
import com.winstacker.app.data.db.WinsDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideStatsDaoFactory implements Factory<StatsDao> {
  private final Provider<WinsDatabase> dbProvider;

  public DatabaseModule_ProvideStatsDaoFactory(Provider<WinsDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public StatsDao get() {
    return provideStatsDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideStatsDaoFactory create(Provider<WinsDatabase> dbProvider) {
    return new DatabaseModule_ProvideStatsDaoFactory(dbProvider);
  }

  public static StatsDao provideStatsDao(WinsDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideStatsDao(db));
  }
}
