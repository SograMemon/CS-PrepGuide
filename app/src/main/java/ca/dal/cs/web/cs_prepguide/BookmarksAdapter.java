package ca.dal.cs.web.cs_prepguide;

import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class BookmarksAdapter extends ArrayAdapter<String>{
    Context cntx;
    private ArrayList<String> bookmarksList;
    CSPrepGuideSingleTon userSingleTon = CSPrepGuideSingleTon.getInstance(cntx);
    guideNavigationInterface guideNavigationInterfaceObject;

    public BookmarksAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> bookmarksList, guideNavigationInterface guideNavigationInterfaceObject) {
        super(context, resource,bookmarksList);
        this.cntx = cntx;
        this.bookmarksList = bookmarksList;
        this.guideNavigationInterfaceObject = guideNavigationInterfaceObject;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bookmarks_and_skills_list_layout, null);
        }

        String i = bookmarksList.get(position);

        TextView txtBookmarks;
        ImageButton imgBtnDelete;

        if(i!= null){
            txtBookmarks = convertView.findViewById(R.id.txtBookmarks);
            imgBtnDelete = convertView.findViewById(R.id.imgBtnDelete);
            txtBookmarks.setText(i);
//            View bookmarksLinerLayout = convertView;
            imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent();
                    int position1 = listView.getPositionForView(parentRow);
                    Toast.makeText(getContext(), "delete clicked"+String.valueOf(position1), Toast.LENGTH_SHORT).show();
                    bookmarksList.remove(position1);
                    userSingleTon.addUserToFireBaseDB();
                    notifyDataSetChanged();
                }
            });

            final View finalConvertView = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(), "list view clicked", Toast.LENGTH_SHORT).show();
                    ListView listView = (ListView) finalConvertView.getParent();
                    int position1 = listView.getPositionForView(listView);
                    Toast.makeText(getContext(), "list view clicked"+bookmarksList.get(position), Toast.LENGTH_SHORT).show();
                    guideNavigationInterfaceObject.testGuideNavigation();
                }
            });
        }

        return convertView;
    }
}
