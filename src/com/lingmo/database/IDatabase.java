package com.lingmo.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public interface IDatabase {
	/**
	 * 增加书签
	 * @param	sqLiteDatabase	数据库
	 * @param	name	书签名
	 * @param	url		书签地址
	 * @param   bitmap  网页图标
	 * */
	public boolean addFavorite(SQLiteDatabase sqLiteDatabase, String name, String url, Bitmap bitmap);
	
	/**
	 * 删除书签
	 * @param	sqLiteDatabase	数据库
	 * @param	id		书签ID
	 * */
	public boolean deleteFavorite(SQLiteDatabase sqLiteDatabase, String id);
	
	/**
	 * 修改书签
	 * @param	sqLiteDatabase	数据库
	 * @param	id		修改的书签ID
	 * @param	name	修改后的书签名
	 * @param	url		修改后的书签地址
	 * */
	public boolean modifyFavorite(SQLiteDatabase sqLiteDatabase, String id, String name, String url);
	
	/**
	 * 获取所有书签
	 * @param	sqLiteDatabase	数据库
	 * @return	HashMap<String, String>
	 * */
	public Cursor getAllFavorites(SQLiteDatabase sqLiteDatabase);
	
	/**
	 * 查询某个书签是否存在，即查询url是否重复
	 * @param	sqLiteDatabase	数据库
	 * @param	url		书签地址
	 * */
	public boolean multiplyFavorite(SQLiteDatabase sqLiteDatabase, String url);
	
	/**
	 * 开始事务
	 * @param	readOnly	是否只读
	 * @param	callback	函数回调
	 * */
	void transactionAround(boolean readOnly, CallBack callback);
	
}
