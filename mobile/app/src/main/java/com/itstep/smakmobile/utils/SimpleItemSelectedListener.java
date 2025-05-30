package com.itstep.smakmobile.utils;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

    private final Runnable onSelected;

    public SimpleItemSelectedListener(Runnable onSelected) {
        this.onSelected = onSelected;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onSelected.run();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}

