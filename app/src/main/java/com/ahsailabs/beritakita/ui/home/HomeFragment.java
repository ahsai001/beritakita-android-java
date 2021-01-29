package com.ahsailabs.beritakita.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ahsailabs.beritakita.NewsDetailActivity;
import com.ahsailabs.beritakita.R;
import com.ahsailabs.beritakita.configs.Config;
import com.ahsailabs.beritakita.ui.home.adapters.NewsAdapter;
import com.ahsailabs.beritakita.ui.home.models.News;
import com.ahsailabs.beritakita.ui.home.models.NewsListResponse;
import com.ahsailabs.beritakita.utils.HttpUtil;
import com.ahsailabs.beritakita.utils.InfoUtil;
import com.ahsailabs.beritakita.utils.SwipeRefreshLayoutUtil;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rvList;
    private ArrayList<News> newsList;
    private NewsAdapter newsAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayoutUtil swipeRefreshLayoutUtil;
    private ShimmerFrameLayout sflLoading;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadViews(view);
    }

    private void loadViews(View root) {
        rvList = root.findViewById(R.id.rvList);
        swipeRefreshLayout = root.findViewById(R.id.srlHome);
        sflLoading = root.findViewById(R.id.sflLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        setupListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayoutUtil.refreshNow();
    }

    private void setupViews() {
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList);
        rvList.setAdapter(newsAdapter);


        swipeRefreshLayoutUtil = SwipeRefreshLayoutUtil.init(swipeRefreshLayout, new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }

    private void setupListener() {
        //setup item click listener
        newsAdapter.setOnChildViewClickListener(new NewsAdapter.OnChildViewClickListener<News>() {
            @Override
            public void onClick(View view, News dataModel, int position) {
                InfoUtil.showSnackBar(view, "data ke-"+position+" clicked");
                NewsDetailActivity.start(view.getContext(), dataModel.getId());
            }

            @Override
            public void onLongClick(View view, News dataModel, int position) {

            }
        });
    }

    private void showLoading(){
        rvList.setVisibility(View.GONE);

        sflLoading.startShimmer();
        sflLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        rvList.setVisibility(View.VISIBLE);

        sflLoading.stopShimmer();
        sflLoading.setVisibility(View.GONE);
    }


    private void loadData() {
        showLoading();
        AndroidNetworking.post(Config.getNewsListUrl())
                .setOkHttpClient(HttpUtil.getCLient(getActivity()))
                .addBodyParameter("groupcode", Config.GROUP_CODE)
                .addBodyParameter("keyword", "")
                .setTag("newslist")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(NewsListResponse.class, new ParsedRequestListener<NewsListResponse>() {
                    @Override
                    public void onResponse(NewsListResponse response) {
                        if(response.getStatus() == 1){
                            List<News> resultList = response.getData();
                            //TODO: show listview dengan data resultList
                            newsList.clear();
                            newsList.addAll(resultList);
                            newsAdapter.notifyDataSetChanged();
                        } else {
                            //TODO: show info error
                            InfoUtil.showToast(getActivity(), response.getMessage());
                        }
                        swipeRefreshLayoutUtil.refreshDone();
                        hideLoading();
                    }

                    @Override
                    public void onError(ANError anError) {
                        //TODO: show info error
                        InfoUtil.showToast(getActivity(), anError.getMessage());
                        swipeRefreshLayoutUtil.refreshDone();
                        hideLoading();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.home_action_refresh){
            swipeRefreshLayoutUtil.refreshNow();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        AndroidNetworking.cancel("newslist");
        super.onDestroyView();
    }
}