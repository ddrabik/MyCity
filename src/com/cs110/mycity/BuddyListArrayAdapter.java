package com.cs110.mycity;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BuddyListArrayAdapter extends ArrayAdapter<String> {
		  private final Activity context;
		  private final ArrayList<String> buddies;

		  static class ViewHolder {
		    public TextView text;
		    public ImageView image;
		  }

		  public BuddyListArrayAdapter(Activity context, ArrayList<String> listItems) {
		    super(context, R.layout.buddylistrow, listItems);
		    this.context = context;
		    this.buddies = listItems;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    View rowView = convertView;
		    if (rowView == null) {
		      LayoutInflater inflater = context.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.buddylistrow, null);
		      ViewHolder viewHolder = new ViewHolder();
		      viewHolder.text = (TextView) rowView.findViewById(R.id.label);
		      viewHolder.image = (ImageView) rowView
		          .findViewById(R.id.icon);
		      rowView.setTag(viewHolder);
		    }

		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    String s = buddies.get(position);
		    holder.text.setText(s.substring(1));
		    if (s.startsWith("A")){
		      holder.image.setImageResource(R.drawable.buddy_avaliable);
		    } else {
		      holder.image.setImageResource(R.drawable.buddy_unavaliable);
		    }

		    return rowView;
		  }
		} 