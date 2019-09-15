package com.jmelzer.myttr.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

/**
 */
public class SimInfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sim_info, container, false);
        if (getActivity() != null) {
            // Inflate the layout for this fragment
            View simView = view.findViewById(R.id.around_sim_view);
            if (MyApplication.simPlayer != null) {
                ((TextView) view.findViewById(R.id.player_name)).setText(MyApplication.simPlayer.getFullName());
                simView.setVisibility(View.VISIBLE);
            } else {
                simView.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }
}
