package io.gitcafe.maxco292.editsharp.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库管理类
 * 
 * @author zihao
 * 
 */
public class DatabaseManager {
	private AtomicInteger mAtomicInteger = new AtomicInteger();// 供原子操作的Integer的类,默认值为0
	private static DatabaseManager mDbManager;// 数据库管理器实例
	private static DatabaseHelper mDbHelper;// 数据库帮助类实例
	private SQLiteDatabase mDatabase;// SQLiteDatabase类，用于操作和管理数据库实例

	/**
	 * 初始化数据库管理类实例
	 * 
	 * @param helper
	 *            // 数据库帮助类对象
	 */
	public static synchronized void initializeInstance(DatabaseHelper helper) {// synchronized表示给这个方法加锁
		if (mDbManager == null) {
			mDbManager = new DatabaseManager();
			mDbHelper = helper;
		}
	}

	/**
	 * 获取数据库管理实例
	 * 
	 * @param helper
	 *            // 数据库帮助类对象
	 * @return
	 */
	public static synchronized DatabaseManager getInstance(DatabaseHelper helper) {
		if (mDbManager == null) {
			initializeInstance(helper);
		}
		return mDbManager;
	}

	/**
	 * 获取一个可写的数据库操作实例
	 * 
	 * @return
	 */
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (mAtomicInteger.incrementAndGet() == 1) {// 自增值为1的时候，允许执行
			// Opening new database
			mDatabase = mDbHelper.getWritableDatabase();
		}
		return mDatabase;
	}

	/**
	 * 获取一个可读的数据库操作实例
	 * 
	 * @return
	 */
	public synchronized SQLiteDatabase getReadableDatabase() {
		if (mAtomicInteger.incrementAndGet() == 1) {// 自增值为1的时候，允许执行
			// Opening new database
			mDatabase = mDbHelper.getReadableDatabase();
		}
		return mDatabase;
	}

	/**
	 * 关闭数据库
	 */
	public synchronized void closeDatabase() {
		if (mAtomicInteger.decrementAndGet() == 0) {// 自减值为0的时候，允许执行
			// Closing database
			mDatabase.close();
		}
	}
}
