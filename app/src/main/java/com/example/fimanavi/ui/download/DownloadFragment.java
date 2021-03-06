package com.example.fimanavi.ui.download;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fimanavi.Common;
import com.example.fimanavi.Constant;
import com.example.fimanavi.FileUtils;
import com.example.fimanavi.ListAdapter;
import com.example.fimanavi.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DownloadFragment extends Fragment {

    private DownloadViewModel downloadViewModel;
    private boolean isFileManagerInitialized;
    private boolean[] selection;
    private File[] files;
    private List<String> filesList;
    private int filesFoundCount;
    private ImageButton refreshButton;
    private ImageButton btnAZ;
    private File dir;
    public String currentPath;
    private boolean isLongClick;
    private int selectedItemIndex;
    private String copyPath;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        downloadViewModel =
                ViewModelProviders.of(this).get(DownloadViewModel.class);
        View root = inflater.inflate(R.layout.fragment_download, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Common.arePermissionDenied(this.getContext())) {
            requestPermissions(Constant.PERMISSIONS, Constant.REQUEST_PERMISSIONS);
            return;
        }

        if (!isFileManagerInitialized) {
            currentPath = Constant.DOWNLOAD_DIRECTORY;
            final String rootPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            //final TextView pathOutput = getView().findViewById(R.id.pathOutput);
            final ListView listView = getView().findViewById(R.id.androidList);
            final ListAdapter textAdapter1 = new ListAdapter();

            filesList = new ArrayList<String>();

            // Refresh Button
            refreshButton = getView().findViewById(R.id.refresh);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pathOutput.setText(currentPath.substring(currentPath.lastIndexOf('/') + 1));
                    //pathOutput.setText(currentPath);
                    dir = new File(currentPath);
                    files = dir.listFiles();
                    Arrays.sort(files);
                    filesFoundCount = files.length;
                    selection = new boolean[files.length];
                    //textAdapter1.setData(getContext(),filesList, filesList, filesList);
                    filesList.clear();
                    for (int i = 0; i < filesFoundCount; i++) {
                        filesList.add(String.valueOf(files[i].getAbsolutePath()));
                    }
                    String[] filesArray = new String[filesList.size()];
                    filesArray = filesList.toArray(filesArray);

                    textAdapter1.setData(getActivity(), filesArray, FileUtils.getLastModified(files), FileUtils.getIcon(files));
                    listView.setAdapter(textAdapter1);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Common.minimumPath(currentPath));
                }
            });

            // Search Button
            ImageButton btnSearch = getView().findViewById(R.id.btnSearch);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText input = new EditText(getContext());

                    AlertDialog.Builder newFolderDialog = new AlertDialog.Builder(getContext());
                    newFolderDialog.setTitle("Search Box");
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    newFolderDialog.setView(input);
                    newFolderDialog.setPositiveButton("Search Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<String> result = new ArrayList<>();
                            for(int i = 0; i < filesList.size(); i++){
                                if(FileUtils.getName(files[i]).contains(input.getText().toString())){
                                    result.add(FileUtils.getName(files[i]));
                                }
                            }

                            String[] filesArray = new String[result.size()];
                            filesArray = result.toArray(filesArray);
                            textAdapter1.setData(getActivity(), filesArray, FileUtils.getLastModified(files), FileUtils.getIcon(files));
                        }
                    });
                    newFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    newFolderDialog.show();
                }
            });

            // Sort AZ
            btnAZ = getView().findViewById(R.id.btnAZ);
            btnAZ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.reverseFileArray(files);
                    Collections.reverse(filesList);
                    String[] filesArray = new String[filesList.size()];
                    filesArray = filesList.toArray(filesArray);
                    textAdapter1.setData(getActivity(), filesArray, FileUtils.getLastModified(files), FileUtils.getIcon(files));
                }
            });

            refreshButton.callOnClick();

            // Go Back Button
            final ImageButton goBackButton = getView().findViewById(R.id.goBack);
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
//                                    newIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                    String mimeType = myMime.getMimeTypeFromExtension(FileUtils.getExtension(files[position].getAbsolutePath()));
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
                    for (boolean aSelection : selection) {
                        if (aSelection) {
                            selectionCount++;
                        }
                    }

                    if (selectionCount > 0) {
                        selectedItemIndex = position;
                        getView().findViewById(R.id.btnRename).setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
                        getView().findViewById(R.id.btnInfo).setVisibility(View.VISIBLE);
                        if (!files[selectedItemIndex].isDirectory()) {
                            getView().findViewById(R.id.btnCopy).setVisibility(View.VISIBLE);
                        }
                    } else {
                        getView().findViewById(R.id.btnCopy).setVisibility(View.GONE);
                        getView().findViewById(R.id.btnRename).setVisibility(View.GONE);
                        getView().findViewById(R.id.btnDelete).setVisibility(View.GONE);
                        getView().findViewById(R.id.btnInfo).setVisibility(View.GONE);
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
            final ImageButton btnDelete = getView().findViewById(R.id.btnDelete);
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
                                    FileUtils.deleteFileOrFolder(files[i]);
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

            // Properties button
            final ImageButton btnInfo = getView().findViewById(R.id.btnInfo);
            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = "";
                    String name = "";
                    int size = 0;
                    String lastModified = "";
                    String extension = "";
                    String type = "";
                    for (int i = 0; i < files.length; i++) {
                        if (selection[i]) {
                            path = files[i].getAbsolutePath();
                            name = FileUtils.getName(files[i]);
                            size = FileUtils.getSize(files[i]);
                            lastModified = FileUtils.getLastModified(files[i]);
                            extension = FileUtils.getExtension(files[i].getAbsolutePath());
                            type = FileUtils.getType(files[i]);
                        }
                    }
                    final AlertDialog.Builder propertiesDialog = new AlertDialog.Builder(getActivity());
                    propertiesDialog.setTitle(name);
                    propertiesDialog.setMessage("\nPath: " + path + "\n\n"
                            + "Size: " + size
                            + "B\n\nLast Modified: " + lastModified + "\n\n"
                            + "Extension: " + extension + "\n\n"
                            + "Type: " + type);
                    propertiesDialog.show();
                }
            });

            // Rename Button
            final ImageButton renameButton = getView().findViewById(R.id.btnRename);
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
            final ImageButton copyButton = getView().findViewById(R.id.btnCopy);
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
            final ImageButton pasteButton = getView().findViewById(R.id.btnPaste);
            pasteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pasteButton.setVisibility(View.GONE);
                    String dstPath = currentPath + copyPath.substring(copyPath.lastIndexOf('/'));
                    FileUtils.copy(new File(copyPath), new File(dstPath));
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

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permission, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == Constant.REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (Common.arePermissionDenied(this.getContext())) {
                //((ActivityManager) Objects.requireNonNull(getActivity().getSystemService(Context.ACTIVITY_SERVICE))).clearApplicationUserData();
                //recreate();
                getFragmentManager()
                        .beginTransaction()
                        .detach(DownloadFragment.this)
                        .attach(DownloadFragment.this)
                        .commit();
            } else {
                onResume();
            }
        }
    }
}