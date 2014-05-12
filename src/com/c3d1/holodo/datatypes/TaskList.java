package com.c3d1.holodo.datatypes;

import java.util.Calendar;

public class TaskList
{
	private long	mlListID;
	public long		mlUserID;
	public String	msListName;
	private String	msCreateTime;
	public String	msModifyTime;

	/**
	 * Taskliste erstellen um neune Task hinzuzufügen
	 * 
	 * @param sListName
	 */
	public TaskList( String sListName )
	{
		this.mlListID = -1;
		this.mlUserID = -1;
		this.msListName = sListName;

		Calendar cal = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();

		sb.append( cal.get( Calendar.DATE ) ).append( "." );
		sb.append( cal.get( Calendar.MONTH ) + 1 ).append( "." );
		sb.append( cal.get( Calendar.YEAR ) ).append( " " );
		sb.append( cal.get( Calendar.HOUR ) ).append( ":" );
		sb.append( cal.get( Calendar.MINUTE ) );

		this.msCreateTime = sb.toString();
		this.msModifyTime = this.msCreateTime;
	}

	/**
	 * Konstruktor um TaskList Objekt für die Datenbank zu ersellen
	 * 
	 * @param lListID
	 * @param lUserID
	 * @param sListName
	 * @param sCreateTime
	 * @param sModifyTime
	 */
	public TaskList( long lListID, long lUserID, String sListName, String sCreateTime, String sModifyTime )
	{
		this.mlListID = lListID;
		this.mlUserID = lUserID;
		this.msListName = sListName;
		this.msCreateTime = sCreateTime;
		this.msModifyTime = sModifyTime;
	}

	/**
	 * @return die ID der liste
	 */
	public long getID()
	{
		return mlListID;
	}

	/**
	 * @return holt die erstellungszeit
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
		StringBuilder sb = new StringBuilder();

		sb.append( cal.get( Calendar.DATE ) ).append( "." );
		sb.append( cal.get( Calendar.MONTH ) + 1 ).append( "." );
		sb.append( cal.get( Calendar.YEAR ) ).append( " " );
		sb.append( cal.get( Calendar.HOUR ) ).append( ":" );
		sb.append( cal.get( Calendar.MINUTE ) );

		this.msModifyTime = sb.toString();
	}

	/**
	 * den Titel
	 */
	@Override
	public String toString()
	{
		return msListName;
	}

}
