package com.example.christian.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;

public class Main2Activity extends AppCompatActivity {

    private static ListView loadList;
    private static Button loadBtn;
    private static Button delBtn;

    private static ArrayList<ListObject> LISTPROPERTIES = new ArrayList<ListObject>();
    private static ArrayList<String> SHPLIST = new ArrayList<String>();
    private static ArrayList<String> LOADDATES = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        loadList = (ListView)findViewById(R.id.LoadList);
        loadBtn = (Button)findViewById(R.id.button3);
        delBtn = (Button)findViewById(R.id.button5);
        setToolbar(toolbar);
        setAdapter(); //This method sets the adapter for the listView
        collectLoadedListDates(); //This method is used to collect all the dates of the saved lists in the file
        loadListFileContents(); //This method is used to load the dates into the listView
        onLoadButtonClicked(); //This method registers an event of the load button being clicked
        listViewClickEvent();
    }

    public void setToolbar(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this, MainActivity.class));
            }
        });
    }

    public void listViewClickEvent() {

        loadList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        view.setSelected(true);

                        loadBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //MainActivity.SHPLIST.clear();
                                        //MainActivity.LISTPROPERTIES.clear();

                                        SHPLIST.clear(); // Clear out this instance of SHPLIST, because it's global and it's instance is saved in MainActivity
                                        LISTPROPERTIES.clear(); // Clear out the list properties as well

                                        String FILENAME = "SavedShoppingLists";
                                        File root = new File(getExternalFilesDir(null).getAbsolutePath() + "/ShoppingList");
                                        File gpxFile = new File(root, FILENAME);

                                        Scanner inFile = null; //Create new scanner

                                        try {
                                            inFile = new Scanner(gpxFile);
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }

                                        while (inFile.hasNext()) {

                                            //Toast.makeText(Main2Activity.this, inFile.next(), Toast.LENGTH_SHORT).show();

                                            String text = inFile.next();

                                            //Toast.makeText(Main2Activity.this, text, Toast.LENGTH_SHORT).show();

                                            if (text.equals("#Mon") || text.equals("#Tue") || text.equals("#Wed") || text.equals("#Thu") || text.equals("#Fri") || text.equals("#Sat") || text.equals("#Sun")) {
                                                text = text.substring(1,4) + " " + inFile.next() + " " + inFile.next() + " " + inFile.next() + " " + inFile.next() + " " + inFile.next();
                                                //Toast.makeText(Main2Activity.this, text, Toast.LENGTH_SHORT).show();
                                                if (text.equals(loadList.getItemAtPosition(position).toString())) {
                                                    // Extract the values for the SHPLIST and LISTPROPERTIES lists until a date is hit
                                                    //Toast.makeText(Main2Activity.this, text, Toast.LENGTH_SHORT).show();
                                                    //Toast.makeText(Main2Activity.this, inFile.next(), Toast.LENGTH_SHORT).show(); // Skip the first instance of "Item:"
                                                    while (true) {
                                                        String listItem = null; // Initialize the listItem variable for the try block

                                                        try {
                                                            listItem = inFile.next(); // Decoy for the Item: label
                                                        }
                                                        catch (Exception ex) {
                                                            break;
                                                        }

                                                        //Toast.makeText(Main2Activity.this, listItem, Toast.LENGTH_SHORT).show();

                                                        if (listItem.equals("#Mon") || listItem.equals("#Tue") || listItem.equals("#Wed") || listItem.equals("#Thu") || listItem.equals("#Fri") || listItem.equals("#Sat") || listItem.equals("#Sun") || !(inFile.hasNext()))
                                                            break;

                                                            //Toast.makeText(Main2Activity.this, inFile.next(), Toast.LENGTH_SHORT).show();

                                                            String temp =  inFile.next();

                                                            //Toast.makeText(Main2Activity.this, inFile.next(), Toast.LENGTH_SHORT).show();
                                                            //Toast.makeText(Main2Activity.this, inFile.next(), Toast.LENGTH_SHORT).show();
                                                            //Toast.makeText(Main2Activity.this, inFile.next(), Toast.LENGTH_SHORT).show();
                                                            /*while(true) {
                                                                String tempText = null;
                                                                try {
                                                                    tempText = inFile.next();
                                                                }
                                                                catch (Exception ex) {
                                                                    break;
                                                                }

                                                                if (tempText.contains("<End>"))
                                                                   break;

                                                                Toast.makeText(Main2Activity.this, tempText, Toast.LENGTH_SHORT).show();
                                                            }*/
                                                            String listItemQuantity = null;
                                                            while (true) {
                                                                try {
                                                                    listItemQuantity = inFile.next();
                                                                    Integer.parseInt(listItemQuantity);
                                                                }
                                                                catch (Exception ex) {temp += " " + listItemQuantity; continue;}
                                                                break;
                                                            }
                                                            SHPLIST.add(temp);
                                                            String listItemWeight = inFile.next();
                                                            String listItemWeightType = inFile.next();
                                                            String listItemDescription = inFile.next();
                                                            listItemDescription = listItemDescription.replaceAll("<Start>", "");
                                                            boolean descriptionCapture = true;

                                                            if (!listItemDescription.contains("<End>")) {
                                                                while (descriptionCapture) {
                                                                    String nextWord = inFile.next();
                                                                    if (nextWord.contains("<End>")) {
                                                                        nextWord = nextWord.replaceAll("<End>", "");
                                                                        listItemDescription += nextWord;
                                                                        break;
                                                                    } else
                                                                        listItemDescription += nextWord;
                                                                }
                                                            }
                                                            else
                                                            {
                                                                listItemDescription = listItemDescription.replaceAll("<End>", "");
                                                            }

                                                            //Toast.makeText(Main2Activity.this, listItemDescription, Toast.LENGTH_SHORT).show();

                                                            LISTPROPERTIES.add(new ListObject(Integer.parseInt(listItemQuantity), Double.parseDouble(listItemWeight), listItemWeightType, listItemDescription));

                                                    }

                                                    Intent i = new Intent(Main2Activity.this, MainActivity.class);

                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    try {
                                                        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                                                        ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
                                                        objectOut.writeObject(LISTPROPERTIES);
                                                        objectOut.close();
                                                    }
                                                    catch (IOException ex) {

                                                    }

                                                    byte[] bytes = baos.toByteArray();

                                                    try {
                                                        i.putExtra("SHPLIST", SHPLIST);
                                                        i.putExtra("LISTPROPERTIES", bytes);
                                                        i.putExtra("DATETEXT", text);
                                                    }
                                                    catch (Exception ex) {
                                                        Toast.makeText(Main2Activity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    startActivity(i);

                                                }
                                            }


                                        }


                                    }
                                }
                        );

                        delBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {



                                    }
                                }
                        );
                    }
                }
        );

    }

    public void collectLoadedListDates() {
        if (LOADDATES.size() > 0) {
            LOADDATES.clear(); //Clear previous save states of the list
        }

        String FILENAME = "SavedShoppingLists";
        File root = new File(getExternalFilesDir(null).getAbsolutePath() + "/ShoppingList");
        File gpxFile = new File(root, FILENAME);

        Scanner inFile = null; //Create new scanner

        try {
            inFile = new Scanner(gpxFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (inFile.hasNext()) {

            String text = inFile.next();

            if (text.equals("#Mon") || text.equals("#Tue") || text.equals("#Wed") || text.equals("#Thu") || text.equals("#Fri") || text.equals("#Sat") || text.equals("#Sun")) {
                text = text.substring(1,4) + " " + inFile.next() + " " + inFile.next() + " " + inFile.next() + " " + inFile.next() + " " + inFile.next();
                LOADDATES.add(text);
            }
        }

        inFile.close();

    }

    public void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.load_list, LOADDATES);
        loadList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void loadListFileContents() {
    //This method is used to load the contents of the load file which will sort files
    //based on a timestamp and looked up by their timestamp. This method is used to put
    //the files from the locally saved file into a list.
        //String FILENAME = "SavedShoppingLists";
        //File root = new File(getExternalFilesDir(null).getAbsolutePath() + "/ShoppingList");
        //File gpxFile = new File(root, FILENAME);

        //int length = (int) gpxFile.length();
        //byte[] bytes = new byte[length];

        //try {
        //    FileInputStream in = new FileInputStream(gpxFile);
        //    in.read(bytes);
        //    in.close();
        //}
        //catch(IOException e) {
        //    e.printStackTrace();
        //}

        //String contents = new String(bytes);

    }

    public void onLoadButtonClicked() {

    }
}
