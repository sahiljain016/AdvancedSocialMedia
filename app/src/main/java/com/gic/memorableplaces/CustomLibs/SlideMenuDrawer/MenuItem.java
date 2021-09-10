package com.gic.memorableplaces.CustomLibs.SlideMenuDrawer;

import com.gic.memorableplaces.Adapters.FiltersRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuItem {
    //    String title;
//    int imageId;
//    int NoOfFilters;
    String CurrentFrag;
    ArrayList<String> filterNameList;
    ArrayList<Integer> IconList;
    ArrayList<String> SelectedFields;
    FiltersRecyclerViewAdapter.OnFilterClickListener onFilterClickListener;
    HashMap<String, Boolean> SelectedFiltersHashMap;

    public MenuItem() {
    }

    public MenuItem(String CurrentFrag, ArrayList<String> filterNameList, ArrayList<Integer> iconList, ArrayList<String> selectedFields,
                    FiltersRecyclerViewAdapter.OnFilterClickListener onFilterClickListener,
                    HashMap<String, Boolean> selectedFiltersHashMap) {
        this.filterNameList = filterNameList;
        IconList = iconList;
        SelectedFields = selectedFields;
        this.onFilterClickListener = onFilterClickListener;
        SelectedFiltersHashMap = selectedFiltersHashMap;
    }

    public String getCurrentFrag() {
        return CurrentFrag;
    }

    public void setCurrentFrag(String currentFrag) {
        CurrentFrag = currentFrag;
    }

    public ArrayList<String> getFilterNameList() {
        return filterNameList;
    }

    public void setFilterNameList(ArrayList<String> filterNameList) {
        this.filterNameList = filterNameList;
    }

    public ArrayList<Integer> getIconList() {
        return IconList;
    }

    public void setIconList(ArrayList<Integer> iconList) {
        IconList = iconList;
    }

    public ArrayList<String> getSelectedFields() {
        return SelectedFields;
    }

    public void setSelectedFields(ArrayList<String> selectedFields) {
        SelectedFields = selectedFields;
    }

    public FiltersRecyclerViewAdapter.OnFilterClickListener getOnFilterClickListener() {
        return onFilterClickListener;
    }

    public void setOnFilterClickListener(FiltersRecyclerViewAdapter.OnFilterClickListener onFilterClickListener) {
        this.onFilterClickListener = onFilterClickListener;
    }

    public HashMap<String, Boolean> getSelectedFiltersHashMap() {
        return SelectedFiltersHashMap;
    }

    public void setSelectedFiltersHashMap(HashMap<String, Boolean> selectedFiltersHashMap) {
        SelectedFiltersHashMap = selectedFiltersHashMap;
    }
}