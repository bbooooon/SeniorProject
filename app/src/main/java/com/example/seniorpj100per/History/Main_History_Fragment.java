package com.example.seniorpj100per.History;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seniorpj100per.History.Month.Month_HistoryFragment;
import com.example.seniorpj100per.R;

/**
 * Created by Smew on 3/2/2561.
 */

public class Main_History_Fragment extends Fragment {

    private FragmentTabHost mTabHost;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_main_history, container, false);

        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.contenttain);

        mTabHost.addTab(mTabHost.newTabSpec("วัน").setIndicator("วัน"),
                Day_HistoryFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("เดือน").setIndicator("เดือน"),
                Month_HistoryFragment.class, null);

        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}
