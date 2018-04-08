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
 * Adapter for Listview that shows user bookmarks in bookmarks page
 */
public class BookmarksAdapter extends ArrayAdapter<String> {
    Context cntx;
    private ArrayList<String> bookmarksList;

    // singleTon instance to retrieve and update user details
    CSPrepGuideSingleTon userSingleTon = CSPrepGuideSingleTon.getInstance(cntx);

    // GuideNavigationInterface object to call the method navigateToGuideFragment to navigate the user
    // from bookmarks page to Guides page
    GuideNavigationInterface guideNavigationInterfaceObject;

    /**
     * Constructor
     */
    public BookmarksAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> bookmarksList, GuideNavigationInterface guideNavigationInterfaceObject) {
        super(context, resource, bookmarksList);
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

        //UI Components
        TextView txtBookmarks;
        ImageButton imgBtnDelete;

        if (i != null) {
            txtBookmarks = convertView.findViewById(R.id.txtBookmarks);
            imgBtnDelete = convertView.findViewById(R.id.imgBtnDelete);

            String temp;

            // Name mapping for guides based on postId
            if (bookmarksList.get(position).equals("post1")) {
                temp = "Data Scientist - Microsoft";
            } else if (bookmarksList.get(position).equals("post2")) {
                temp = "Software Developer- Microsoft";
            } else {
                temp = "Software Architect - Microsoft";
            }
            txtBookmarks.setText(temp);


            // Method to delete bookmarks from user details
            imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent();
                    int position1 = listView.getPositionForView(parentRow);
                    bookmarksList.remove(position1);

                    // Push the changes to firebase
                    userSingleTon.addUserToFireBaseDB();

                    // Update the listview
                    notifyDataSetChanged();
                }
            });


            final View finalConvertView = convertView;
            // Method to navigate the user to guide page on clicking a bookmark
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListView listView = (ListView) finalConvertView.getParent();
                    int position1 = listView.getPositionForView(listView);
                    userSingleTon.setCurrentPostId(bookmarksList.get(position));
                    guideNavigationInterfaceObject.navigateToGuideFragment();
                }
            });
        }

        return convertView;
    }
}
