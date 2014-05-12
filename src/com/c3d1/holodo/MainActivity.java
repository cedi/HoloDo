
package com.c3d1.holodo;

// Java Imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.c3d1.holodo.datatypes.SortBy;
// HoloDo Imports
import com.c3d1.holodo.datatypes.Task;
import com.c3d1.holodo.datatypes.TaskList;
import com.c3d1.holodo.datatypes.TaskPriority;
import com.c3d1.holodo.database.DatabaseHelper;
import com.c3d1.holodo.database.HoloDoDataSource;
import com.c3d1.holodo.reminder.Reminder;

// Android Imprts
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Paint;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TimePicker;

public class MainActivity extends Activity
{
	// ****************************************************************************
	// Attribute
	
	// Drawer allgemein
	private DrawerLayout						mDrawerLayout;
	private ActionBarDrawerToggle				mDrawerToogle;
	
	// Die DetailDrawer Elemente
	private EditText							mEditTitle;
	private EditText							mEditNote;
	private EditText							mEditPlace;
	private Button								mBtnDate;
	private Button								mBtnRemember;
	private Button								mBtnTitle;
	private Button								mBtnFav;
	private CheckBox							mChboxDone;
	
	// Der MenueDrawer
	private EditText							mEditAddList;
	private ListView							mlvMenu;
	private List<TaskList>						mlMenuList;
	private TaskListAdapter						mlMenuListAdapter;
	private boolean								bMenuOpened					= false;
	
	// Der MainView
	EditText									mEditAddTask;
	private ListView							mlvTasklist;
	private List<Task>							mlTaskList;
	private TaskAdapter							mlTaskListAdapter;
	
	// Der geoeffnete Task
	private long								mlUserID					= 0;
	private TaskList							mCurrentTaskList			= null;
	private Task								mCurrentTask				= null;
	
	// Der Datenbankzugrif
	private HoloDoDataSource					mDataSource					= null;
	
	// Dialoge
	private TimePickerDialog					mDlgRememberTime			= null;
	private DatePickerDialog					mDlgRememberDate			= null;
	private DatePickerDialog					mDlgDate					= null;
	
	// Dialog Listener
	// Date Dialog
	private DatePickerDialog.OnDateSetListener	mDateSetListener			= null;
	
	// Time Listener
	private DatePickerDialog.OnDateSetListener	mRememberDateSetListener	= null;
	private TimePickerDialog.OnTimeSetListener	mRememberTimeSetListener	= null;
	
	// Dialog Werte
	private int									mnDateYear;
	private int									mnDateMonth;
	private int									mnDateDay;
	private int									mnRememberYear;
	private int									mnRememberMonth;
	private int									mnRememberDay;
	private int									mnRememberHour;
	private int									mnRememberMinute;
	
	// Sortieren nach
	private SortBy								meSortBy					= SortBy.NONE;
	
	// Actionbar Menü
	private Menu								mmActionBarMenue;
	
	/****************************************************************************
	 * Erstellt die Activity
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		mDataSource = new HoloDoDataSource( getApplicationContext() );
		
		// Den Datums und den Erinnerungs Auswahldialog anzeigen
		initDlgListener();
		
		// Den Drawer holen
		mDrawerLayout = ( DrawerLayout )findViewById( R.id.drawer_layout );
		
		// Menp Initalisieren
		initMenu();
		
		// Taskliste Initalisieren
		initTaskList();
		
		// Detail Drawer initalisieren
		initDetailDrawer();
		
		// Drawer Layout initalisieren
		initDrawerLayout();
		
		// Actionbar Initalisieren
		initActionBar();
		
		// Die Menü Liste updaten
		updateMenuList();
		
		// Standart Liste auswaehlen
		mCurrentTaskList = mlMenuList.get( 0 );
		getActionBar().setTitle( mCurrentTaskList.msListName );
		
		// Und die TaskListe Updaten
		updateTaskList();
		
		// Reminder starten
		Reminder.restart( getApplicationContext() );
	}
	
	/**************************************
	 * Wird beim beenden der Anwendung aufgerufen
	 */
	public void finish()
	{
		mDataSource.closeConnections();
		super.finish();
	}
	
	// ****************************************************************************
	// Initalisierungs Methoden
	
