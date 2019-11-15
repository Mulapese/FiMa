package com.example.fimanavi.ui.pictures;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PicturesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PicturesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}