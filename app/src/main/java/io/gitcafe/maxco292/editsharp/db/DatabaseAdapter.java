package io.gitcafe.maxco292.editsharp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作类
 * 
 * @author zihao
 * 
 */
public class DatabaseAdapter {

	private static DatabaseManager manager;
	private static Context mContext;

	/**
	 * 获取一个操作类对象
	 * 
	 * @param context
	 * @return
	 */
	public static DatabaseAdapter getIntance(Context context) {
		DatabaseAdapter adapter = new DatabaseAdapter();
		mContext = context;
		manager = DatabaseManager.getInstance(new DatabaseHelper(mContext));
		return adapter;
	}

	/**
	 * 插入信息
	 * 
	 * @param titleArray
	 */
	public void inserInfo(List<String> titleArray) {
		SQLiteDatabase database = manager.getWritableDatabase();

		try {
			for (String title : titleArray) {
				ContentValues values = new ContentValues();
				values.put("title", title);
				values.put("pinyin", title);// 讲内容转换为拼音
				database.insert(DatabaseHelper.TABLE_NAME, null, values);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			manager.closeDatabase();
		}
	}
	public void inserInfo(String title)
    {
        SQLiteDatabase database = manager.getWritableDatabase();

        try {
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("pinyin", title);// 讲内容转换为拼音
                database.insert(DatabaseHelper.TABLE_NAME, null, values);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            manager.closeDatabase();
        }
    }
	/**
	 * 查询信息
	 * 
	 * @param pinyin
	 *            // 字符串转换的拼音
	 * @return
	 */
	public List<String> queryInfo(String pinyin) {
		List<String> resultArray = new ArrayList<String>();
		SQLiteDatabase database = manager.getReadableDatabase();
		Cursor cursor = null;

		try {
			// 创建模糊查询的条件
			String likeStr = "'";
			for (int i = 0; i < pinyin.length(); i++) {
				if (i < pinyin.length() - 1) {
					likeStr += "%" + pinyin.charAt(i);
				} else {
					likeStr += "%" + pinyin.charAt(i) + "%'";
				}
			}

			cursor = database.rawQuery("select * from "
					+ DatabaseHelper.TABLE_NAME + " where pinyin like "
					+ likeStr, null);

			while (cursor.moveToNext()) {
				resultArray
						.add(cursor.getString(cursor.getColumnIndex("title")));
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.toString();
		} finally {
			manager.closeDatabase();
		}

		return resultArray;
	}

	/**
	 * 删除表中的所有数据
	 */
	public void deleteAll() {
		SQLiteDatabase database = manager.getWritableDatabase();

		try {
			database.delete(DatabaseHelper.TABLE_NAME, null, null);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			manager.closeDatabase();
		}
	}
}