	/**************************************
	 * Initalisiert die Datum/Time Auswahl Dialoge
	 */
	private void initDlgListener()
	{
		// Datums Listener
		mDateSetListener = new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet( DatePicker view, int nYear, int nMonthOfYear, int nDayOfMonth )
			{
				mnDateYear = nYear;
				mnDateMonth = nMonthOfYear;
				mnDateDay = nDayOfMonth;
				
				mCurrentTask.msDate = Task.dateToString( mnDateYear, mnDateMonth, mnDateDay );
				mBtnDate.setText( mCurrentTask.msDate );
			}
		};
		
		// Erinnerungs Dialog Listener fuer das Datum
		mRememberDateSetListener = new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet( DatePicker view, int nYear, int nMonthOfYear, int nDayOfMonth )
			{
				mnRememberYear = nYear;
				mnRememberMonth = nMonthOfYear;
				mnRememberDay = nDayOfMonth;
				
				mDlgRememberTime.show();
			}
		};
		
		// Erinnerungsdialog Listener fuer die Zeit
		mRememberTimeSetListener = new TimePickerDialog.OnTimeSetListener()
		{
			@Override
			public void onTimeSet( TimePicker view, int hourOfDay, int minute )
			{
				mnRememberHour = hourOfDay;
				mnRememberMinute = minute;
				
				mCurrentTask.msRemember = Task.timeToString( mnRememberYear, mnRememberMonth,
							mnRememberDay, mnRememberHour, mnRememberMinute );
				mBtnRemember.setText( mCurrentTask.msRemember );
			}
		};
	}
	
	/**************************************
	 * Initalisiert die Dialoge
	 */
	private void initDialogs()
	{
		mDlgRememberTime = new TimePickerDialog( this, mRememberTimeSetListener, mnRememberHour,
					mnRememberMinute, true );
		
		mDlgRememberDate = new DatePickerDialog( this, mRememberDateSetListener, mnRememberYear,
					mnRememberMonth, mnRememberDay );
		
		mDlgDate = new DatePickerDialog( this, mDateSetListener, mnDateYear, mnDateMonth, mnDateDay );
	}
	
	/**************************************
	 * Erstellt den Datums Dialog
	 */
	private void initDetailDrawer()
	{
		mEditTitle = ( EditText )findViewById( R.id.edit_title );
		mEditNote = ( EditText )findViewById( R.id.edit_note );
		mEditPlace = ( EditText )findViewById( R.id.edit_place );
		mBtnDate = ( Button )findViewById( R.id.button_date );
		mBtnRemember = ( Button )findViewById( R.id.button_remember );
		mBtnTitle = ( Button )findViewById( R.id.button_title );
		mBtnFav = ( Button )findViewById( R.id.button_edit_fav );
		mChboxDone = ( CheckBox )findViewById( R.id.checkBox_done );
		
		mEditTitle.setOnKeyListener( new OnKeyListener()
		{
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event )
			{
				if( ( event.getAction() == KeyEvent.ACTION_DOWN )
							&& ( keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) )
				{
					saveChangedTitle();
					return true;
				}
				return false;
			}
		} );
		
		mChboxDone.setOnCheckedChangeListener( new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged( CompoundButton buttonView, boolean isChecked )
			{
				if( isChecked )
				{
					mBtnTitle.setPaintFlags( mBtnTitle.getPaintFlags()
								| Paint.STRIKE_THRU_TEXT_FLAG );
				}
				else
				{
					mBtnTitle.setPaintFlags( mBtnTitle.getPaintFlags()
								& ~Paint.STRIKE_THRU_TEXT_FLAG );
				}
			}
		} );
	}
	
	/**************************************
	 * Generiert das Menue der Application
	 */
	private void initMenu()
	{
		// Controlls holen
		mlvMenu = ( ListView )findViewById( R.id.left_drawer_listview );
		
		// Listen erstellen
		mlMenuList = mDataSource.getTaskListsAsList();
		
		if( mlMenuList.size() > 0 )
		{
			mCurrentTaskList = mlMenuList.get( 0 );
		}
		
		// Adapter erstellen
		mlMenuListAdapter = new TaskListAdapter( getApplicationContext(), mlMenuList );
		
		// Adapter setzen
		mlvMenu.setAdapter( mlMenuListAdapter ); // fuer das Menue
		
		// On Click listener fuer Menue Liste
		mlvMenu.setOnItemClickListener( new OnItemClickListener()
		{
			public void onItemClick( AdapterView<?> parent, View view, int nPos, long lId )
			{
				// Menue schliesen
				mDrawerLayout.closeDrawer( Gravity.LEFT );
				
				mCurrentTaskList = mlMenuList.get( nPos );
				getActionBar().setTitle( mCurrentTaskList.toString() );
				
				// die TaskListe updaten
				updateTaskList();
			}
		} );
		
		registerForContextMenu( mlvMenu );
		
		mEditAddList = ( EditText )findViewById( R.id.add_list_text );
		mEditAddList.setOnKeyListener( new OnKeyListener()
		{
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event )
			{
				if( ( event.getAction() == KeyEvent.ACTION_DOWN )
							&& ( keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) )
				{
					
					addList();
					return true;
				}
				return false;
			}
		} );
	}
	
	/**************************************
	 * Generiert die Taskliste
	 */
	private void initTaskList()
	{
		// Controlls holen
		mlvTasklist = ( ListView )findViewById( R.id.todo_listview );
		
		// Liste der Tasks erstellen
		// Wenn bereits eine TaskListe ausgewält wurde
		if( mCurrentTaskList != null )
		{
			// dann alle Tasks der Liste anzeigen
			mlTaskList = mDataSource.getTasksAsList( mCurrentTaskList.getID(), meSortBy );
		}
		else
		{
			// Ansonsten leer
			mlTaskList = new ArrayList<Task>();
		}
		
		// Adapter erstellen
		mlTaskListAdapter = new TaskAdapter( getApplicationContext(), mlTaskList );
		
		// Adapter setzen
		mlvTasklist.setAdapter( mlTaskListAdapter ); // Fuer die Taskliste
		
		// On Click listener fuer Menue Liste
		mlvTasklist.setOnItemClickListener( new OnItemClickListener()
		{
			public void onItemClick( AdapterView<?> parent, View view, int nPos, long lId )
			{
				mCurrentTask = mlTaskList.get( nPos );
				openTask();
			}
		} );
		
		registerForContextMenu( mlvTasklist );
		
		mEditAddTask = ( EditText )findViewById( R.id.add_task_text );
		mEditAddTask.setOnKeyListener( new OnKeyListener()
		{
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event )
			{
				if( ( event.getAction() == KeyEvent.ACTION_DOWN )
							&& ( keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) )
				{
					
					addTask();
					return true;
				}
				return false;
			}
		} );
	}
	
	/**************************************
	 * ActionBar anpassen
	 */
	private void initActionBar()
	{
		// HomeButton aktivieren
		getActionBar().setDisplayHomeAsUpEnabled( true );
		getActionBar().setHomeButtonEnabled( true );
	}
	
	/**************************************
	 * Initalisiert die das DrawerLayout
	 */
	private void initDrawerLayout()
	{
		// DrawerToogle erstellen
		mDrawerToogle = new ActionBarDrawerToggle( this, mDrawerLayout, R.drawable.ic_drawer,
					R.string.drawer_open, R.string.drawer_close )
		{
			@Override
			public void onDrawerClosed( View view )
			{
				// Wenn ein Task gesetzt ist
				if( mCurrentTask != null )
				{
					// Dann die Werte von den Feldern holen
					String sTitle = mBtnTitle.getText().toString();
					String sNotes = mEditNote.getText().toString();
					String sPlace = mEditPlace.getText().toString();
					String sDate = mBtnDate.getText().toString();
					String sRemember = mBtnRemember.getText().toString();
					boolean bDone = mChboxDone.isChecked();
					
					// Wenn der Titel leer ist einen fehler setzen
					if( sTitle.isEmpty() )
					{
						mEditTitle.setError( getString( R.string.err_no_empty_title ) );
					}
					else
					// ansonsnten in den Task rein speichern
					{
						mCurrentTask.msTitle = sTitle;
						mCurrentTask.msNotes = sNotes;
						mCurrentTask.msPlace = sPlace;
						mCurrentTask.msDate = sDate;
						mCurrentTask.msRemember = sRemember;
						mCurrentTask.mbDone = bDone;
						
						// und in der Datenbank updaten
						mDataSource.updateTask( mCurrentTask );
					}
					
					// Die Felder leern machen
					mBtnTitle.setText( "" );
					mEditTitle.setText( "" );
					mEditTitle.setText( "" );
					mEditTitle.setText( "" );
					mEditTitle.setText( "" );
					mEditTitle.setText( "" );
					mChboxDone.setChecked( false );
					
					// den CurrentTask leer machen um sicher zu sein das
					// nicht versehentlich was geschrieben wird
					mCurrentTask = null;
				}
				
				// die TaskListe updaten
				updateTaskList();
				
				// Den Namen updaten
				getActionBar().setTitle( mCurrentTaskList.msListName );
				
				// das Sortierungs Menü wieder updaten
				updateSortMenue();
				
				// den rechten drawer wieder locken
				mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
							Gravity.RIGHT );
			}
			
			/**
			 * Called when a drawer has settled in a completely open state
			 */
			@Override
			public void onDrawerOpened( View drawerView )
			{
				if( mCurrentTask != null )
				{
					mDrawerLayout
								.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT );
					
					getActionBar().setTitle( mCurrentTask.msTitle );
				}
				else
				{
					updateMenuList();
					getActionBar().setTitle( R.string.action_menue );
				}
				
				remoteSortMenuItems();
			}
		};
		
		// DrawerToogle setzen
		mDrawerLayout.setDrawerListener( mDrawerToogle );
		
		// und den Drawer sperren das er von rechts nicht durch die Swipe geste
		// aufgeht
		mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT );
	}
	
	// ****************************************************************************
	// Operationen
	
	/**************************************
	 * oeffnet einen Task im Drawer
	 */
	protected void openTask()
	{
		mBtnTitle.setText( mCurrentTask.msTitle );
		mEditNote.setText( mCurrentTask.msNotes );
		mEditPlace.setText( mCurrentTask.msPlace );
		mBtnDate.setText( mCurrentTask.msDate );
		mBtnRemember.setText( mCurrentTask.msRemember );
		mChboxDone.setChecked( mCurrentTask.mbDone );
		
		showPriority();
		
		mDrawerLayout.openDrawer( Gravity.RIGHT );
	}
	
	/**************************************
	 * Fuegt einen Task der Liste hinzu benutzt dafuer das Standard Feld fuer
	 * den Titel des Tasks
	 */
	private void addTask()
	{
		// Das EditText Element holen
		EditText text = ( EditText )findViewById( R.id.add_task_text );
		
		// den String vom EditText holen und merken
		String sText = text.getText().toString().trim();
		
		// Die TextBox leer machen
		text.setText( "" );
		
		// Den task hinzufuegen
		addTask( sText );
		
		hideKeyboard( text );
	}
	
	/**************************************
	 * Fuegt einen Task der Liste hinzu
	 * 
	 * @param sTitle Der Titel fuer den Task
	 */
	protected Task addTask( String sTitle )
	{
		Task myTask = null;
		
		if( sTitle.isEmpty() )
		{
			( ( EditText )findViewById( R.id.add_task_text ) ).setError( getResources().getString(
						R.string.err_no_empty_title ) );
		}
		else
		{
			try
			{
				// Ein ToDo der ToDoListe dazufuegen
				if( mCurrentTaskList == null )
				{
					Log.e( "MainActivity.addTask", "mCurrentTaskList == null" );
				}
				else
				{
					long lTaskID = mDataSource.addTask( sTitle, mCurrentTaskList.getID(), mlUserID );
					myTask = mDataSource.getTask( lTaskID );
					
					updateTaskList();
				}
			}
			catch( Exception ex )
			{
				Log.e( "MainActivity.addTask", "Exception in addTask " + ex.toString() );
			}
		}
		
		return myTask;
	}
	
	/**************************************
	 * fuegt ein TaskListe hinzu benutzt dafuer das Standard Feld fuer den Titel
	 * der Taskliste
	 */
	private void addList()
	{
		// Das EditText Element holen
		EditText text = ( EditText )findViewById( R.id.add_list_text );
		
		// den String vom EditText holen und merken
		String sText = text.getText().toString().trim();
		
		// Die TextBox leer machen
		text.setText( "" );
		
		// die liste hinzufuegen
		addList( sText );
		
		hideKeyboard( text );
	}
	
	/**************************************
	 * Fuegt eine TaskListe hinzu
	 * 
	 * @param sTitle Der Titel fuer die Liste
	 */
	protected TaskList addList( String sTitle )
	{
		TaskList myTaskList = null;
		if( sTitle.isEmpty() )
		{
			( ( EditText )findViewById( R.id.add_list_text ) ).setError( getResources().getString(
						R.string.err_no_empty_list ) );
		}
		else
		{
			try
			{
				// Die ToDo liste erzeugen
				long lTaskListID = mDataSource.addList( sTitle, mlUserID );
				myTaskList = mDataSource.getTaskList( lTaskListID );
				
				updateMenuList();
			}
			catch( Exception ex )
			{
				Log.e( "MainActivity.addList", "Exception in addList " + ex.toString() );
			}
		}
		
		return myTaskList;
	}
	
	/**************************************
	 * Die Menü Liste updaten
	 */
	private void updateMenuList()
	{
		mlMenuList.clear();
		
		ArrayList<TaskList> lists = mDataSource.getTaskListsAsList();;
		for( TaskList list : lists )
		{
			mlMenuList.add( list );
		}
		
		// Den Adapter Updaten
		( ( BaseAdapter )mlMenuListAdapter ).notifyDataSetChanged();
	}
	
	/**************************************
	 * Die Task Liste updaten
	 */
	private void updateTaskList()
	{
		mlTaskList.clear();
		
		ArrayList<Task> tasks = mDataSource.getTasksAsList( mCurrentTaskList.getID(), meSortBy );
		for( Task task : tasks )
		{
			mlTaskList.add( task );
		}
		
		// Den Adapter Updaten
		( ( BaseAdapter )mlTaskListAdapter ).notifyDataSetChanged();
	}
	
	// ****************************************************************************
	// Interne Funktionen
	
	/**************************************
	 * Seichert den Titel vom EditText in den Task und stellt ihn auf dem Button
	 * ein auueerdem versteckt es das EditText und zeigt den Button wieder an
	 */
	private void saveChangedTitle()
	{
		String sTitle = mEditTitle.getText().toString();
		
		if( sTitle.isEmpty() )
		{
			mEditTitle.setError( getResources().getString( R.string.err_no_empty_title ) );
		}
		else
		{
			mCurrentTask.msTitle = sTitle;
			mBtnTitle.setText( mCurrentTask.msTitle );
			
			mBtnTitle.setVisibility( View.VISIBLE );
			mEditTitle.setVisibility( View.GONE );
			
			showPriority();
			
			getActionBar().setTitle( mCurrentTask.msTitle );
			
			hideKeyboard( mEditTitle );
		}
	}
	
	/**************************************
	 * Zeigt die Priorituet auf den Button an
	 */
	private void showPriority()
	{
		switch( mCurrentTask.mePriority )
		{
			case Low:
				mBtnFav.setBackground( getResources().getDrawable(
							R.drawable.ic_action_not_important ) );
				break;
			
			case Normal:
				mBtnFav.setBackground( getResources().getDrawable(
							R.drawable.ic_action_half_important ) );
				break;
			
			case High:
				mBtnFav.setBackground( getResources().getDrawable( R.drawable.ic_action_important ) );
				break;
		}
	}
	
	/**************************************
	 * Toogelt die Priorituet eines Tasks durch
	 */
	private void tooglePriority()
	{
		
		switch( mCurrentTask.mePriority )
		{
			case Low:
				mBtnFav.setBackground( getResources().getDrawable(
							R.drawable.ic_action_half_important ) );
				mCurrentTask.mePriority = TaskPriority.Normal;
				break;
			
			case Normal:
				mBtnFav.setBackground( getResources().getDrawable( R.drawable.ic_action_important ) );
				mCurrentTask.mePriority = TaskPriority.High;
				break;
			
			case High:
				mBtnFav.setBackground( getResources().getDrawable(
							R.drawable.ic_action_not_important ) );
				mCurrentTask.mePriority = TaskPriority.Low;
				break;
		}
	}
	
	/**************************************
	 * Updatet die Anzeige der sortierungsmöglichkeiten
	 */
	private void updateSortMenue()
	{
		remoteSortMenuItems();
		
		switch( meSortBy )
		{
			case NONE:
				{
					mmActionBarMenue.add( Menu.NONE, R.id.action_sort_name_desc, Menu.NONE,
								R.string.action_sort_name_desc );
					mmActionBarMenue.add( Menu.NONE, R.id.action_sort_name_asc, Menu.NONE,
								R.string.action_sort_name_asc );
				}
				break;
			
			case NAME_ASCEND:
				{
					mmActionBarMenue.add( Menu.NONE, R.id.action_sort_none, Menu.NONE,
								R.string.action_sort_none );
					mmActionBarMenue.add( Menu.NONE, R.id.action_sort_name_desc, Menu.NONE,
								R.string.action_sort_name_desc );
				}
				break;
			
			case NAME_DESCEND:
				{
					mmActionBarMenue.add( Menu.NONE, R.id.action_sort_none, Menu.NONE,
								R.string.action_sort_none );
					mmActionBarMenue.add( Menu.NONE, R.id.action_sort_name_asc, Menu.NONE,
								R.string.action_sort_name_asc );
				}
				break;
		}
	}
	
	/**************************************
	 * entfernt alle Menü Einträge zur Sortierung
	 */
	private void remoteSortMenuItems()
	{
		mmActionBarMenue.removeItem( R.id.action_sort_none );
		mmActionBarMenue.removeItem( R.id.action_sort_name_asc );
		mmActionBarMenue.removeItem( R.id.action_sort_name_desc );
	}
	
	/**************************************
	 * Das Keyboard verstecken
	 * 
	 * @param text das EditText Element von dem das Keyboard versteckt werden
	 *            soll
	 */
	private void hideKeyboard( EditText text )
	{
		InputMethodManager imm = ( InputMethodManager )getSystemService( Context.INPUT_METHOD_SERVICE );
		imm.hideSoftInputFromWindow( text.getWindowToken(), 0 );
	}
	
	/**************************************
	 * Das Keyboard anzeigen
	 * 
	 * @param text das EditText Element in dem das Keyboard angezeigt werden
	 *            soll
	 */
	private void showKeyboard( EditText text )
	{
		InputMethodManager imm = ( InputMethodManager )getSystemService( Context.INPUT_METHOD_SERVICE );
		imm.showSoftInput( text, InputMethodManager.SHOW_IMPLICIT );
	}
	
	/**************************************
	 * Kopiert die Datenbank in ein Verzeichnis von wo aus sie auf den PC
	 * kopiert werden kann
	 * 
	 * @throws IOException falls Datenbank nicht gefunden werden konnte oder
	 *             nicht kopiert werden konnte
	 */
	@SuppressWarnings( "resource" )
	private void dumpDb() throws IOException
	{
		File fSdCard = Environment
					.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS );
		
		if( fSdCard.canWrite() )
		{
			String currentDBPath = DatabaseHelper.DATABASE_NAME;
			String backupDBPath = "backupname.db";
			
			String sDbPath = getApplicationContext().getFilesDir().getAbsolutePath()
						.replace( "files", "databases" )
						+ File.separator;
			
			File sCurrentDB = new File( sDbPath, currentDBPath );
			File sBackupDB = new File( fSdCard, backupDBPath );
			
			if( sCurrentDB.exists() )
			{
				FileChannel src = new FileInputStream( sCurrentDB ).getChannel();
				FileChannel dst = new FileOutputStream( sBackupDB ).getChannel();
				dst.transferFrom( src, 0, src.size() );
				src.close();
				dst.close();
				
				Toast.makeText( getApplicationContext(), getString( R.string.debug_copy_succeed ),
							Toast.LENGTH_SHORT ).show();
			}
		}
	}
	
	// ****************************************************************************
	// Nachrichten / Event Behandlung
	
	/**************************************
	 * Erstellt das Menue
	 * 
	 * @param menu Das Menue welches erstellt wird
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate( R.menu.main, menu );
		mmActionBarMenue = menu;
		
		updateSortMenue();
		
		boolean isDebuggable = ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
		if( isDebuggable == false )
		{
			mmActionBarMenue.removeItem( R.id.action_debug_dump_db );
		}
		
		return true;
	}
	
	/**************************************
	 * Behandelt einen klick auf ein Menue Item in der Actionbar
	 * 
	 * @param item das Menueitem welches angeklickt wurde
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
			case R.id.action_sort_name_asc:
				{
					meSortBy = SortBy.NAME_ASCEND;
					mmActionBarMenue.removeItem( R.id.action_sort_name_asc );
					updateSortMenue();
					updateTaskList();
				}
				break;
			
			case R.id.action_sort_name_desc:
				{
					meSortBy = SortBy.NAME_DESCEND;
					updateSortMenue();
					updateTaskList();
				}
				break;
			
			case R.id.action_sort_none:
				{
					meSortBy = SortBy.NONE;
					updateSortMenue();
					updateTaskList();
				}
				break;
			
			case android.R.id.home:
				{
					if( mCurrentTask != null )
					{
						// den offenen rechten Drawer schliesen
						mDrawerLayout.closeDrawer( Gravity.RIGHT );
					}
					else
					{
						if( bMenuOpened == false )
						{
							// den Menü Drawer öffnen
							mDrawerLayout.openDrawer( Gravity.LEFT );
							
							bMenuOpened = true;
						}
						else
						{
							// den Menü Drawer öffnen
							mDrawerLayout.closeDrawer( Gravity.LEFT );
							
							bMenuOpened = false;
						}
					}
				}
			
			case R.id.action_debug_dump_db:
				{
					try
					{
						dumpDb();
					}
					catch( Exception e )
					{
						Log.e( "MainActivity.onOptionsItemSelected", e.getMessage() );
					}
				}
		}
		
		return super.onOptionsItemSelected( item );
	}
	
	/**************************************
	 * Erstellt das Context Menü
	 * 
	 * @param menu das ContextMenü
	 * @param v der View
	 * @param menuInfo ContextMenuInfo
	 */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		
		ListView lv = ( ListView )v;
		AdapterView.AdapterContextMenuInfo acmi = ( AdapterView.AdapterContextMenuInfo )menuInfo;
		MenuInflater inflater = getMenuInflater();
		
		switch( v.getId() )
		{
			case R.id.todo_listview:
				{
					Task task = ( Task )lv.getItemAtPosition( acmi.position );
					menu.setHeaderTitle( String.format( getString( R.string.context_title ),
								task.msTitle ) );
					
					inflater.inflate( ( task.mbDone == true ) ? R.menu.context_menu_tasks_done
								: R.menu.context_menu_tasks, menu );
					
				}
				break;
			
			case R.id.left_drawer_listview:
				{
					TaskList list = ( TaskList )lv.getItemAtPosition( acmi.position );
					menu.setHeaderTitle( String.format( getString( R.string.context_title ),
								list.msListName ) );
					inflater.inflate( R.menu.context_menu_lists, menu );
				}
				break;
		}
	}
	
	/**************************************
	 * Behandelt einen klick auf ein Menue Item in der Actionbar
	 * 
	 * @param item das Menueitem welches angeklickt wurde
	 */
	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
		AdapterContextMenuInfo info = ( AdapterContextMenuInfo )item.getMenuInfo();
		int nIndex = info.position;
		
		switch( info.targetView.getId() )
		{
			case R.id.lvitem_todo_list:
				{
					Task task = mlTaskList.get( nIndex );
					
					switch( item.getItemId() )
					{
						case R.id.context_delete:
							{
								mDataSource.deleteTask( task.getID() );
								Toast.makeText(
											getApplicationContext(),
											task.msTitle
														+ getString( R.string.toast_delete_succesfull ),
											Toast.LENGTH_SHORT ).show();
								updateTaskList();
							}
							break;
						
						case R.id.context_done:
							{
								if( task.mbDone == false )
								{
									task.mbDone = true;
									Toast.makeText(
												getApplicationContext(),
												task.msTitle + getString( R.string.toast_task_done ),
												Toast.LENGTH_SHORT ).show();
								}
								else
								{
									task.mbDone = false;
								}
								
								mDataSource.updateTask( task );
								updateTaskList();
							}
							break;
						
						case R.id.context_open:
							{
								mCurrentTask = task;
								openTask();
							}
							break;
					}
				}
				break;
			
			case R.id.menu_list_item:
				{
					TaskList list = mlMenuList.get( nIndex );
					
					switch( item.getItemId() )
					{
						case R.id.context_delete:
							{
								mCurrentTaskList = mDataSource.deleteList( list.getID() );
								Toast.makeText(
											getApplicationContext(),
											list.msListName
														+ getString( R.string.toast_delete_succesfull ),
											Toast.LENGTH_SHORT ).show();
								
								updateMenuList();
								updateTaskList();
								
							}
							break;
					}
				}
				break;
		}
		
		return super.onContextItemSelected( item );
	}
	
	/**************************************
	 * fuegt ein Eintrag dem Menue hinzu
	 * 
	 * @param view
	 */
	public void onAddTask( View view )
	{
		addTask();
	}
	
	/**************************************
	 * fuegt ein Eintrag dem Menue hinzu
	 * 
	 * @param view
	 */
	public void onAddList( View view )
	{
		addList();
	}
	
	/**************************************
	 * Auf den Titel Button wurde geklickt
	 * 
	 * @param view
	 */
	public void onTitleClicked( View view )
	{
		if( mBtnTitle.getVisibility() == View.VISIBLE )
		{
			// Felder verstecken und anzeigen
			mEditTitle.setVisibility( View.VISIBLE );
			mBtnTitle.setVisibility( View.GONE );
			
			// Fokus setzen
			mEditTitle.setFocusableInTouchMode( true );
			mEditTitle.requestFocus();
			
			// Keyboard anzeigen
			showKeyboard( mEditTitle );
			
			if( mCurrentTask != null )
			{
				mEditTitle.setText( mCurrentTask.msTitle );
				mBtnFav.setBackground( getResources().getDrawable( R.drawable.ic_action_save ) );
			}
			else
			{
				Log.e( "MainActivity.onTitleClicked", "Kein Task geueffnet" );
				mEditTitle.setText( mBtnTitle.getText() );
			}
		}
		else
		{
			Log.e( "MainActivity.onTitleClicked", "Button geklickt obwohl Button View.GONE ist" );
		}
	}
	
	/**************************************
	 * Auf den Edit bzw den Favoriten Button wurde geklickt
	 * 
	 * @param view
	 */
	public void onEditFavClicked( View view )
	{
		EditText editTitle = ( EditText )findViewById( R.id.edit_title );
		
		if( editTitle.getVisibility() == View.GONE )
		{
			tooglePriority();
		}
		else
		{
			saveChangedTitle();
		}
	}
	
	/**************************************
	 * uendert die Erinnerungszeit bzw. fuegt diese hinzu
	 * 
	 * @param view
	 */
	public void onChangeDate( View view )
	{
		if( mCurrentTask != null )
		{
			try
			{
				String sTemp = mCurrentTask.msDate;
				
				int[] anDate = new int[3];
				
				Task.stringToDate( sTemp, anDate );
				mnDateYear = anDate[0];
				mnDateMonth = anDate[1];
				mnDateDay = anDate[2];
			}
			catch( Exception e )
			{
				Calendar cal = Calendar.getInstance();
				
				mnDateDay = cal.get( Calendar.DATE );
				mnDateMonth = cal.get( Calendar.MONTH );
				mnDateYear = cal.get( Calendar.YEAR );
			}
		}
		
		initDialogs();
		mDlgDate.show();
	}
	
	/**************************************
	 * uendert das Erinnerungsdatum bzw. fuegt dieses hinzu
	 * 
	 * @param view
	 */
	public void onChangeRemember( View view )
	{
		if( mCurrentTask != null )
		{
			try
			{
				String sTemp = mCurrentTask.msRemember;
				
				int[] anTime = new int[5];
				Task.stringToTime( sTemp, anTime );
				mnRememberYear = anTime[0];
				mnRememberMonth = anTime[1];
				mnRememberDay = anTime[2];
				mnRememberHour = anTime[3];
				mnRememberMinute = anTime[4];
			}
			catch( Exception e )
			{
				Log.e( "MainActivity.onChangeDate", "Wrong format" );
				Calendar cal = Calendar.getInstance();
				
				mnRememberDay = cal.get( Calendar.DATE );
				mnRememberMonth = cal.get( Calendar.MONTH );
				mnRememberYear = cal.get( Calendar.YEAR );
				mnRememberHour = cal.get( Calendar.HOUR_OF_DAY );
				mnRememberMinute = cal.get( Calendar.MINUTE );
			}
		}
		
		initDialogs();
		mDlgRememberDate.show();
	}
	
	/**************************************
	 * Löscht das Datum
	 * 
	 * @param view
	 */
	public void onDeleteDate( View view )
	{
		mBtnDate.setText( R.string.button_default_date );
	}
	
	/**************************************
	 * löscht die erinnerung
	 * 
	 * @param view
	 */
	public void onDeleteRemember( View view )
	{
		mBtnRemember.setText( R.string.button_default_remember );
	}
}
