package eu.uberdust.myfragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import eu.uberdust.model.Room;
import eu.uberdust.uClient.R;

public final class MyCapabilitiesFragment extends Fragment {

    Room room;
    Activity thatActivity;

    public static MyCapabilitiesFragment newInstance(String content, Room iroom, Activity thisActivity) {
        MyCapabilitiesFragment fragment = new MyCapabilitiesFragment();
        fragment.room = iroom;
        fragment.thatActivity = thisActivity;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView list = new ListView(getActivity());

        MyCapabilitiesAdapter adapter = new MyCapabilitiesAdapter(getActivity(), R.layout.list_row_capabilities, room.getCapabilities(), thatActivity);
        list.setAdapter(adapter);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.addView(list);

        return layout;
    }

}
