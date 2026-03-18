package com.winstacker.app.di;

import com.winstacker.app.data.db.WinDao;
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
public final class DatabaseModule_ProvideWinDaoFactory implements Factory<WinDao> {
  private final Provider<WinsDatabase> dbProvider;

  public DatabaseModule_ProvideWinDaoFactory(Provider<WinsDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WinDao get() {
    return provideWinDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWinDaoFactory create(Provider<WinsDatabase> dbProvider) {
    return new DatabaseModule_ProvideWinDaoFactory(dbProvider);
  }

  public static WinDao provideWinDao(WinsDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWinDao(db));
  }
}
