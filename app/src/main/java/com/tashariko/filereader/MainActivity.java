package com.tashariko.filereader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_REQUEST_CODE = 1302;
    private static final int PERMISSION_STORAGE_REQUEST_CODE = 2302;
    private static final String TAG = "#MainActivity#";

    private ArrayList<ListModel> dataList=new ArrayList<>();
    private ArrayList<String> textList=new ArrayList<>();

    private ListView listView;
    private ListAdapter adapter;
    private int mainLimit=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView= (ListView) findViewById(R.id.listView);

        Toast.makeText(this, "Select any text file to parse.", Toast.LENGTH_LONG).show();
    }

    //get the file on button click
    public void selectFile(View view) {

        if (Build.VERSION.SDK_INT >= 23.0) {
            try {
                // Google API
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_REQUEST_CODE);
                } else {
                    accessFile();
                }

            } catch (Exception e) {
                Log.i(TAG, "InvalidPermission");
                e.printStackTrace();
                Toast.makeText(this, "Grant permission to access the files.", Toast.LENGTH_SHORT).show();
            }
        } else {
            accessFile();
        }
    }


    private void accessFile(){
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("text/plain");
        try {
            startActivityForResult(Intent.createChooser(fileIntent, "Choose a file"), PICK_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK){
            if(requestCode==PICK_REQUEST_CODE){

                Uri selectedImageUri = data.getData();
                System.out.println(selectedImageUri.toString());

                String selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                Log.i("Image File Path", "" + selectedImagePath);

                if (selectedImagePath != null && !selectedImagePath.equals("")) {
                    parseFile(selectedImagePath);
                } else {
                    if(data.getData().getPath().contains("//")) {
                        String path = data.getData().getPath().split(Pattern.quote("//"))[1];
                        parseFile(path);
                    }else{
                        Toast.makeText(this, "Cant get the file", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }else{
            Toast.makeText(this, "Cant get the file", Toast.LENGTH_SHORT).show();
        }

    }

    private void parseFile(final String filePath) {
        dataList.clear();
        textList.clear();
        mainLimit=0;

        if(adapter!=null)
            adapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(filePath);

                    String line="";
                    if (file.exists()) {
                        FileInputStream is = new FileInputStream(file);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        while ((line = reader.readLine()) != null) {
                            String[] split = line.split("\\s+");
                            for (int i = 0; i < split.length; i++) {
                                textList.add(split[i]);
                            }
                        }

                        fillOriginalList();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "not exist.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void fillOriginalList() {

        Map<String, Integer> occurrences = new HashMap<String, Integer>();

        for ( String word : textList ) {
            Integer oldCount = occurrences.get(word);
            if ( oldCount == null ) {
                oldCount = 0;
            }
            occurrences.put(word, oldCount + 1);
        }

        for(String word: occurrences.keySet()){
            ListModel model=new ListModel();
            model.word=word;
            model.cnt=occurrences.get(word);

            int limit = getLimitValue(occurrences.get(word));
            model.listTitle=String.valueOf(limit)+" - "+Integer.valueOf(limit-10)+"";

            dataList.add(model);
        }

        Collections.sort(dataList);
        Collections.reverse(dataList);


        for(int i=0;i<dataList.size();i++) {

            ListModel model = dataList.get(i);
            int limit = getLimitValue(model.cnt);
            if (mainLimit == limit) {
                model.show = false;
            } else {
                model.show = true;
            }

            mainLimit = limit;

            dataList.set(i, model);
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 adapter=new ListAdapter(getApplicationContext(),R.layout.list_item_home,dataList);
                listView.setAdapter(adapter);
            }
        });

    }

    private int getLimitValue(Integer integer) {
        int val=0;
        if(integer%10!=10){
            val=integer+ (10-integer%10);
        }else {
            val=integer;
        }

        return val;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==PERMISSION_STORAGE_REQUEST_CODE){
            boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);

            if (!showRationale) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessFile();
                } else {
                    Toast.makeText(this, "Grant permission to access the files.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Go to setting to give access.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}