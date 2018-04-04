package ca.dal.cs.web.cs_prepguide;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static ca.dal.cs.web.cs_prepguide.R.*;
import static ca.dal.cs.web.cs_prepguide.R.layout.list_layout;
import static com.facebook.FacebookSdk.getApplicationContext;

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

        job job1= jobList.get(position);

        if(job1.jobId != null){
            TextView textViewTitle = (TextView) listViewItem.findViewById(id.txt_title);
            TextView textViewStream = (TextView) listViewItem.findViewById(id.txt_stream);
            TextView textViewType = (TextView) listViewItem.findViewById(id.txt_type);
            TextView textViewCompany = (TextView) listViewItem.findViewById(id.txt_Company);
            TextView textViewMatchSkill = (TextView) listViewItem.findViewById(id.txt_match);

            ImageView logo = listViewItem.findViewById(R.id.logo);

            textViewCompany.setText(job1.getJobCompany());
            textViewStream.setText(job1.getJobStream());
            textViewTitle.setText(job1.getJobTitle());
            textViewType.setText(job1.getJobType());
            textViewMatchSkill.setText(job1.getJobFilter());

            if (job1.getJobCompany().equals("Google"))
            Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/companyLogo%2FCompnayLogosSquarGoogle.jpg?alt=media&token=a4b631b1-f975-4493-aeef-a5c5525c26a7").fit().centerCrop().into(logo);

            if (job1.getJobCompany().equals("Facebook"))
                Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/companyLogo%2FCompnayLogosSquarFB.jpg?alt=media&token=62803779-3eb9-4c4a-a692-358af5febffd").fit().centerCrop().into(logo);

            if (job1.getJobCompany().equals("Microsoft"))
                Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/cs-prepguide.appspot.com/o/companyLogo%2Fif_Microsoft_New_Logo_98785.png?alt=media&token=13feaac8-b909-4e49-992f-8c8d616bac1b").fit().centerCrop().into(logo);

        }

        return listViewItem;

    }
}

