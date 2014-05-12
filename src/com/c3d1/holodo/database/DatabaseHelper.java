package com.c3d1.holodo.database;

import com.c3d1.holodo.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	public static final String	DATABASE_NAME			= "HoloDoDatabase";
	private static final int	DATABASE_VERSION		= 2;

	public static final String	TABLE_TASK_NAME			= "Tasks";
	public static final String	COL_TASK_ID				= "task_id";
	public static final String	COL_TASK_DONE			= "task_done";
	public static final String	COL_TASK_TASK_NAME		= "task_name";
	public static final String	COL_TASK_USERID			= "user_id";
	public static final String	COL_TASK_LISTID			= "list_id";
	public static final String	COL_TASK_PLACE			= "place";
	public static final String	COL_TASK_DATE			= "date";
	public static final String	COL_TASK_REMEMBER		= "remember";
	public static final String	COL_TASK_NOTE			= "note";
	public static final String	COL_TASK_PRIORITY		= "prority";
	public static final String	COL_TASK_CREATE_TIME	= "created";
	public static final String	COL_TASK_MODIFY_TIME	= "modified";

	public static final String	TABLE_LIST_NAME			= "Lists";
	public static final String	COL_LIST_ID				= "list_id";
	public static final String	COL_LIST_USERID			= "user_id";
	public static final String	COL_LIST_LIST_NAME		= "task_name";
	public static final String	COL_LIST_CREATE_TIME	= "created";
	public static final String	COL_LIST_MODIFY_TIME	= "modified";

	private static final String	DATABASE_TASK_CREATE	= "CREATE TABLE " + TABLE_TASK_NAME + " ( "
																+ COL_TASK_ID
																+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
																+ COL_TASK_TASK_NAME + " TEXT NOT NULL, "
																+ COL_TASK_DONE + " INTEGER,"
																+ COL_TASK_USERID + " INTEGER NOT NULL, "
																+ COL_TASK_LISTID + " INTEGER NOT NULL, "
																+ COL_TASK_PLACE + " TEXT, "
																+ COL_TASK_DATE + " TEXT, "
																+ COL_TASK_REMEMBER + " TEXT, "
																+ COL_TASK_NOTE + " TEXT, "
																+ COL_TASK_PRIORITY + " INTEGER, "
																+ COL_TASK_CREATE_TIME + " TEXT, "
																+ COL_TASK_MODIFY_TIME + " TEXT " + ")";

	private static final String	DATABASE_LIST_CREATE	= "CREATE TABLE " + TABLE_LIST_NAME + " ( "
																+ COL_LIST_ID
																+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
																+ COL_LIST_USERID + " INTEGER NOT NULL, "
																+ COL_LIST_LIST_NAME + " TEXT NOT NULL, "
																+ COL_LIST_CREATE_TIME + " TEXT, "
																+ COL_LIST_MODIFY_TIME + " TEXT " + " ) ";

	public static long			DEFAULT_ENTRY_LIST;
	private Context				mMyContext;

	/**
	 * Das TasksDB Objekt erstellen
	 * 
	 * @param context
	 *            der Context der Anwendung
	 */
	public DatabaseHelper( Context context )
	{
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
		mMyContext = context;
	}

	/**
	 * Die Datenbankerstellen
	 * 
	 * @param db
	 *            die Datenbank
	 */
	@Override
	public void onCreate( SQLiteDatabase db )
	{
		db.execSQL( DATABASE_TASK_CREATE );
		db.execSQL( DATABASE_LIST_CREATE );

		// Default Listen dazu fügen
		// Werte befüllen
		ContentValues values = new ContentValues();

		values.put( COL_LIST_LIST_NAME, mMyContext.getString( R.string.liste_entry ) );
		values.put( COL_LIST_USERID, 0 );

		DEFAULT_ENTRY_LIST = db.insert( DatabaseHelper.TABLE_LIST_NAME, null, values );
	}

	/**
	 * Die Datenbank Upgraden
	 * 
	 * @param db
	 *            die Datenbank
	 * @param oldVersion
	 *            die alte Datenbankversion
	 * @param newVersion
	 *            die neue Datenbankversion
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
	{
		Log.w( SQLiteOpenHelper.class.getName(), "Upgraiding database " + DATABASE_NAME + "from Version "
				+ oldVersion + " to " + newVersion + ". Old Data will be destroyed" );

		db.execSQL( "DROP TABLE IF EXISTS " + TABLE_LIST_NAME );
		db.execSQL( "DROP TABLE IF EXISTS " + TABLE_TASK_NAME );
		onCreate( db );
	}
}
