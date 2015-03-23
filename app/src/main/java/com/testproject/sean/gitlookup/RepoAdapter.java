package com.testproject.sean.gitlookup;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Sean on 3/22/2015.
 */
public class RepoAdapter extends ArrayAdapter<RepoActivity.RepoListItem> {

    private Context context;
    int resourceID;
    private LayoutInflater inflater;
    private List<RepoActivity.RepoListItem> items;

    public RepoAdapter(Context context, int textViewResourceID){
        super(context, textViewResourceID);
    }

    public RepoAdapter(Context context, int resourceID, List<RepoActivity.RepoListItem> items){
        super(context, resourceID, items);

        this.context = context;
        this.resourceID = resourceID;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        RepoActivity.RepoListItem item = getItem(position);

        //Only do heavy processing stuff if needed
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.repo_layout, parent, false);
            viewHolder.repoName = (TextView) convertView.findViewById(R.id.repo_name_text);
            viewHolder.forksCount = (TextView) convertView.findViewById(R.id.forks_count_text);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.repoName.setText(item.repo);
        viewHolder.forksCount.setText(item.fork_count);

        return convertView;
    }

    private static class ViewHolder{
        TextView repoName;
        TextView forksCount;
    }
}
