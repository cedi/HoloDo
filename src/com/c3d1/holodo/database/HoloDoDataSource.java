
package com.c3d1.holodo.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.c3d1.holodo.datatypes.SortBy;
import com.c3d1.holodo.datatypes.Task;
import com.c3d1.holodo.datatypes.TaskList;

public class HoloDoDataSource
{
	// Task
	public static final int	TASK_COL_ID_IDX				= 0;
	public static final int	TASK_COL_TASK_NAME_IDX		= 1;
	public static final int	TASK_COL_LISTID_IDX			= 2;
	public static final int	TASK_COL_USERID_IDX			= 3;
	public static final int	TASK_COL_PLACE_IDX			= 4;
	public static final int	TASK_COL_DATE_IDX			= 5;
	public static final int	TASK_COL_REMEMBER_IDX		= 6;
	public static final int	TASK_COL_NOTE_IDX			= 7;
	public static final int	TASK_COL_PRIORITY_IDX		= 8;
	public static final int	TASK_COL_DONE_IDX			= 9;
	public static final int	TASK_COL_CREATE_TIME_IDX	= 10;
	public static final int	TASK_COL_MODIFY_TIME_IDX	= 11;
	private String[]		mAllTaskColumns				= { DatabaseHelper.COL_TASK_ID,
				DatabaseHelper.COL_TASK_TASK_NAME, DatabaseHelper.COL_TASK_LISTID,
				DatabaseHelper.COL_TASK_USERID, DatabaseHelper.COL_TASK_PLACE,
				DatabaseHelper.COL_TASK_DATE, DatabaseHelper.COL_TASK_REMEMBER,
				DatabaseHelper.COL_TASK_NOTE, DatabaseHelper.COL_TASK_PRIORITY,
				DatabaseHelper.COL_TASK_DONE, DatabaseHelper.COL_TASK_CREATE_TIME,
				DatabaseHelper.COL_TASK_MODIFY_TIME	};
	
	// Liste
	public static final int	LIST_COL_ID_IDX				= 0;
	public static final int	LIST_COL_USERID_IDX			= 1;
	public static final int	LIST_COL_NAME_IDX			= 2;
	public static final int	LIST_CREATE_TIME_NAME_IDX	= 3;
	public static final int	LIST_COL_MODIFY_TIME_IDX	= 4;
	private String[]		mAllListColumns				= new String[] {
				DatabaseHelper.COL_LIST_ID, DatabaseHelper.COL_LIST_USERID,
				DatabaseHelper.COL_LIST_LIST_NAME, DatabaseHelper.COL_LIST_CREATE_TIME,
				DatabaseHelper.COL_LIST_MODIFY_TIME	};
	
	// Database und Helper
	private SQLiteDatabase	mDatabase					= null;
	private DatabaseHelper	mDatabaseHelper				= null;
	
	/**
	 * Erstellt den DataSource
	 * 
	 * @param context der Applicationcontext
	 */
	public HoloDoDataSource( Context context )
	{
		mDatabaseHelper = new DatabaseHelper( context );
		mDatabase = mDatabaseHelper.getWritableDatabase();
	}
	
	public void closeConnections()
	{
		if( mDatabase != null && mDatabase.isOpen() )
		{
			mDatabase.close();
		}
	}
	
	/**
	 * erstellt einen Task in der Datenbank
	 * 
	 * @param task der Task der in die Tabelle eingesetzt werden soll
	 * @return die ID des erstellten Task
	 */
	public long addTask( Task task )
	{
		// Werte befüllen
		ContentValues values = new ContentValues();
		values.put( DatabaseHelper.COL_TASK_TASK_NAME, task.msTitle );
		values.put( DatabaseHelper.COL_TASK_LISTID, task.mlTaskListID );
		values.put( DatabaseHelper.COL_TASK_USERID, task.mlUserID );
		values.put( DatabaseHelper.COL_TASK_DATE, task.msDate );
		values.put( DatabaseHelper.COL_TASK_REMEMBER, task.msRemember );
		values.put( DatabaseHelper.COL_TASK_PLACE, task.msPlace );
		values.put( DatabaseHelper.COL_TASK_NOTE, task.msNotes );
		values.put( DatabaseHelper.COL_TASK_DONE, task.mbDone );
		values.put( DatabaseHelper.COL_TASK_CREATE_TIME, task.getCreateTime() );
		values.put( DatabaseHelper.COL_TASK_MODIFY_TIME, task.msModifyTime );
		
		// Database holen
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		// einfügen
		return mDatabase.insert( DatabaseHelper.TABLE_TASK_NAME, null, values );
	}
	
