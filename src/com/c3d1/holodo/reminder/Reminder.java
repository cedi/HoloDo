
package com.c3d1.holodo.reminder;

import java.util.ArrayList;
import java.util.Calendar;

import com.c3d1.holodo.MainActivity;
import com.c3d1.holodo.R;
import com.c3d1.holodo.database.HoloDoDataSource;
import com.c3d1.holodo.datatypes.Task;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Reminder extends BroadcastReceiver
{
	private static final String	TIMED	= "timed";
	
	/**
	 * @param context
	 * @param intent
	 */
	@Override
	public void onReceive( Context context, Intent intent )
	{
		if( intent.getBooleanExtra( TIMED, false ) )
		{
			notify( context );
		}
		
		start( context );
	}
	
	/**
	 * Erstellt einen Intent um den Reminder korrekt starten zu kšnnen
	 * 
	 * @param context
	 * @return
	 */
	private static PendingIntent createPendingIntent( Context context )
	{
		Intent intent = new Intent( context, Reminder.class );
		intent.putExtra( TIMED, true );
		return PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
	}
	
	/**
	 * Startet die Erinnerungsfunktion
	 * 
	 * @param context
	 */
	public static void start( Context context )
	{
		AlarmManager alarmManager = ( AlarmManager )context
					.getSystemService( Context.ALARM_SERVICE );
		PendingIntent pendingIntent = createPendingIntent( context );
		
		Calendar wakeUpCal = Calendar.getInstance();
		wakeUpCal.add( Calendar.MINUTE, 1 );
		
		alarmManager.set( AlarmManager.RTC_WAKEUP, wakeUpCal.getTimeInMillis(), pendingIntent );
	}
	
	/**
	 * Stoppt die Erinnerungsfunktion
	 * 
	 * @param context
	 */
	public static void stop( Context context )
	{
		AlarmManager alarmManager = ( AlarmManager )context
					.getSystemService( Context.ALARM_SERVICE );
		PendingIntent pendingIntent = createPendingIntent( context );
		
		alarmManager.cancel( pendingIntent );
		pendingIntent.cancel();
	}
	
	/**
	 * Startet die Erinnerungsfunktion neu
	 * 
	 * @param context
	 */
	public static void restart( Context context )
	{
		stop( context );
		start( context );
	}
	
	/**
	 * die eigentliche notify Funktion.
	 * 
	 * @param context
	 */
	private void notify( Context context )
	{
		try
		{
			HoloDoDataSource dataSource = new HoloDoDataSource( context );
			
			// alle tasks holen
			 ArrayList<Task> alTasks = dataSource.getTasksFromTimeAsList( Task
						.timeToString( Calendar.getInstance() ) );
			
			int nSize = alTasks.size();
			
			if( nSize > 0 )
			{
				
				// NotificationManager erstellen
				NotificationManager notificationManager = ( NotificationManager )context
							.getSystemService( Context.NOTIFICATION_SERVICE );
				
				// alte lšschen
				notificationManager.cancelAll();
				
				String sTitle = context.getString( R.string.app_name );
				
				Intent intent = new Intent( context, MainActivity.class );
				PendingIntent pi = PendingIntent.getActivity( context, 0, intent,
							Intent.FLAG_ACTIVITY_NEW_TASK );
				
				Notification notification = new Notification( R.drawable.ic_launcher, sTitle,
							System.currentTimeMillis() );
				
				String sText = "";
				for( int nIdx = 0; nIdx < nSize - 1; nIdx++ )
				{
					sText += alTasks.get( nIdx ).msTitle + ", ";
				}
				
				sText += alTasks.get( nSize - 1 ).msTitle;
				
				if( nSize > 1 )
				{
					notification.number = nSize;
				}
				
				notification.setLatestEventInfo( context, sTitle, sText, pi );
				notificationManager.notify( 0, notification );
			}
		}
		catch( Exception e )
		{
			Log.e( "Reminder.notify", e.getMessage() );
		}
	}
}
