package com.android.group.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.group.R;
import com.android.group.model.Venue;
import com.android.group.network.constants.CategoryConstants;
import com.android.group.view.recyclerview.AddLocationAdapter;
import com.android.group.viewmodel.NetworkViewModel;

import java.util.List;

public class AddLocationFragment extends Fragment {

    private static final String TAG = "AddLocationFragment";
    private View rootView;
    private NetworkViewModel viewModel;
    private AddLocationAdapter adapter;

    public AddLocationFragment() {}

    public static AddLocationFragment newInstance(){
        return new AddLocationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_location, container, false);
        getViewModel();
        setViewModelListener();
        initSearchView();
        initCategorySpinner();
        initRecyclerView();
        return rootView;
    }

    private void getViewModel() {
        viewModel = NetworkViewModel.getSingleInstance();
    }

    private void setViewModelListener() {
        viewModel.setOnDataLoadedListener(new NetworkViewModel.OnDataLoadedListener() {
            @Override
            public void dataLoaded(List<Venue> venues) {
                Log.d(TAG, "dataLoaded: " + venues.get(0).getName());
                adapter.setData(venues);
            }
        });
    }

    private void initSearchView() {
        SearchView searchView = rootView.findViewById(R.id.add_location_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
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
                viewModel.makeNetworkCall(CategoryConstants.CATEGORIES[position]);
                Log.d(TAG, "onItemClick: " + CategoryConstants.CATEGORIES[position]);
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

}