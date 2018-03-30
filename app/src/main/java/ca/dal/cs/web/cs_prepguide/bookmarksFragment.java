package ca.dal.cs.web.cs_prepguide;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link bookmarksFragment.bookmarksFragmentInterface} interface
 * to handle interaction events.
 * Use the factory method to
 * create an instance of this fragment.
 */
public class bookmarksFragment extends Fragment {

    private bookmarksFragmentInterface mListener;
    private static final String TAG = "bookmarksFragment";

    private ListView listViewBookmarks;
    private ArrayList<String> bookmarksList;
    private ArrayAdapter bookmarksAdapter;
    CSPrepGuideSingleTon singleTonInstance;

    public bookmarksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        listViewBookmarks = view.findViewById(R.id.listViewBookmarks);
        singleTonInstance = CSPrepGuideSingleTon.getInstance(getContext());
//        bookmarksList = singleTon.getAppUser().getBookmarks();
        bookmarksList = new ArrayList<>();
        bookmarksList.add("a");
        Log.w(TAG, singleTonInstance.getAppUser().toString());
        bookmarksAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, bookmarksList);
        listViewBookmarks.setAdapter(bookmarksAdapter);

        listViewBookmarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, ""+position + bookmarksList.get(position));
                mListener.bookmarksItemClicked(position, bookmarksList.get(position));
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof bookmarksFragmentInterface) {
            mListener = (bookmarksFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface bookmarksFragmentInterface {
        // TODO: Update argument type and name
        void bookmarksItemClicked(int position, String id);
    }
}
