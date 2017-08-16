package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    static class ViewHolder {
        private TextView sectionTextView;
        private TextView headlineTextView;
        private TextView dateTextView;
        private TextView timeTextView;
    }

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        ViewHolder holder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        News currentNewsItem = getItem(position);

        String originalDate = currentNewsItem.getDate();
        String finalDate;
        String finalTime;

        String[] parts = originalDate.split("T");
        finalDate = parts[0];
        finalTime = parts[1];
        String finalTimeLessZ = finalTime.substring(0, finalTime.lastIndexOf("Z"));

        holder = new ViewHolder();
        //section
        holder.sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);
        holder.sectionTextView.setText(currentNewsItem.getSection());
        //headline
        holder.headlineTextView = (TextView) listItemView.findViewById(R.id.headline_text_view);
        holder.headlineTextView.setText(currentNewsItem.getTitle());
        //date
        holder.dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        holder.dateTextView.setText(finalDate);
        //time
        holder.timeTextView = (TextView) listItemView.findViewById(R.id.time_text_view);
        holder.timeTextView.setText(finalTimeLessZ);

        return listItemView;
    }
}
