package com.example.fimanavi.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fimanavi.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private boolean isFileManagerInitialized;
    private boolean[] selection;
    private File[] files;
    private List<String> filesList;
    private int filesFoundCount;
    private Button refreshButton;
    private File dir;
    public String currentPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    private boolean isLongClick;
    private int selectedItemIndex;
    private String copyPath;
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final int MAX_LENGTH_TITLE = 29;
    private static int PERMISSION_COUNT = 2;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
   // public Button createNewFolder;

    private boolean arePermissionDenied() {
        int p = 0;
        while (p < PERMISSION_COUNT) {
            if (ActivityCompat.checkSelfPermission(getContext(), PERMISSIONS[p]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            p++;
        }
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.getView().findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //createNewFolder = getView().findViewById(R.id.newFolder);
    }

    // Get extension of file
    private String fileExt(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        final String rootPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (currentPath.equals(rootPath)) {
//                this.finish();
//                System.exit(0);
//            }
//            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
//            refreshButton.callOnClick();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
    public void myOnKeyDown(int keyCode) {
        final String rootPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentPath.equals(rootPath)) {
                //this.finish();
                //System.exit(0);
            }
            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            refreshButton.callOnClick();
        }
    }

    // Minimum the path if it is too long
    public String minimumPath(String s){
        if(s.length() > MAX_LENGTH_TITLE){
            int start = s.lastIndexOf("/");
            int end = s.length();
            s = s.substring(0,9) + "..." + s.substring(start, end);
        }
        return s;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }

        if (!isFileManagerInitialized) {
            currentPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            final String rootPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            final TextView pathOutput = getView().findViewById(R.id.pathOutput);
            final ListView listView = getView().findViewById(R.id.listView);
            final TextAdapter textAdapter1 = new TextAdapter();
            listView.setAdapter(textAdapter1);
            filesList = new ArrayList<>();
            // Refresh Button
            refreshButton = getView().findViewById(R.id.refresh);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pathOutput.setText(currentPath.substring(currentPath.lastIndexOf('/') + 1));
                    pathOutput.setText(currentPath);
                    dir = new File(currentPath);
                    files = dir.listFiles();
                    filesFoundCount = files.length;
                    selection = new boolean[files.length];
                    textAdapter1.setData(filesList);
                    filesList.clear();
                    for (int i = 0; i < filesFoundCount; i++) {
                        filesList.add(String.valueOf(files[i].getAbsolutePath()));
                    }
                    textAdapter1.setData(filesList);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(minimumPath(currentPath));
                }
            });

            refreshButton.callOnClick();

            // Go Back Button
            final Button goBackButton = getView().findViewById(R.id.goBack);
            goBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPath.equals(rootPath)) {
                        return;
                    }
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                    refreshButton.callOnClick();
                }
            });

            // Load các files và folder của một folder khi nhấn vào nó
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isLongClick) {
                                // Open folder
                                if (files[position].isDirectory()) {
                                    currentPath = files[position].getAbsolutePath();
                                    refreshButton.callOnClick();
                                }
                                // Open file
                                else {
                                    MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                                    String mimeType = myMime.getMimeTypeFromExtension(fileExt(files[position].getAbsolutePath()));
                                    newIntent.setDataAndType(Uri.parse(files[position].getAbsolutePath()), mimeType);
                                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(newIntent);
                                }
                            }
                        }
                    }, 50);
                }
            });


            // Bấm giữ để chọn item
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    isLongClick = true;
                    selection[position] = !selection[position];
                    textAdapter1.setSelection(selection);
                    int selectionCount = 0;
                    getView().findViewById(R.id.bottomBar).setVisibility(View.GONE);
                    for (boolean aSelection : selection) {
                        if (aSelection) {
                            selectionCount++;
                        }
                    }
                    if (selectionCount > 0) {
                        if (selectionCount == 1) {
                            selectedItemIndex = position;
                            getView().findViewById(R.id.btnRename).setVisibility(View.VISIBLE);
                            if (!files[selectedItemIndex].isDirectory()) {
                                getView().findViewById(R.id.btnCopy).setVisibility(View.VISIBLE);
                            }
                        } else {
                            getView().findViewById(R.id.btnCopy).setVisibility(View.GONE);
                            getView().findViewById(R.id.btnRename).setVisibility(View.GONE);
                        }
                        getView().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                    } else {
                        getView().findViewById(R.id.bottomBar).setVisibility(View.GONE);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isLongClick = false;
                        }
                    }, 1000);
                    return false;
                }
            });

            // Delete button
            final Button btnDelete = getView().findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                    deleteDialog.setTitle("Delete");
                    deleteDialog.setMessage("Do you really want to delete it?");
                    deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < files.length; i++) {
                                if (selection[i]) {
                                    deleteFileOrFolder(files[i]);
                                    selection[i] = false;
                                }
                            }
                            refreshButton.callOnClick();
                        }
                    });
                    deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            refreshButton.callOnClick();
                        }
                    });
                    deleteDialog.show();
                }
            });

