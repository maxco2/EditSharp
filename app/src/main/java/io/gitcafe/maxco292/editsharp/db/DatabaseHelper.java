package io.gitcafe.maxco292.editsharp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * 数据库助手类
 * 
 * @author zihao
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	public static final int VERSION = 1;// 版本号
	// private static final String DB_NAME = "ZiHao.db";// 数据库名称
	public static final String DB_NAME = SDBHelper.DB_DIR + File.separator
			+ "maxc.db";
	public static final String TABLE_NAME = "auto";// 表名

	/**
	 * DatabaseHelper构造方法
	 * 
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createAutoTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/**
	 * 创建数据库表
	 * 
	 * @param db
	 */
	private void createAutoTable(SQLiteDatabase db) {
		db.execSQL("create table  if not exists "
				+ TABLE_NAME
				+ "(id  INTEGER PRIMARY KEY AUTOINCREMENT,title varchar(255),pinyin varchar(255))");
	}

}