package ca.dal.cs.web.cs_prepguide;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static ca.dal.cs.web.cs_prepguide.R.*;
import static ca.dal.cs.web.cs_prepguide.R.layout.list_layout;

/**
 * Created by SM on 3/5/18.
 */

public class jobList extends ArrayAdapter<job> {

    private Activity context;
    private List<job> jobList;




    public jobList(Activity context, List<job> jobList){
        super(context, layout.list_layout, jobList);

        this.context= context;
        this.jobList= jobList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= context.getLayoutInflater();
        View listViewItem = inflater.inflate(list_layout,null,true);
        TextView textViewTitle = (TextView) listViewItem.findViewById(id.txt_title);
        TextView textViewStream = (TextView) listViewItem.findViewById(id.txt_stream);
        TextView textViewType = (TextView) listViewItem.findViewById(id.txt_type);
        TextView textViewCompany = (TextView) listViewItem.findViewById(id.txt_Company);
        TextView textViewMatchSkill = (TextView) listViewItem.findViewById(id.txt_match);
        job job1= jobList.get(position);

        textViewCompany.setText(job1.getJobCompany());
        textViewStream.setText(job1.getJobStream());
        textViewTitle.setText(job1.getJobTitle());
        textViewType.setText(job1.getJobType());
        textViewMatchSkill.setText(job1.getMatchSkill());

        return listViewItem;

    }
}