	/**
	 * erstellt einen Task in der Datenbank
	 * 
	 * @param sTaskName der Name der Datenbank
	 * @param lUserID der User dem der Task gehört
	 * @oaran lListiD die Liste zu dem der Task gehört
	 * @return die ID des erstellten Task
	 */
	public long addTask( String sTaskName, long lListID, long lUserID )
	{
		Task task = new Task( sTaskName, lListID );
		task.mlUserID = lUserID;
		
		// einfügen
		return addTask( task );
	}
	
	/**
	 * Einen bestehenden Task in der Datenbank updaten
	 * 
	 * @param task der zu Updatende Task
	 */
	public void updateTask( Task task )
	{
		task.updateTimestamp();
		
		ContentValues values = new ContentValues();
		values.put( DatabaseHelper.COL_TASK_ID, task.getID() );
		values.put( DatabaseHelper.COL_TASK_TASK_NAME, task.msTitle );
		values.put( DatabaseHelper.COL_TASK_DATE, task.msDate );
		values.put( DatabaseHelper.COL_TASK_DONE, task.mbDone );
		values.put( DatabaseHelper.COL_TASK_LISTID, task.mlTaskListID );
		values.put( DatabaseHelper.COL_TASK_NOTE, task.msNotes );
		values.put( DatabaseHelper.COL_TASK_PLACE, task.msPlace );
		values.put( DatabaseHelper.COL_TASK_PRIORITY, Task.enumToIntPriority( task.mePriority ) );
		values.put( DatabaseHelper.COL_TASK_REMEMBER, task.msRemember );
		values.put( DatabaseHelper.COL_TASK_DONE, task.mbDone );
		values.put( DatabaseHelper.COL_TASK_CREATE_TIME, task.getCreateTime() );
		values.put( DatabaseHelper.COL_TASK_MODIFY_TIME, task.msModifyTime );
		
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		mDatabase.update( DatabaseHelper.TABLE_TASK_NAME, values, DatabaseHelper.COL_TASK_ID + "="
					+ task.getID(), null );
	}
	
	/**
	 * Einen Task aus der Datenbank extrahieren
	 * 
	 * @param lTaskID die TaskID
	 * @return der Task aus der Datenbank
	 */
	public Task getTask( long lTaskID )
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		Cursor cursor = mDatabase.query( DatabaseHelper.TABLE_TASK_NAME, mAllTaskColumns,
					DatabaseHelper.COL_TASK_ID + "=" + lTaskID, null, null, null, null );
		
		// das erste Ergebnis holen
		cursor.moveToFirst();
		
		if( cursor.getCount() == 0 )
		{
			Log.e( "TaskDB.getTask",
						"cursor ohne Ergebnis. Bitte sicherstellen dass nur ein Eintrag im Cursor existiert" );
			
			return null;
		}
		
		if( cursor.getCount() > 1 )
		{
			Log.e( "TaskDB.getTask",
						"cursor mit mehreren Ergebnisen. Bitte sicherstellen dass nur ein Ergebnis im Cursor existiert" );
		}
		
		// das erste Ergebnis holen
		cursor.moveToFirst();
		
