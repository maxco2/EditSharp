package io.gitcafe.maxco292.editsharp.db;

import android.os.Environment;

import java.io.File;

public class SDBHelper {

public static final String DB_DIR = Environment
        .getExternalStorageDirectory().getPath()
        + File.separator
        + "maxcData"
        + File.separator
        + SDBHelper.class.getPackage().getName();
	static {
		while (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

		}
		File dbFolder = new File(DB_DIR);
		// 目录不存在则自动创建目录
		if (!dbFolder.exists()) {
			dbFolder.mkdirs();
		}
	}
}