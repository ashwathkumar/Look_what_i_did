package com.winstacker.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.winstacker.app.data.model.Category;
import com.winstacker.app.data.model.CategoryCount;
import com.winstacker.app.data.model.Win;
import com.winstacker.app.data.model.WinSource;
import com.winstacker.app.data.model.WinWithCategory;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WinDao_Impl implements WinDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Win> __insertionAdapterOfWin;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Win> __deletionAdapterOfWin;

  private final EntityDeletionOrUpdateAdapter<Win> __updateAdapterOfWin;

  private final SharedSQLiteStatement __preparedStmtOfPruneOldWins;

  public WinDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWin = new EntityInsertionAdapter<Win>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `wins` (`id`,`title`,`notes`,`categoryId`,`dateEpochDay`,`createdAtMs`,`updatedAtMs`,`source`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Win entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getNotes());
        if (entity.getCategoryId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCategoryId());
        }
        statement.bindLong(5, entity.getDateEpochDay());
        statement.bindLong(6, entity.getCreatedAtMs());
        statement.bindLong(7, entity.getUpdatedAtMs());
        final String _tmp = __converters.fromWinSource(entity.getSource());
        statement.bindString(8, _tmp);
      }
    };
    this.__deletionAdapterOfWin = new EntityDeletionOrUpdateAdapter<Win>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `wins` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Win entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWin = new EntityDeletionOrUpdateAdapter<Win>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `wins` SET `id` = ?,`title` = ?,`notes` = ?,`categoryId` = ?,`dateEpochDay` = ?,`createdAtMs` = ?,`updatedAtMs` = ?,`source` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Win entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getNotes());
        if (entity.getCategoryId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCategoryId());
        }
        statement.bindLong(5, entity.getDateEpochDay());
        statement.bindLong(6, entity.getCreatedAtMs());
        statement.bindLong(7, entity.getUpdatedAtMs());
        final String _tmp = __converters.fromWinSource(entity.getSource());
        statement.bindString(8, _tmp);
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfPruneOldWins = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM wins WHERE dateEpochDay < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertWin(final Win win, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfWin.insertAndReturnId(win);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWin(final Win win, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWin.handle(win);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWin(final Win win, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWin.handle(win);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object pruneOldWins(final long cutoffDay, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPruneOldWins.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffDay);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfPruneOldWins.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getWinById(final long id, final Continuation<? super Win> $completion) {
    final String _sql = "SELECT * FROM wins WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Win>() {
      @Override
      @Nullable
      public Win call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfDateEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEpochDay");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final Win _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final long _tmpDateEpochDay;
            _tmpDateEpochDay = _cursor.getLong(_cursorIndexOfDateEpochDay);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final long _tmpUpdatedAtMs;
            _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
            final WinSource _tmpSource;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toWinSource(_tmp);
            _result = new Win(_tmpId,_tmpTitle,_tmpNotes,_tmpCategoryId,_tmpDateEpochDay,_tmpCreatedAtMs,_tmpUpdatedAtMs,_tmpSource);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WinWithCategory>> getWinsInRange(final long startDay, final long endDay) {
    final String _sql = "\n"
            + "        SELECT * FROM wins\n"
            + "        WHERE dateEpochDay BETWEEN ? AND ?\n"
            + "        ORDER BY dateEpochDay DESC, createdAtMs DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "wins"}, new Callable<List<WinWithCategory>>() {
      @Override
      @NonNull
      public List<WinWithCategory> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
            final int _cursorIndexOfDateEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEpochDay");
            final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
            final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
            final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
            final LongSparseArray<Category> _collectionCategory = new LongSparseArray<Category>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomWinstackerAppDataModelCategory(_collectionCategory);
            final List<WinWithCategory> _result = new ArrayList<WinWithCategory>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final WinWithCategory _item;
              final Win _tmpWin;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpNotes;
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final long _tmpDateEpochDay;
              _tmpDateEpochDay = _cursor.getLong(_cursorIndexOfDateEpochDay);
              final long _tmpCreatedAtMs;
              _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
              final long _tmpUpdatedAtMs;
              _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
              final WinSource _tmpSource;
              final String _tmp;
              _tmp = _cursor.getString(_cursorIndexOfSource);
              _tmpSource = __converters.toWinSource(_tmp);
              _tmpWin = new Win(_tmpId,_tmpTitle,_tmpNotes,_tmpCategoryId,_tmpDateEpochDay,_tmpCreatedAtMs,_tmpUpdatedAtMs,_tmpSource);
              final Category _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _item = new WinWithCategory(_tmpWin,_tmpCategory);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<WinWithCategory>> getWinsInRangeByCategory(final long startDay,
      final long endDay, final long categoryId) {
    final String _sql = "\n"
            + "        SELECT * FROM wins\n"
            + "        WHERE dateEpochDay BETWEEN ? AND ?\n"
            + "          AND categoryId = ?\n"
            + "        ORDER BY dateEpochDay DESC, createdAtMs DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    _argIndex = 3;
    _statement.bindLong(_argIndex, categoryId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "wins"}, new Callable<List<WinWithCategory>>() {
      @Override
      @NonNull
      public List<WinWithCategory> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
            final int _cursorIndexOfDateEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEpochDay");
            final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
            final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
            final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
            final LongSparseArray<Category> _collectionCategory = new LongSparseArray<Category>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomWinstackerAppDataModelCategory(_collectionCategory);
            final List<WinWithCategory> _result = new ArrayList<WinWithCategory>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final WinWithCategory _item;
              final Win _tmpWin;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpNotes;
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final long _tmpDateEpochDay;
              _tmpDateEpochDay = _cursor.getLong(_cursorIndexOfDateEpochDay);
              final long _tmpCreatedAtMs;
              _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
              final long _tmpUpdatedAtMs;
              _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
              final WinSource _tmpSource;
              final String _tmp;
              _tmp = _cursor.getString(_cursorIndexOfSource);
              _tmpSource = __converters.toWinSource(_tmp);
              _tmpWin = new Win(_tmpId,_tmpTitle,_tmpNotes,_tmpCategoryId,_tmpDateEpochDay,_tmpCreatedAtMs,_tmpUpdatedAtMs,_tmpSource);
              final Category _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _item = new WinWithCategory(_tmpWin,_tmpCategory);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<WinWithCategory>> getWinsInRangeForOther(final long startDay, final long endDay,
      final Set<Long> presetIds) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM wins");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE dateEpochDay BETWEEN ");
    _stringBuilder.append("?");
    _stringBuilder.append(" AND ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("          AND (categoryId IS NULL OR categoryId NOT IN (");
    final int _inputSize = presetIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY dateEpochDay DESC, createdAtMs DESC");
    _stringBuilder.append("\n");
    _stringBuilder.append("    ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 2 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    _argIndex = 3;
    for (long _item : presetIds) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    return CoroutinesRoom.createFlow(__db, true, new String[] {"categories",
        "wins"}, new Callable<List<WinWithCategory>>() {
      @Override
      @NonNull
      public List<WinWithCategory> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
            final int _cursorIndexOfDateEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dateEpochDay");
            final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
            final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
            final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
            final LongSparseArray<Category> _collectionCategory = new LongSparseArray<Category>();
            while (_cursor.moveToNext()) {
              final Long _tmpKey;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey != null) {
                _collectionCategory.put(_tmpKey, null);
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcategoriesAscomWinstackerAppDataModelCategory(_collectionCategory);
            final List<WinWithCategory> _result = new ArrayList<WinWithCategory>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final WinWithCategory _item_1;
              final Win _tmpWin;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final String _tmpNotes;
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              final Long _tmpCategoryId;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpCategoryId = null;
              } else {
                _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              final long _tmpDateEpochDay;
              _tmpDateEpochDay = _cursor.getLong(_cursorIndexOfDateEpochDay);
              final long _tmpCreatedAtMs;
              _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
              final long _tmpUpdatedAtMs;
              _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
              final WinSource _tmpSource;
              final String _tmp;
              _tmp = _cursor.getString(_cursorIndexOfSource);
              _tmpSource = __converters.toWinSource(_tmp);
              _tmpWin = new Win(_tmpId,_tmpTitle,_tmpNotes,_tmpCategoryId,_tmpDateEpochDay,_tmpCreatedAtMs,_tmpUpdatedAtMs,_tmpSource);
              final Category _tmpCategory;
              final Long _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfCategoryId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getLong(_cursorIndexOfCategoryId);
              }
              if (_tmpKey_1 != null) {
                _tmpCategory = _collectionCategory.get(_tmpKey_1);
              } else {
                _tmpCategory = null;
              }
              _item_1 = new WinWithCategory(_tmpWin,_tmpCategory);
              _result.add(_item_1);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CategoryCount>> getCategoryCountsInRange(final long startDay,
      final long endDay) {
    final String _sql = "\n"
            + "        SELECT categoryId, COUNT(*) as count\n"
            + "        FROM wins\n"
            + "        WHERE dateEpochDay BETWEEN ? AND ?\n"
            + "        GROUP BY categoryId\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"wins"}, new Callable<List<CategoryCount>>() {
      @Override
      @NonNull
      public List<CategoryCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfCount = 1;
          final List<CategoryCount> _result = new ArrayList<CategoryCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CategoryCount _item;
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new CategoryCount(_tmpCategoryId,_tmpCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getWinCountForDay(final long day) {
    final String _sql = "SELECT COUNT(*) FROM wins WHERE dateEpochDay = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, day);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"wins"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getWinCountForDaySuspend(final long day,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM wins WHERE dateEpochDay = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, day);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllDistinctDays(final Continuation<? super List<Long>> $completion) {
    final String _sql = "SELECT DISTINCT dateEpochDay FROM wins ORDER BY dateEpochDay DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Long> _result = new ArrayList<Long>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Long _item;
            _item = _cursor.getLong(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalWinsInRange(final long startDay, final long endDay,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM wins WHERE dateEpochDay BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTopCategoryInRange(final long startDay, final long endDay,
      final Continuation<? super CategoryCount> $completion) {
    final String _sql = "\n"
            + "        SELECT categoryId, COUNT(*) as count\n"
            + "        FROM wins\n"
            + "        WHERE dateEpochDay BETWEEN ? AND ?\n"
            + "        GROUP BY categoryId\n"
            + "        ORDER BY count DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategoryCount>() {
      @Override
      @Nullable
      public CategoryCount call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfCount = 1;
          final CategoryCount _result;
          if (_cursor.moveToFirst()) {
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _result = new CategoryCount(_tmpCategoryId,_tmpCount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMostActiveDayOfWeekInRange(final long startDay, final long endDay,
      final Continuation<? super CategoryCount> $completion) {
    final String _sql = "\n"
            + "        SELECT (dateEpochDay % 7 + 3) % 7 + 1 AS categoryId,\n"
            + "               COUNT(*) AS count\n"
            + "        FROM wins\n"
            + "        WHERE dateEpochDay BETWEEN ? AND ?\n"
            + "        GROUP BY categoryId\n"
            + "        ORDER BY count DESC\n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CategoryCount>() {
      @Override
      @Nullable
      public CategoryCount call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCategoryId = 0;
          final int _cursorIndexOfCount = 1;
          final CategoryCount _result;
          if (_cursor.moveToFirst()) {
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _result = new CategoryCount(_tmpCategoryId,_tmpCount);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipcategoriesAscomWinstackerAppDataModelCategory(
      @NonNull final LongSparseArray<Category> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipcategoriesAscomWinstackerAppDataModelCategory(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`name`,`emoji`,`colorHex`,`isPreset`,`sortOrder` FROM `categories` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfName = 1;
      final int _cursorIndexOfEmoji = 2;
      final int _cursorIndexOfColorHex = 3;
      final int _cursorIndexOfIsPreset = 4;
      final int _cursorIndexOfSortOrder = 5;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final Category _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          final String _tmpEmoji;
          _tmpEmoji = _cursor.getString(_cursorIndexOfEmoji);
          final String _tmpColorHex;
          _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
          final boolean _tmpIsPreset;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfIsPreset);
          _tmpIsPreset = _tmp != 0;
          final int _tmpSortOrder;
          _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder);
          _item_1 = new Category(_tmpId,_tmpName,_tmpEmoji,_tmpColorHex,_tmpIsPreset,_tmpSortOrder);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
