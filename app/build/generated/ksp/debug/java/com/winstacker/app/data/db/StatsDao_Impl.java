package com.winstacker.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.winstacker.app.data.model.AggregatedStats;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StatsDao_Impl implements StatsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AggregatedStats> __insertionAdapterOfAggregatedStats;

  public StatsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAggregatedStats = new EntityInsertionAdapter<AggregatedStats>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `aggregated_stats` (`yearMonth`,`categoryId`,`winCount`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AggregatedStats entity) {
        statement.bindString(1, entity.getYearMonth());
        if (entity.getCategoryId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCategoryId());
        }
        statement.bindLong(3, entity.getWinCount());
      }
    };
  }

  @Override
  public Object upsert(final AggregatedStats stats, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAggregatedStats.insert(stats);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getStatsForYear(final String yearPrefix,
      final Continuation<? super List<AggregatedStats>> $completion) {
    final String _sql = "SELECT * FROM aggregated_stats WHERE yearMonth LIKE ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearPrefix);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AggregatedStats>>() {
      @Override
      @NonNull
      public List<AggregatedStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "yearMonth");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfWinCount = CursorUtil.getColumnIndexOrThrow(_cursor, "winCount");
          final List<AggregatedStats> _result = new ArrayList<AggregatedStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AggregatedStats _item;
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final int _tmpWinCount;
            _tmpWinCount = _cursor.getInt(_cursorIndexOfWinCount);
            _item = new AggregatedStats(_tmpYearMonth,_tmpCategoryId,_tmpWinCount);
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
  public Object getTotalForMonth(final String yearMonth,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT SUM(winCount) FROM aggregated_stats WHERE yearMonth = ? AND categoryId IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public Flow<List<AggregatedStats>> getAllStats() {
    final String _sql = "SELECT * FROM aggregated_stats ORDER BY yearMonth ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"aggregated_stats"}, new Callable<List<AggregatedStats>>() {
      @Override
      @NonNull
      public List<AggregatedStats> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "yearMonth");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfWinCount = CursorUtil.getColumnIndexOrThrow(_cursor, "winCount");
          final List<AggregatedStats> _result = new ArrayList<AggregatedStats>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AggregatedStats _item;
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final int _tmpWinCount;
            _tmpWinCount = _cursor.getInt(_cursorIndexOfWinCount);
            _item = new AggregatedStats(_tmpYearMonth,_tmpCategoryId,_tmpWinCount);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
