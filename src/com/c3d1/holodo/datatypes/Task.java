
package com.c3d1.holodo.datatypes;

import java.util.Calendar;

import android.util.Log;

public class Task
{
	private long		mlID;
	public String		msTitle;
	public long			mlTaskListID;
	public long			mlUserID;
	public String		msNotes;
	public String		msDate;
	public String		msRemember;
	public String		msPlace;
	public TaskPriority	mePriority;
	public boolean		mbDone;
	private String		msCreateTime;
	public String		msModifyTime;
	
	/**
	 * Ein normales Task Objekt erzeugen um einen Task anzulegen
	 * 
	 * @param sTitle
	 * @param nTaskListID
	 */
	public Task( String sTitle, long nTaskListID )
	{
		this.mlID = -1;
		
		this.msTitle = sTitle;
		this.mlTaskListID = nTaskListID;
		this.msNotes = "";
		this.msDate = null;
		this.msRemember = null;
		this.msPlace = "";
		this.mePriority = TaskPriority.Low;
		this.mbDone = false;
		this.mlUserID = 0;
		
		Calendar cal = Calendar.getInstance();
		
		this.msCreateTime = timeToString( cal );
		this.msModifyTime = this.msCreateTime;
	}
	
	/**
	 * Konstruktor im Task Objekte in MainActivity zu erstellen
	 * 
	 * @param msTitle
	 * @param mnTaskListID
	 * @param lUserID
	 * @param msNotes
	 * @param msDate
	 * @param msRemember
	 * @param msPlace
	 * @param mePriority
	 * @param mbDone
	 */
	public Task( String msTitle, long mnTaskListID, long lUserID, String msNotes, String msDate,
				String msRemember, String msPlace, TaskPriority mePriority, boolean mbDone )
	{
		this.msTitle = msTitle;
		this.mlTaskListID = mnTaskListID;
		this.msNotes = msNotes;
		this.msDate = msDate;
		this.msRemember = msRemember;
		this.msPlace = msPlace;
		this.mePriority = mePriority;
		this.mbDone = mbDone;
		this.mlUserID = lUserID;
	}
	
	/**
	 * Konstruktor um Task aus der Datenbank heraus zu holen
	 * 
	 * @param lID
	 * @param sTitle
	 * @param lTaskListID
	 * @param lUserID
	 * @param sNote
	 * @param sDate
	 * @param sRemember
	 * @param sPlace
	 * @param nPriority
	 * @param bDone
	 * @param sCreateTime
	 */
	public Task( long lID, String sTitle, long lTaskListID, long lUserID, String sNote,
				String sDate, String sRemember, String sPlace, int nPriority, int bDone,
				String sCreateTime, String sModifyTime )
	{
		this.mlID = lID;
		this.msTitle = sTitle;
		this.mlTaskListID = lTaskListID;
		this.msNotes = sNote;
		this.msDate = sDate;
		this.msRemember = sRemember;
		this.msPlace = sPlace;
		this.mePriority = intToEnumPriority( nPriority );
		this.mlUserID = lUserID;
		this.msCreateTime = sCreateTime;
		this.msModifyTime = sModifyTime;
		
		switch( bDone )
		{
			case 0:
				this.mbDone = false;
				break;
			
			case 1:
				this.mbDone = true;
				break;
			
			default:
				Log.e( "Task.Task", "bDone only 0 and 1 supportet" );
				this.mbDone = false;
				break;
		}
	}
	
	/**
	 * @return die Task ID
	 */
	public long getID()
	{
		return this.mlID;
	}
	
	/**
	 * @return die Zeit als der Task erstellt wurde
	 */
	public String getCreateTime()
	{
		return msCreateTime;
	}
	
	/**
	 * TimeStamp updaten für die ModifyTime
	 */
	public void updateTimestamp()
	{
		Calendar cal = Calendar.getInstance();
		
		this.msModifyTime = timeToString( cal );
	}
	
	/**
	 * ToString Funktion überschreiben
	 */
	@Override
	public String toString()
	{
		return msTitle;
	}
	
	/**
	 * Wandelt das Priority ENUM in ein int um
	 * 
	 * @param priority die Priorität als ENUM
	 * @return die Priorität als int
	 */
	public static int enumToIntPriority( TaskPriority priority )
	{
		int nPriority = -1;
		
		switch( priority )
		{
			case Low:
				nPriority = 0;
				break;
			
			case Normal:
				nPriority = 1;
				break;
			
			case High:
				nPriority = 2;
				break;
		}
		
		return nPriority;
	}
	
