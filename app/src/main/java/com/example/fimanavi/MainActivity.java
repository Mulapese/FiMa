package com.example.fimanavi;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.fimanavi.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private MenuItem newFolder;
    private MenuItem refresh;
    private static final int REQUEST_PERMISSIONS = 1234;
    private static int PERMISSION_COUNT = 2;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        newFolder = menu.findItem(R.id.btnNewFolder);
        refresh = menu.findItem(R.id.refresh);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final HomeFragment homeFragment = new HomeFragment();
        final String currentPath = homeFragment.currentPath;
        final EditText input = new EditText(this);

        switch (item.getItemId()) {
            case R.id.btnNewFile:
                AlertDialog.Builder newFileDialog = new AlertDialog.Builder(this);
                newFileDialog.setTitle("New File");
                final EditText filename = new EditText(this);
                final EditText contextBox = new EditText(this);
                filename.setHint("Title: ");
                contextBox.setHint("Context: ");
                filename.setInputType(InputType.TYPE_CLASS_TEXT);
                contextBox.setInputType(InputType.TYPE_CLASS_TEXT);

                LinearLayout lay = new LinearLayout(this);
                lay.setOrientation(LinearLayout.VERTICAL);
                lay.addView(filename);
                lay.addView(contextBox);
                newFileDialog.setView(lay);

                newFileDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileOutputStream fos = null;
                        try {
                            File file = new File(currentPath + "/" +  filename.getText().toString() + ".txt");
                            file.createNewFile();
                            if (!file.exists()) {
                                fos = new FileOutputStream(file);
                                fos.write(contextBox.getText().toString().getBytes());
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        contextBox.setText("");
                        Snackbar.make(getWindow().getDecorView(), filename.getText().toString() + ".txt saved!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
//                        File file = new File(contextBox.getFilesDir(), filename);
//                        final File newFolder = new File(currentPath + "/" + input.getText()+".txt");
//                        if (!newFolder.exists()) {
//                            newFolder.mkdir();
//                        }
                    }
                });
                newFileDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newFileDialog.show();
                return true;
            case R.id.btnNewFolder:
//                Snackbar.make(getWindow().getDecorView(), currentPath, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                AlertDialog.Builder newFolderDialog = new AlertDialog.Builder(this);
                newFolderDialog.setTitle("New Folder");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                newFolderDialog.setView(input);
                newFolderDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final File newFolder = new File(currentPath + "/" + input.getText());
                        if (!newFolder.exists()) {
                            newFolder.mkdir();
                        }
                    }
                });
                newFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newFolderDialog.show();
                return true;
            case R.id.refresh:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            HomeFragment homeFragment = new HomeFragment();
//            homeFragment.myOnKeyDown(keyCode);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean arePermissionDenied() {
        int p = 0;
        while (p < PERMISSION_COUNT) {
            if (checkSelfPermission(PERMISSIONS[p]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            p++;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            } else {
                onResume();
            }
        }
    }
}