//            // Create a new folder
//            createNewFolder.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final AlertDialog.Builder newFolderDialog = new AlertDialog.Builder(getActivity());
//                    newFolderDialog.setTitle("New Folder");
//                    final EditText input = new EditText(getActivity());
//                    input.setInputType(InputType.TYPE_CLASS_TEXT);
//                    newFolderDialog.setView(input);
//                    newFolderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            final File newFolder = new File(currentPath + "/" + input.getText());
//                            if (!newFolder.exists()) {
//                                newFolder.mkdir();
//                                refreshButton.callOnClick();
//                            }
//                        }
//                    });
//                    newFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    newFolderDialog.show();
//                }
//            });

            // Rename Button
            final Button renameButton = getView().findViewById(R.id.btnRename);
            renameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder renameDialog = new AlertDialog.Builder(getActivity());
                    renameDialog.setTitle("Rename to:");
                    final EditText input = new EditText(getActivity());
                    final String renamePath = files[selectedItemIndex].getAbsolutePath();
                    input.setText(renamePath.substring(renamePath.lastIndexOf('/')));
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    renameDialog.setView(input);
                    renameDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s = new File(renamePath).getParent() + "/" + input.getText();
                            File newFile = new File(s);
                            new File(renamePath).renameTo(newFile);
                            refreshButton.callOnClick();
                            selection = new boolean[files.length];
                            textAdapter1.setSelection(selection);
                        }
                    });
                    renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            refreshButton.callOnClick();
                        }
                    });
                    renameDialog.show();
                }
            });

            //Copy Button
            final Button copyButton = getView().findViewById(R.id.btnCopy);
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyPath = files[selectedItemIndex].getAbsolutePath();
                    selection = new boolean[files.length];
                    textAdapter1.setSelection(selection);
                    getView().findViewById(R.id.btnPaste).setVisibility(View.VISIBLE);
                }
            });

            // Paste Button
            final Button pasteButton = getView().findViewById(R.id.btnPaste);
            pasteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pasteButton.setVisibility(View.GONE);
                    String dstPath = currentPath + copyPath.substring(copyPath.lastIndexOf('/'));
                    copy(new File(copyPath), new File(dstPath));
//                    files = new File(currentPath).listFiles();
//                    selection = new boolean[files.length];
//                    textAdapter1.setSelection(selection);
                    refreshButton.callOnClick();
                }
            });
            isFileManagerInitialized = true;
        } else {
            refreshButton.callOnClick();
        }
    }


    private void copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class TextAdapter extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        private boolean[] selection;

        public void setData(List<String> data) {
            if (data != null) {
                this.data.clear();
                if (data.size() > 0) {
                    this.data.addAll(data);
                }
                notifyDataSetChanged();
            }
        }

        void setSelection(boolean[] selection) {
            if (selection != null) {
                this.selection = new boolean[selection.length];
                for (int i = 0; i < selection.length; i++) {
                    this.selection[i] = selection[i];
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.textItem)));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            final String item = getItem(position);
            holder.info.setText(item.substring(item.lastIndexOf('/') + 1));
            if (selection != null) {
                if (selection[position]) {
                    holder.info.setBackgroundColor(Color.argb(100, 8, 5, 5)); // Màu của Item khi được select
                } else {
                    holder.info.setBackgroundColor(Color.WHITE); // Màu của Item khi bỏ select
                }
            }
            return convertView;
        }

        class ViewHolder {
            TextView info;

            ViewHolder(TextView info) {
                this.info = info;
            }
        }
    }

    private void deleteFileOrFolder(File fileOrFolder) {
        if (fileOrFolder.isDirectory()) {
            if (fileOrFolder.list().length == 0) {
                fileOrFolder.delete();
            } else {
                String files[] = fileOrFolder.list();
                for (String temp : files) {
                    File fileToDelete = new File(fileOrFolder, temp);
                    deleteFileOrFolder(fileToDelete);
                }
                if (fileOrFolder.list().length == 0) {
                    fileOrFolder.delete();
                }
            }
        } else {
            fileOrFolder.delete();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permission, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionDenied()) {
                //((ActivityManager) Objects.requireNonNull(getActivity().getSystemService(Context.ACTIVITY_SERVICE))).clearApplicationUserData();
                //recreate();
                getFragmentManager()
                        .beginTransaction()
                        .detach(HomeFragment.this)
                        .attach(HomeFragment.this)
                        .commit();
            } else {
                onResume();
            }
        }
    }
}