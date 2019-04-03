package com.example.brwtalk;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.brwtalk.Message;
import com.example.brwtalk.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Message> {
    private List<Message> messages = new ArrayList<>();
    private int layoutId;
    private LayoutInflater inflater;

    public ListViewAdapter(Context context, int layoutId,List<Message> messages) {
        super(context, layoutId, messages);
        this.messages = messages;
        this.layoutId = layoutId;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Message msg = messages.get(position);
        View listItem = (convertView == null)?inflater.inflate(this.layoutId,null):convertView;
        String s = msg.getUsername() + ":";
        ((TextView) listItem.findViewById(R.id.textViewUsername)).setText(s);
        String s2 = zweistelligeZeit(msg.getDate().getHours()) + ":" + zweistelligeZeit(msg.getDate().getMinutes());
        ((TextView) listItem.findViewById(R.id.textViewDate)).setText(s2);
        ((TextView) listItem.findViewById(R.id.textViewText)).setText(msg.getText());
        return listItem;
    }

    private String zweistelligeZeit(int s)
    {
        String result = String.valueOf(s);
        if(Integer.parseInt(result)<10)
        {
            result = "0" + s;
        }
        return result;
    }
}
