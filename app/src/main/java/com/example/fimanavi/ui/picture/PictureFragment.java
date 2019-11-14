package com.example.fimanavi.ui.picture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.fimanavi.R;

public class PictureFragment extends Fragment {
    private PictureViewModel pictureViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pictureViewModel =
                ViewModelProviders.of(this).get(PictureViewModel.class);
        View root = inflater.inflate(R.layout.fragment_picture, container, false);
        final TextView textView = root.findViewById(R.id.text_picture);
        pictureViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
