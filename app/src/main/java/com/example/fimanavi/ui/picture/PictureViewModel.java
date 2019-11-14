package com.example.fimanavi.ui.picture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PictureViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public PictureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is picture fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
