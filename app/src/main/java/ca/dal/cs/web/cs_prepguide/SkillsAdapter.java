package ca.dal.cs.web.cs_prepguide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for Listview that shows user skills in user profile page
 */
public class SkillsAdapter extends ArrayAdapter<String> {
    private Context cntx;
    private ArrayList<String> skillsList;

    // singleTon instance to retrieve and update user details
    CSPrepGuideSingleTon userSingleTon = CSPrepGuideSingleTon.getInstance(cntx);

    /**
     * Constructor
     */
    public SkillsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> skillsList) {
        super(context, resource, skillsList);
        this.cntx = cntx;
        this.skillsList = skillsList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bookmarks_and_skills_list_layout, null);
        }

        String i = skillsList.get(position);

        //UI Components
        TextView txtBookmarks;
        ImageButton imgBtnDelete;

        if (i != null) {
            txtBookmarks = convertView.findViewById(R.id.txtBookmarks);
            imgBtnDelete = convertView.findViewById(R.id.imgBtnDelete);
            txtBookmarks.setText(i);

            // Method to delete skills from user details
            imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent();
                    int position1 = listView.getPositionForView(parentRow);
                    skillsList.remove(position1);

                    // Push the changes to firebase
                    userSingleTon.addUserToFireBaseDB();

                    // Update the listview
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }
}
