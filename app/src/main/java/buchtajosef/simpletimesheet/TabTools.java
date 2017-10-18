package buchtajosef.simpletimesheet;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ibm.icu.text.SimpleDateFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;


public class TabTools extends mFragment {

    private Button exportButton, backupButton, restoreButton;
    private ArrayList<File> dbFiles = new ArrayList<>();

    public TabTools() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_tools,container,false);
        setBackground(v);
        myTimeSheetDB =  new MySQLiteHelper(getActivity());

        exportButton = (Button) v.findViewById(R.id.tab_tools_button_export);
        backupButton = (Button) v.findViewById(R.id.tab_tools_button_backup);
        restoreButton = (Button) v.findViewById(R.id.tab_tools_button_restore);

        ViewPagerAdapter.fragments.put(3,this);
        setupButtons();
        return v;
    }

    private void setupButtons () {
        assert exportButton != null;
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickExport();
            }
        });
        assert backupButton != null;
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBackup();
            }
        });
        assert restoreButton != null;
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRestore();
            }
        });
    }

    private void onClickExport () {
        Intent intent = new Intent(getContext(), ExportActivity.class);
        startActivity(intent);
    }

    private void onClickBackup () {
        String DBName = myTimeSheetDB.getDBName();
        String DBFilePath = getContext().getFilesDir().getParent() + "/databases/" + DBName;
        File DBFile = new File(DBFilePath);
        if (DBFile.exists()) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
            path += "/Simple TimeSheet";
            File DBFileBackup = new File(path);
            boolean directoryOK = false;
            if (DBFileBackup.exists()) {
                path += "/backups";
                DBFileBackup = new File(path);
                if (DBFileBackup.exists()) {
                    directoryOK = true;
                } else if (DBFileBackup.mkdir()) {
                        directoryOK = true;
                }
            } else {
                if (DBFileBackup.mkdir()) {
                    path += "/backups";
                    DBFileBackup = new File(path);
                    if (DBFileBackup.mkdir()) directoryOK = true;
                }
            }

            if (directoryOK) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                path += "/" + currentDateandTime + ".db";
                DBFileBackup = new File(path);

                try {
                    FileInputStream inStream = new FileInputStream(DBFile);
                    FileOutputStream outStream = new FileOutputStream(DBFileBackup);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inStream.read(buffer)) > 0) {
                        outStream.write(buffer, 0, length);
                    }

                    // Close the streams
                    outStream.flush();
                    outStream.close();
                    inStream.close();

                    Toast.makeText(getContext(), getString(R.string.backup_created), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), getString(R.string.backup_failed), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.dir_failed), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void onClickRestore () {
        dbFiles.clear();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
        path += "/Simple TimeSheet/backups";
        File backupDirectory = new File(path);
        if (backupDirectory.exists()) {
            File[] backupFiles = backupDirectory.listFiles();
            for (File f : backupFiles) {
                if (f.getName().endsWith(".db")) {
                    dbFiles.add(f);
                }
            }
            String[] fileNames = new String[dbFiles.size()];
            for (int i = 0; i < dbFiles.size(); i++) {
                fileNames[i] = dbFiles.get(i).getName();
            }

            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.dialog_title_restore))
                    .setSingleChoiceItems(fileNames, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restoreDBfromFile (which);
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.dialog_title_error))
                    .setMessage(getString(R.string.dialog_message_no_backup))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })/*
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })*/
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    void restoreDBfromFile (int filePosition) {
        String DBName = myTimeSheetDB.getDBName();
        String DBFilePath = getContext().getFilesDir().getParent() + "/databases/" + DBName;
        File DBFile = new File(DBFilePath);
        if (DBFile.exists()) {
            File DBFileRestore = dbFiles.get(filePosition);

            try {
                FileInputStream inStream = new FileInputStream(DBFileRestore);
                FileOutputStream outStream = new FileOutputStream(DBFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }

                // Close the streams
                outStream.flush();
                outStream.close();
                inStream.close();

                MainActivity.mSelectedDate.reloadData();
                Toast.makeText(getContext(), getString(R.string.restore_success), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), getString(R.string.restore_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void fillView () {

    }
}
