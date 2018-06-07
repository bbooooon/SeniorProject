package com.example.seniorpj100per.Healthy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.seniorpj100per.R;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HealthyFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener, RssItemsAdapter.OnItemClickListener {

    private static final String KEY_FEED = "FEED";

    private String mFeedUrl;
    private RssItemsAdapter mAdapter;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.swRefresh)
    SwipeRefreshLayout mSwRefresh;

    public static HealthyFragment newInstance(String feedUrl) {
        HealthyFragment rssFragment = new HealthyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FEED, feedUrl);
        rssFragment.setArguments(bundle);
        return rssFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFeedUrl = getArguments().getString(KEY_FEED);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healthy, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new RssItemsAdapter(getActivity());
        mAdapter.setListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mSwRefresh.setOnRefreshListener(this);

        fetchRss();
        return view;
    }

    private void fetchRss() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://rssfeeds.sanook.com/rss/feeds/sanook/health.medicine.xml/")
                .addConverterFactory(RssConverterFactory.create())
                .build();

        showLoading();
        ServiceXML service = retrofit.create(ServiceXML.class);
        service.getRss(mFeedUrl)
                .enqueue(new Callback<RssFeed>() {
                    @Override
                    public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                        onRssItemsLoaded(response.body().getItems());
                        hideLoading();
                    }

                    @Override
                    public void onFailure(Call<RssFeed> call, Throwable t) {
                        Toast.makeText(getActivity(), "Failed to fetchRss RSS feed!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void onRssItemsLoaded(List<RssItem> rssItems) {
        mAdapter.setItems(rssItems);
        mAdapter.notifyDataSetChanged();
        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void showLoading() {
        mSwRefresh.setRefreshing(true);
    }

    public void hideLoading() {
        mSwRefresh.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        fetchRss();
    }

    @Override
    public void onItemSelected(RssItem rssItem) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.getLink()));
        startActivity(intent);
    }

}
