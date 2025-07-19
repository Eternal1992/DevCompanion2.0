package com.ethereon.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> selectedModelPath = new MutableLiveData<>();

    public void setSelectedModelPath(String path) {
        selectedModelPath.setValue(path);
    }

    public LiveData<String> getSelectedModelPath() {
        return selectedModelPath;
    }
}