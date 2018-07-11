package ru.d9d.cupnews;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, List<News> newss) {
        super(context, 0, newss);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        News news = getItem(position);

        // News Title
        String title = news.getTitle();
        TextView tvTitle = listItemView.findViewById(R.id.title);
        tvTitle.setText(title);

        // News Author(s)
        String authors = news.getAuthor();
        TextView tvAuthors = listItemView.findViewById(R.id.author);
        if (authors.isEmpty()) tvAuthors.setVisibility(View.GONE);
        else tvAuthors.setText(authors);

        // News section
        String section = news.getSection();
        TextView tvSection = listItemView.findViewById(R.id.section);
        if (section.isEmpty()) tvSection.setVisibility(View.INVISIBLE);
        else tvSection.setText(section);

        // News date for item
        Date date = new Date(news.getDateMilliseconds());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        TextView tvDate = (TextView) listItemView.findViewById(R.id.date);
        tvDate.setText(dateFormatter.format(date));
        TextView tvTime = (TextView) listItemView.findViewById(R.id.time);
        tvTime.setText(timeFormatter.format(date));

        return listItemView;
    }

}