	/**
	 * Wandelt das Priority int in ein ENUM um
	 * 
	 * @param priority die Priorität als int
	 * @return die Priorität als enum
	 */
	public static TaskPriority intToEnumPriority( int nPriority )
	{
		TaskPriority ePriority = null;
		
		switch( nPriority )
		{
			case 0:
				ePriority = TaskPriority.Low;
				break;
			
			case 1:
				ePriority = TaskPriority.Normal;
				break;
			
			case 2:
				ePriority = TaskPriority.High;
				break;
			
			default:
				Log.e( "Task.Task", "nPriority only 0 (Low), 1 (normal), 2 (high) supportet" );
				ePriority = TaskPriority.Low;
				break;
		}
		
		return ePriority;
	}
	
	/**
	 * erstellt den gültigen TimeString
	 * 
	 * @param nYear
	 * @param nMonth
	 * @param nDate
	 * @param nHour
	 * @param nMinute
	 * @return
	 */
	public static String timeToString( int nYear, int nMonth, int nDate, int nHour, int nMinute )
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( dateToString( nYear, nMonth, nDate ) );
		sb.append( " " );
		sb.append( nHour ).append( ":" );
		
		if( nMinute < 10 )
		{
			sb.append( '0' );
		}
		
		sb.append( nMinute );
		
		return sb.toString();
	}
	
	/**
	 * erstellt den gültigen TimeString
	 * 
	 * @param cal
	 * @return
	 */
	public static String timeToString( Calendar cal )
	{
		return timeToString( cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH ),
					cal.get( Calendar.DATE ), cal.get( Calendar.HOUR_OF_DAY ),
					cal.get( Calendar.MINUTE ) );
	}
	
	/**
	 * Erstellt aus einem String die die Zeit in int's
	 * 
	 * @param sTime
	 * @param anTime
	 */
	public static void stringToTime( String sTime, int[] anTime )
	{
		if( anTime.length != 5 )
		{
			throw new UnsupportedOperationException();
		}
		
		int nYear;
		int nMonth;
		int nDate;
		int nHour;
		int nMinute;
		
		int nA = 0;
		int nB = sTime.indexOf( '.', nA );
		nDate = Integer.parseInt( sTime.substring( nA, nB ) );
		nA = nB + 1;
		
		nB = sTime.indexOf( '.', nA );
		nMonth = Integer.parseInt( sTime.substring( nA, nB ) );
		nMonth--; // Month is 0 based
		nA = nB + 1;
		
		nB = sTime.indexOf( ' ', nA );
		nYear = Integer.parseInt( sTime.substring( nA, nB ) );
		nA = nB + 1;
		
		nB = sTime.indexOf( ':', nA );
		nHour = Integer.parseInt( sTime.substring( nA, nB ) );
		nA = nB + 1;
		
		nMinute = Integer.parseInt( sTime.substring( nA, sTime.length() ) );
		
		anTime[0] = nYear;
		anTime[1] = nMonth;
		anTime[2] = nDate;
		anTime[3] = nHour;
		anTime[4] = nMinute;
	}
	
	/**
	 * erstellt den gültigen DateString
	 * 
	 * @param nYear
	 * @param nMonth
	 * @param nDate
	 * @return
	 */
	public static String dateToString( int nYear, int nMonth, int nDate )
	{
		StringBuilder sb = new StringBuilder();
		
		// Month is 0 based so add 1
		sb.append( nDate ).append( "." );
		sb.append( nMonth + 1 ).append( "." );
		sb.append( nYear );
		
		return sb.toString();
	}
	
	/**
	 * erstellt den gültigen DateString
	 * 
	 * @param cal
	 * @return
	 */
	
	public static String dateToString( Calendar cal )
	{
		return dateToString( cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH ),
					cal.get( Calendar.DATE ) );
	}
	
	/**
	 * Erstellt aus einem String das Datum in int's
	 * 
	 * @param sDate
	 * @param nYear
	 * @param nMonth
	 * @param nDate
	 */
	public static void stringToDate( String sDate, int[] anDate )
	{
		if( anDate.length != 3 )
		{
			throw new UnsupportedOperationException();
		}
		
		int nYear, nMonth, nDate;
		
		int nA = 0;
		int nB = sDate.indexOf( '.', nA );
		nDate = Integer.parseInt( sDate.substring( nA, nB ) );
		nA = nB + 1;
		
		nB = sDate.indexOf( '.', nA );
		nMonth = Integer.parseInt( sDate.substring( nA, nB ) );
		nMonth--; // Month i 0 based
		nA = nB + 1;
		
		nYear = Integer.parseInt( sDate.substring( nA, sDate.length() ) );
		
		anDate[0] = nYear;
		anDate[1] = nMonth;
		anDate[2] = nDate;
	}
}
