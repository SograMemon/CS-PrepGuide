package ca.dal.cs.web.cs_prepguide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by vamshikrishnamoogala on 2018-03-31.
 */

/**
 * Adapter for comments that are shown in comments page for a job guide in Guide screen
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {
    Context cntx;
    private ArrayList<Comment> CommentsList;

    // singleTon instance to retrieve and update user details
    CSPrepGuideSingleTon userSingleTon = CSPrepGuideSingleTon.getInstance(cntx);

    // singleTon instance to retrieve and update post details
    PostSingleTon postSingleTonInstance = PostSingleTon.getInstance(cntx);

    /**
     * Constructor
     */
    public CommentsAdapter(@NonNull Context context, int layoutId, ArrayList<Comment> CommentsList) {
        super(context, layoutId, CommentsList);
        this.CommentsList = CommentsList;
        this.cntx = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comments_layout_others, null);
        }

        Comment i = CommentsList.get(position);

        //UI Components
        final Button btnEditComment;
        TextView txtUserName;
        final EditText editTxtComment;
        Button btnDeleteComment = null;
        final int[] currentPosition = new int[1];

        if (i != null) {
            // If comment is added by the current logged in user, setting the layout to be on the right side
            // else on the left side
            if (i.getCommentedByUser().equals(userSingleTon.getAppUser().getId())) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.comments_layout_self, null);
            } else {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.comments_layout_others, null);
            }

            txtUserName = convertView.findViewById(R.id.txtUserName);
            editTxtComment = convertView.findViewById(R.id.editTxtComment);
            btnEditComment = convertView.findViewById(R.id.btnEditComment);

            // Hiding the edit and delete buttons for comments that are not added by current user
            btnEditComment.setVisibility(View.INVISIBLE);
            btnDeleteComment = convertView.findViewById(R.id.btnDeleteComment);
            btnDeleteComment.setVisibility(View.INVISIBLE);

            // Editing comments logic
            btnEditComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnEditComment.getText().toString().equals("save")) {
                        editTxtComment.setEnabled(false);
                        btnEditComment.setText("edit");
                    } else {
                        btnEditComment.setText("save");
                        editTxtComment.setEnabled(true);
                        View parentRow = (View) v.getParent();
                        ListView listView = (ListView) parentRow.getParent().getParent().getParent();
                        currentPosition[0] = listView.getPositionForView(parentRow);
                        CommentsList.get(currentPosition[0]).setCommentContent(editTxtComment.getText().toString());

                        // Updating the edited details in singleTonInstance and in Firebase
                        postSingleTonInstance.getPost().setComments(CommentsList);
                        postSingleTonInstance.addCommentsToFireBaseDB();
                    }
                }
            });


//            convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    Toast.makeText(getContext(), "focus changed", Toast.LENGTH_SHORT).show();
////                    if(editTxtComment.isEnabled()){
////                        btnEditComment.setText("edit");
////                        editTxtComment.setEnabled(false);
////                    }
//                }
//            });

            // Deleting comments logic
            btnDeleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int position = (Integer)

                    //https://stackoverflow.com/questions/20541821/get-listview-item-position-on-button-click
                    View parentRow = (View) v.getParent();
                    ListView listView = (ListView) parentRow.getParent().getParent().getParent();
                    int position1 = listView.getPositionForView(parentRow);
//                    Toast.makeText(getContext(), "delete clicked"+String.valueOf(position1), Toast.LENGTH_SHORT).show();
                    CommentsList.remove(position1);
                    postSingleTonInstance.addCommentsToFireBaseDB();
                    notifyDataSetChanged();
                }
            });

            // Showing display name as userId when name is not present
            if (i.getCommentedByUserName().equals("")) {
                txtUserName.setText(i.getCommentedByUser());
            } else {
                txtUserName.setText(i.getCommentedByUserName());
            }

            editTxtComment.setText(i.getCommentContent());
            editTxtComment.setEnabled(false);

            if (i.getCommentedByUser().equals(userSingleTon.getAppUser().getId())) {
                btnEditComment.setVisibility(View.VISIBLE);
                btnDeleteComment.setVisibility(View.VISIBLE);
            }

        }

        return convertView;
    }
}
