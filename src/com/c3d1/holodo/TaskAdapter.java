package com.c3d1.holodo;

// Java Imports
import java.util.List;

// HoloDo Imports
import com.c3d1.holodo.datatypes.Task;

import android.widget.ImageView;
// Android Imprts
import android.widget.TextView;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TaskAdapter extends BaseAdapter
{
	List<Task>	mlTasks;
	Context		mContext;

	TaskAdapter( Context context, List<Task> tasks )
	{
		mlTasks = tasks;
		mContext = context;
	}

	@Override
	public int getCount()
	{
		return mlTasks.size();
	}

	@Override
	public Task getItem( int nPos )
	{
		return mlTasks.get( nPos );
	}

	@Override
	public long getItemId( int position )
	{
		return 0;
	}

	@Override
	public View getView( int nPos, View convertView, ViewGroup parent )
	{
		if( convertView == null )
		{
			convertView =
					LayoutInflater.from( mContext ).inflate( R.layout.todo_list_item, parent, false );
		}

		// setup our row
		TextView tvTitle = ( TextView )convertView.findViewById( R.id.textview_title );
		tvTitle.setText( mlTasks.get( nPos ).msTitle );

		if( mlTasks.get( nPos ).mbDone )
		{
			tvTitle.setPaintFlags( tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
		}
		else
		{
			tvTitle.setPaintFlags( tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG );
		}

		TextView tvDate = ( TextView )convertView.findViewById( R.id.textview_date );
		tvDate.setText( mlTasks.get( nPos ).msDate );

		TextView tvNote = ( TextView )convertView.findViewById( R.id.textview_note );

		String sTmp = mlTasks.get( nPos ).msNotes;

		if( sTmp.isEmpty() )
		{
			tvNote.setVisibility( View.GONE );
		}
		else
		{
			tvNote.setVisibility( View.VISIBLE );
			tvNote.setText( sTmp );
		}

		ImageView image = ( ImageView )convertView.findViewById( R.id.image_favorite );

		switch( mlTasks.get( nPos ).mePriority )
		{
			case Low:
				image.setImageResource( R.drawable.ic_action_not_important );
				break;

			case Normal:
				image.setImageResource( R.drawable.ic_action_half_important );
				break;

			case High:
				image.setImageResource( R.drawable.ic_action_important );
				break;
		}

		if( mlTasks.get( nPos ).msDate == null )
			mlTasks.get( nPos ).msDate = mContext.getString( R.string.default_date );

		if( mlTasks.get( nPos ).msRemember == null )
			mlTasks.get( nPos ).msRemember = mContext.getString( R.string.default_remember );

		return convertView;
	}

}
