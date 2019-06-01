package com.android.group.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.airbnb.lottie.LottieAnimationView;
import com.android.group.R;
import com.android.group.model.bindle.BindleBusiness;
import com.android.group.network.constants.CategoryConstants;
import com.android.group.view.recyclerview.AddLocationAdapter;
import com.android.group.view.utils.SearchViewListFilter;
import com.android.group.viewmodel.NetworkViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public final class AddLocationFragment extends Fragment {

    private static final String TAG = "AddLocationFragment";
    private View rootView;
    private NetworkViewModel viewModel;
    private AddLocationAdapter adapter;
    private LottieAnimationView lottieAnimationView;
    private List<BindleBusiness> bindleBusinessesList = new ArrayList<>();
    private Disposable disposable;

    public AddLocationFragment() {
    }

    public static AddLocationFragment newInstance() {
        return new AddLocationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_location, container, false);
        findViews();
        getViewModel();
        initSearchView();
        initCategorySpinner();
        initRecyclerView();
        return rootView;
    }

    private void findViews() {
        lottieAnimationView = rootView.findViewById(R.id.bindle_color_load);
    }

    private void getViewModel() {
        viewModel = NetworkViewModel.getSingleInstance();
    }

    private void initSearchView() {
        SearchView searchView = rootView.findViewById(R.id.add_location_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                adapter.setData(SearchViewListFilter.getFilteredList(search, bindleBusinessesList));
                return false;
            }
        });
    }

    private void initCategorySpinner() {
        Spinner categorySpinner = rootView.findViewById(R.id.category_spinner);
        categorySpinner.setAdapter(ArrayAdapter.createFromResource(
                getContext(), R.array.category_array, android.R.layout.simple_dropdown_item_1line));
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lottieAnimationView.setVisibility(View.VISIBLE);
                bindleBusinessesList.clear();
                disposable = viewModel.makeBindleBusinessNetworkCall(CategoryConstants.CATEGORIES[position])
                        .doOnSubscribe(unit -> adapter.clear())
                        .subscribe(bindleBusiness -> {
                            bindleBusinessesList.add(bindleBusiness);
                            adapter.addData(bindleBusiness);
                            lottieAnimationView.setVisibility(View.GONE);
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = rootView.findViewById(R.id.add_location_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AddLocationAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