		return new Task( cursor.getLong( TASK_COL_ID_IDX ),
					cursor.getString( TASK_COL_TASK_NAME_IDX ),
					cursor.getLong( TASK_COL_LISTID_IDX ), TASK_COL_USERID_IDX,
					cursor.getString( TASK_COL_NOTE_IDX ), cursor.getString( TASK_COL_DATE_IDX ),
					cursor.getString( TASK_COL_REMEMBER_IDX ),
					cursor.getString( TASK_COL_PLACE_IDX ), cursor.getInt( TASK_COL_PRIORITY_IDX ),
					cursor.getInt( TASK_COL_DONE_IDX ),
					cursor.getString( TASK_COL_CREATE_TIME_IDX ),
					cursor.getString( TASK_COL_MODIFY_TIME_IDX ) );
	}
	
	/**
	 * Löscht einen Task aus der Liste
	 * 
	 * @param lTaskID die ID
	 * @return die anzahl der gelöschten zeilen
	 */
	public int deleteTask( long lTaskID )
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		return mDatabase.delete( DatabaseHelper.TABLE_TASK_NAME, DatabaseHelper.COL_TASK_ID + "=?",
					new String[] { "" + lTaskID } );
	}
	
	/**
	 * Ruft alle Tasks einer zugehörigen Taskliste ab
	 * 
	 * @param lTaskListID die ID der Taskliste
	 * @return Cursor mit den Ergebnissen
	 */
	public Cursor getTasks( long lTaskListID, SortBy eSortBy )
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		String sOrderBy = null;
		
		switch( eSortBy )
		{
			case NAME_ASCEND:
				sOrderBy = DatabaseHelper.COL_TASK_TASK_NAME + " ASC";
				break;
			
			case NAME_DESCEND:
				sOrderBy = DatabaseHelper.COL_TASK_TASK_NAME + " DESC";
				break;
			
			case NONE:
				sOrderBy = DatabaseHelper.COL_TASK_ID + " ASC";
				break;
		}
		
		return mDatabase.query( DatabaseHelper.TABLE_TASK_NAME, mAllTaskColumns,
					DatabaseHelper.COL_TASK_LISTID + "=" + lTaskListID, null, null, null, sOrderBy );
	}
	
	/**
	 * Alle Tasks als ArrayList zurück holen
	 * 
	 * @param lTasklistID die TaskListe der zurück zu gebenden Tasks
	 * @return ArrayList<Task>
	 */
	public ArrayList<Task> getTasksAsList( long lTasklistID, SortBy eSortBy )
	{
		ArrayList<Task> listTasks = new ArrayList<Task>();
		
		Cursor cursor = getTasks( lTasklistID, eSortBy );
		
		if( cursor.moveToFirst() )
		{
			do
			{
				Task task = new Task( cursor.getLong( TASK_COL_ID_IDX ),
							cursor.getString( TASK_COL_TASK_NAME_IDX ),
							cursor.getLong( TASK_COL_LISTID_IDX ),
							cursor.getLong( TASK_COL_USERID_IDX ),
							cursor.getString( TASK_COL_NOTE_IDX ),
							cursor.getString( TASK_COL_DATE_IDX ),
							cursor.getString( TASK_COL_REMEMBER_IDX ),
							cursor.getString( TASK_COL_PLACE_IDX ),
							cursor.getInt( TASK_COL_PRIORITY_IDX ),
							cursor.getInt( TASK_COL_DONE_IDX ),
							cursor.getString( TASK_COL_CREATE_TIME_IDX ),
							cursor.getString( TASK_COL_MODIFY_TIME_IDX ) );
				
				if( task != null )
				{
					listTasks.add( task );
				}
			}
			while( cursor.moveToNext() );
		}
		
		return listTasks;
	}
	
	/**
	 * Eine TaskListe hinzufügen
	 * 
	 * @param sListName der Name der TaskListe
	 * @param lUserID der Benuter der Taskliste
	 * @return die ID der Taskliste
	 */
	public long addList( String sListName, long lUserID )
	{
		TaskList list = new TaskList( sListName );
		list.mlUserID = lUserID;
		
		return addList( list );
	}
	
	public long addList( TaskList list )
	{
		ContentValues values = new ContentValues();
		values.put( DatabaseHelper.COL_LIST_LIST_NAME, list.msListName );
		values.put( DatabaseHelper.COL_LIST_USERID, list.mlUserID );
		values.put( DatabaseHelper.COL_LIST_CREATE_TIME, list.getCreateTime() );
		values.put( DatabaseHelper.COL_LIST_MODIFY_TIME, list.msModifyTime );
		
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		// einfügen
		return mDatabase.insert( DatabaseHelper.TABLE_LIST_NAME, null, values );
	}
	
	/**
	 * Eine TaskListe aus der Datenbank holen
	 * 
	 * @param lTaskListID die ID der Taskliste
	 * @return die Taskliste aud der Datenbank
	 */
	public TaskList getTaskList( long lTaskListID )
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		// und das Element holen
		Cursor cursor = mDatabase.query( DatabaseHelper.TABLE_LIST_NAME, mAllListColumns,
					DatabaseHelper.COL_LIST_ID + "=" + lTaskListID, null, null, null, null );
		
		if( cursor.getCount() == 0 )
		{
			Log.e( "ListDB.getTaskList",
						"cursor ohne Ergebnis. Bitte sicherstellen dass nur ein Eintrag im Cursor existiert" );
			
			return null;
		}
		
		if( cursor.getCount() > 1 )
		{
			Log.e( "ListDB.getTaskList",
						"cursor mit mehreren Ergebnisen. Bitte sicherstellen dass nur ein Ergebnis im Cursor existiert" );
		}
		
		// das erste Ergebnis holen
		cursor.moveToFirst();
		
		// und die TaskListe zurück geben
		return new TaskList( cursor.getLong( LIST_COL_ID_IDX ),
					cursor.getInt( LIST_COL_USERID_IDX ), cursor.getString( LIST_COL_NAME_IDX ),
					cursor.getString( LIST_CREATE_TIME_NAME_IDX ),
					cursor.getString( LIST_COL_MODIFY_TIME_IDX ) );
	}
	
	/**
	 * Löscht eine TaskListe aus Tabelle
	 * 
	 * @param lTaskListID die ID der TaskListe
	 * @return die anzahl der gelöschten zeilen
	 */
	public TaskList deleteList( long lTaskListID )
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		TaskList nextList = null;
		
		// die darauf folgende Liste holen
		{
			Cursor cursor = mDatabase.rawQuery( "SELECT * FROM " + DatabaseHelper.TABLE_LIST_NAME
						+ " WHERE " + DatabaseHelper.COL_LIST_ID + " > "
						+ DatabaseHelper.DEFAULT_ENTRY_LIST, null );
			
			if( cursor.moveToFirst() )
			{
				nextList = new TaskList( cursor.getLong( LIST_COL_ID_IDX ),
							cursor.getInt( LIST_COL_USERID_IDX ),
							cursor.getString( LIST_COL_NAME_IDX ),
							cursor.getString( LIST_CREATE_TIME_NAME_IDX ),
							cursor.getString( LIST_COL_MODIFY_TIME_IDX ) );
			}
		}
		
		int nRows = mDatabase.delete( DatabaseHelper.TABLE_LIST_NAME, DatabaseHelper.COL_LIST_ID
					+ "=?", new String[] { "" + lTaskListID } );
		
		if( nRows == 0 )
		{
			Log.e( "HoloDoDataSource.deleteList", "keine Zeilen gelöscht" );
		}
		
		ContentValues values = new ContentValues();
		values.put( DatabaseHelper.COL_TASK_LISTID, DatabaseHelper.DEFAULT_ENTRY_LIST );
		
		mDatabase.update( DatabaseHelper.TABLE_TASK_NAME, values, DatabaseHelper.COL_TASK_LISTID
					+ "=?", new String[] { "" + lTaskListID } );
		
		return nextList;
	}
	
	/**
	 * Gibt alle TaskListen zurück
	 * 
	 * @return der Cursor mit den Ergebnisen
	 */
	public Cursor getTaskLists()
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		return mDatabase.query( DatabaseHelper.TABLE_LIST_NAME, mAllListColumns, null, null, null,
					null, DatabaseHelper.COL_LIST_ID );
	}
	
	/**
	 * Gibt alle TaskListen als ArrayList zurück
	 * 
	 * @return ArrayList<TaskList>
	 */
	public ArrayList<TaskList> getTaskListsAsList()
	{
		ArrayList<TaskList> listTaskList = new ArrayList<TaskList>();
		
		Cursor cursor = getTaskLists();
		
		if( cursor.moveToFirst() )
		{
			do
			{
				TaskList list = new TaskList( cursor.getLong( LIST_COL_ID_IDX ),
							cursor.getInt( LIST_COL_USERID_IDX ),
							cursor.getString( LIST_COL_NAME_IDX ),
							cursor.getString( LIST_CREATE_TIME_NAME_IDX ),
							cursor.getString( LIST_COL_MODIFY_TIME_IDX ) );
				
				if( list != null )
				{
					listTaskList.add( list );
				}
			}
			while( cursor.moveToNext() );
		}
		
		return listTaskList;
	}
	
	public String getTaskListCount( long lTaskListId )
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		Cursor cursor = mDatabase.rawQuery( "SELECT count(*) AS MYCOUNT from "
					+ DatabaseHelper.TABLE_TASK_NAME + " where " + DatabaseHelper.COL_TASK_LISTID
					+ " =?", new String[] { "" + lTaskListId } );
		
		if( cursor.getCount() > 1 || cursor.getCount() < 1 )
		{
			Log.e( "HoloDoDataSource.getTaskListCount", "ungültiger cursor" );
		}
		
		cursor.moveToFirst();
		
		return cursor.getString( 0 );
	}
	
	/**
	 * gibt den Cursor zurück indem alle Tasks einer bestimmten Zeit liegen
	 * 
	 * @param sTimeString
	 * @return
	 */
	private Cursor getTasksFromTime( String sTimeString )
	{
		if( mDatabase == null || !mDatabase.isOpen() )
		{
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		
		Cursor cursor = mDatabase.query( DatabaseHelper.TABLE_TASK_NAME, mAllTaskColumns,
					DatabaseHelper.COL_TASK_REMEMBER + " = '" + sTimeString + "'", null, null, null, null );
		return cursor;
	}
	
	/**
	 * Gibt eine ArrayListe zurück mit allen Tasks einer bestimmten Zeit
	 * 
	 * @return
	 */
	public ArrayList<Task> getTasksFromTimeAsList( String sTimeString )
	{
		ArrayList<Task> list = new ArrayList<Task>();
		
		Cursor cursor = getTasksFromTime( sTimeString );
		
		if( cursor.moveToFirst() )
		{
			do
			{
				Task task = new Task( cursor.getLong( TASK_COL_ID_IDX ),
							cursor.getString( TASK_COL_TASK_NAME_IDX ),
							cursor.getLong( TASK_COL_LISTID_IDX ),
							cursor.getLong( TASK_COL_USERID_IDX ),
							cursor.getString( TASK_COL_NOTE_IDX ),
							cursor.getString( TASK_COL_DATE_IDX ),
							cursor.getString( TASK_COL_REMEMBER_IDX ),
							cursor.getString( TASK_COL_PLACE_IDX ),
							cursor.getInt( TASK_COL_PRIORITY_IDX ),
							cursor.getInt( TASK_COL_DONE_IDX ),
							cursor.getString( TASK_COL_CREATE_TIME_IDX ),
							cursor.getString( TASK_COL_MODIFY_TIME_IDX ) );
				
				if( task != null )
				{
					list.add( task );
				}
			}
			while( cursor.moveToNext() );
		}
		
		return list;
	}
}
