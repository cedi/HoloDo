package com.c3d1.holodo;

import java.util.List;

import com.c3d1.holodo.database.HoloDoDataSource;
import com.c3d1.holodo.datatypes.TaskList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter
{
	List<TaskList>				mlLists;
	Context						mContext;
	private HoloDoDataSource	mDataSource	= null;

	TaskListAdapter( Context context, List<TaskList> lists )
	{
		mlLists = lists;
		mContext = context;

		mDataSource = new HoloDoDataSource( mContext );
	}

	@Override
	public int getCount()
	{
		return mlLists.size();
	}

	@Override
	public TaskList getItem( int nPos )
	{
		return mlLists.get( nPos );
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
					LayoutInflater.from( mContext ).inflate( R.layout.menu_list_item, parent, false );
		}

		TextView tvTitle = ( TextView )convertView.findViewById( R.id.textView_list_title );
		tvTitle.setText( mlLists.get( nPos ).msListName );

		TextView tvCount = ( TextView )convertView.findViewById( R.id.textView_count );

		String sCount = mDataSource.getTaskListCount( mlLists.get( nPos ).getID() );

		if( Integer.parseInt( sCount ) > 99 )
		{
			sCount = mContext.getString( R.string.list_99 );
		}

		if( sCount == null || sCount.isEmpty() )
		{
			tvCount.setVisibility( View.GONE );
		}
		else
		{
			tvCount.setVisibility( View.VISIBLE );
			tvCount.setText( sCount );
		}

		return convertView;
	}

}
