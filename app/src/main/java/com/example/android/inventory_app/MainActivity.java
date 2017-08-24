package com.example.android.inventory_app;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventory_app.data.DummyDataUtility;
import com.example.android.inventory_app.data.InventoryContract.InventoryEntry;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = "MainActivity";
    private static final int INVENTORY_LOADER = 1;
    private ListView listView;
    private InventoryCursorAdapter cursorAdapter;

    public static String countryCodeValue;
    public static String currencySymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, " -> onCreate");

        setContentView(R.layout.activity_main);

        init(savedInstanceState);
        initCountry();
    }

    private void init(Bundle savedInstanceState) {

        Log.v(LOG_TAG, " -> init");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        cursorAdapter = new InventoryCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.v(LOG_TAG, " -> onItemClick -> " + id);
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    private void initCountry() {

        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        MainActivity.countryCodeValue = tm.getNetworkCountryIso();

        Locale locale = new Locale("en", MainActivity.countryCodeValue);
        MainActivity.currencySymbol = NumberFormat.getCurrencyInstance(locale).getCurrency().getSymbol();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, " -> onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, " -> onOptionsItemSelected");

        int id = item.getItemId();

        if( id == R.id.action_delete_all_entries ) {
            deleteAllEntries();
            return true;
        } else if( id >= R.id.action_insert_dummy_data_1 && id <= R.id.action_insert_dummy_data_5 ) {
            insertDummyData(id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyData(int id) {
        Log.v(LOG_TAG, " -> insertDummyData -> " + id);

        ContentValues values = DummyDataUtility.getContentValues(this, id);
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", " -> deleteAllPets -> " + rowsDeleted + " rows deleted");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, " -> onCreateLoader");

        String[] projection = new String[] {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_IMAGE,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY
        };

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, " -> onLoadFinished");
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, " -> onLoaderReset");
        cursorAdapter.swapCursor(null);
    }
}
