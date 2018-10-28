/// Name: Christian Ropchock
/// Food Shopping List
/// This app creates a shopping list that stores different types of text input (food names) into a list.
/// There are also options to add/remove these items from the list and also add their metadata.
///
/// **Interface with Web Server**
/// - SharePoint: Used to host the server on the internet through a router port (Over 1000 recommended for public use)
/// - Sequel Server Manager Studio: Used to manage the app data in the database that is located on the server
package com.example.christian.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {

    private static ListView shoppingList;
    private static EditText addText;
    private static Button subBtn;
    private static Button remBtn;
    private static Spinner weightSpin;
    private static EditText quantityETxt;
    private static EditText weightETxt;
    private static EditText descText;
    private static LinearLayout insTextLayout;
    private static LinearLayout attribLayout;
    private static LinearLayout mainLayout;

    public static TextView dateText;
    public static ArrayList<String> SHPLIST = new ArrayList<String>();
    public static ArrayList<ListObject> LISTPROPERTIES = new ArrayList<ListObject>();

    String[] weightTypes = new String[] {"grams", "pounds", "ounces", "kilograms", "cups", "gallons", "tsp", "tbsp", "liters"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        shoppingList = (ListView)findViewById(R.id.listView);
        addText = (EditText)findViewById(R.id.editText);
        subBtn = (Button)findViewById(R.id.button);
        remBtn = (Button)findViewById(R.id.button2);
        quantityETxt = (EditText)findViewById(R.id.editText2);
        weightETxt = (EditText)findViewById(R.id.editText3);
        weightSpin = (Spinner)findViewById(R.id.spinner);
        mainLayout = (LinearLayout)findViewById(R.id.activityRoot);
        attribLayout= (LinearLayout)findViewById(R.id.linearLayout1);
        insTextLayout= (LinearLayout)findViewById(R.id.linearLayout2);
        descText = (EditText)findViewById(R.id.editText4);
        dateText = (TextView)findViewById(R.id.textView3);
        shoppingList.setBackgroundResource(R.mipmap.background_image);
        mainLayout.setFocusableInTouchMode(true);

        addText.setHint("Name of Item");
        descText.setHint(R.string.item_description);
        dateText.setHint("No saved instance of date.");

        if (getIntent().getExtras() != null) {

            SHPLIST = (ArrayList<String>) b.get("SHPLIST");

            byte[] bytes = (byte[]) b.get("LISTPROPERTIES");
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try {
                GZIPInputStream gzipIn = new GZIPInputStream(bais);
                ObjectInputStream objectIn = new ObjectInputStream(gzipIn);

                LISTPROPERTIES = (ArrayList<ListObject>) objectIn.readObject();
                objectIn.close();
            }
            catch (IOException ex) {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            catch (ClassNotFoundException ex) {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            String date = (String) b.get("DATETEXT");
            Toast.makeText(MainActivity.this, date, Toast.LENGTH_LONG).show();
            dateText.setText(date);

        }

        remBtn.setEnabled(false);
        quantityETxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        weightETxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setAdapter();
        populateSpinner();
        addBtnClickEvent();
        listViewClickEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    public void addBtnClickEvent() {

        setAdapter();

        subBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // If the button is clicked, then add whatever text is in editText into the
                        // list. If it's empty, then break this method.

                        int quantity = 0;
                        double weight = 0;
                        String description = null;

                        if (addText.getText().toString().equals(""))
                            return;
                        else
                        {
                            if (shoppingList.getAdapter().getCount() == 0) {
                                SHPLIST.add(addText.getText().toString());
                                setAdapter();
                            }
                            else {
                                for (int i = SHPLIST.size() - 1; i >= 0; i--) {
                                    if (SHPLIST.get(i).equals(addText.getText().toString())) {
                                        Toast.makeText(MainActivity.this, "The item is already in the list.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }

                                SHPLIST.add(addText.getText().toString());
                                setAdapter();
                            }

                            if (!(quantityETxt.getText().toString().equals("")))
                                quantity = Integer.parseInt(quantityETxt.getText().toString());

                            if (!(weightETxt.getText().toString().equals("")))
                                weight = Double.parseDouble(weightETxt.getText().toString());

                            if (descText.getText().toString().equals(""))
                                description = "N/A";
                            else
                                description = descText.getText().toString();

                            LISTPROPERTIES.add(new ListObject(quantity, weight, String.valueOf(weightSpin.getSelectedItem()), description));

                            addText.getText().clear();
                            quantityETxt.getText().clear();
                            weightETxt.getText().clear();
                            descText.getText().clear();

                            mainLayout.requestFocus();
                            closeKeyboard();
                        }

                    }
                }
        );

    }

    public void listViewClickEvent() {

        shoppingList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Toast.makeText(MainActivity.this, "Quantity: " + LISTPROPERTIES.get(position).quantity + "\n" + "Weight: " + LISTPROPERTIES.get(position).completeWeight +
                                                          "\n" + "Description: " + LISTPROPERTIES.get(position).description, Toast.LENGTH_LONG).show();

                        view.setSelected(true);
                        remBtn.setEnabled(true);

                        remBtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (shoppingList.getAdapter().getCount() > 0 && position < shoppingList.getAdapter().getCount()) {
                                            SHPLIST.remove(position);
                                            LISTPROPERTIES.remove(position);
                                            shoppingList.invalidateViews();
                                            setAdapter();
                                            if (SHPLIST.isEmpty())
                                            {
                                                remBtn.setEnabled(false);
                                            }
                                        }

                                    }
                                }
                        );
                    }
                }
        );

    }

    public void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.shopping_list, SHPLIST);
        shoppingList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void populateSpinner() {

        Spinner weightSpin = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weightTypes);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpin.setAdapter(weightAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_option:
                // User chose the "Save" item, save or overwrite the current instance
                saveMethod();
                return true;

            case R.id.load_option:
                // User chose the "Load" item, show the Load page...
                File shopFile = new File(getExternalFilesDir(null).getAbsolutePath() + "/ShoppingList/SavedShoppingLists");
                if (shopFile.exists())
                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                else
                    Toast.makeText(MainActivity.this, "Saved instance of list doesn't exist. Save a list, then load.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void saveMethod() {
    // This method will create the proper formatting to save to the ShoppingList file
    // located on the internal files of the Android Device.

        String currDate = Calendar.getInstance().getTime().toString();

        dateText.setText(currDate);

        try {
            String FILENAME = "SavedShoppingLists";
            File root = new File(getExternalFilesDir(null).getAbsolutePath() + "/ShoppingList");

            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxFile = new File(root, FILENAME);

            FileWriter writer = new FileWriter(gpxFile, true);
            writer.append("#" + currDate + "\n");
            int itemNum = 0; //Used to track the item number to get appropriate attribute
            if (SHPLIST.isEmpty()) {
                throw new Exception("The List was empty. Can only save instance of populated list.");
            }
            for (String item : SHPLIST) {
                writer.append("Item: " + item + " " + LISTPROPERTIES.get(itemNum).quantity + " " + LISTPROPERTIES.get(itemNum).completeWeight + " <Start>" +LISTPROPERTIES.get(itemNum).description + "<End>\n");
                itemNum++;
                writer.flush();
            }
            writer.append("\n");
            writer.close();
            Toast.makeText(this, "Shopping List has been saved to Internal Storage Location: ./ShoppingList/SavedShoppingLists", Toast.LENGTH_LONG).show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)this.getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

}


