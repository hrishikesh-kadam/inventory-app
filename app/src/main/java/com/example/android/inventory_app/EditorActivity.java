package com.example.android.inventory_app;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventory_app.data.InventoryContract.InventoryEntry;
import com.example.android.inventory_app.data.InventoryDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = "EditorActivity";
    private static final int PICK_IMAGE_ID = 1;
    private static final int EXISTING_ITEM_LOADER = 2;
    private static final int FROM_BACK = 3;
    private static final int FROM_UP = 4;
    private Uri currentUri;
    private long currentId;

    private ImageView imageView;
    private EditText editTextName;
    private EditText editTextPrice;
    private EditText editTextQuantity;
    private Button buttonDecrement;
    private Button buttonIncrement;
    private EditText editTextSupplierName;
    private EditText editTextSupplierPhone;
    private EditText editTextSupplierEmail;

    private byte[] byteArrayImage;

    private boolean isImageChanged;
    private boolean isNameChanged;
    private boolean isPriceChanged;
    private boolean isQuantityChanged;
    private boolean isSupplierNameChanged;
    private boolean isSupplierPhoneChanged;
    private boolean isSupplierEmailChanged;

    private int COLUMN_IMAGE;
    private int COLUMN_NAME;
    private int COLUMN_PRICE;
    private int COLUMN_QUANTITY;
    private int COLUMN_SUPPLIER_NAME;
    private int COLUMN_SUPPLIER_PHONE;
    private int COLUMN_SUPPLIER_EMAIL;

    private String name;
    private String supplierName;
    private String supplierPhone;
    private String supplierEmail;
    private String storedSupplierPhone;
    private String storedSupplierEmail;
    private double price = 0.0;
    private int quantity = 0;

    private boolean itemHasChanged;

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            itemHasChanged = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, " -> onCreate");
        setContentView(R.layout.activity_editor);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {

        Log.v(LOG_TAG, " -> init");
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);

        buttonDecrement = (Button) findViewById(R.id.buttonDecrement);
        buttonDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemHasChanged = true;
                int quantity;
                try {
                    quantity = Integer.parseInt(editTextQuantity.getText().toString());

                    if (quantity > 0)
                        editTextQuantity.setText(String.valueOf(--quantity));
                } catch (Exception e) {
                    editTextQuantity.setText("0");
                }
            }
        });

        buttonIncrement = (Button) findViewById(R.id.buttonIncrement);
        buttonIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemHasChanged = true;
                int quantity;
                try{
                    quantity = Integer.parseInt(editTextQuantity.getText().toString());
                    editTextQuantity.setText(String.valueOf(++quantity));
                } catch (Exception e) {
                    editTextQuantity.setText("1");
                }
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        editTextSupplierName = (EditText) findViewById(R.id.editTextSupplierName);
        editTextSupplierPhone = (EditText) findViewById(R.id.editTextSupplierPhone);
        editTextSupplierEmail = (EditText) findViewById(R.id.editTextSupplierEmail);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemHasChanged = true;
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(EditorActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });

        currentUri = getIntent().getData();
        if( currentUri == null ) {
            setTitle(R.string.editor_activity_title_new_item);
            findViewById(R.id.relativeLayoutButtons).setVisibility(View.GONE);
        } else {
            setTitle(R.string.editor_activity_title_edit_item);
            currentId = ContentUris.parseId(currentUri);

            setUpDeleteButton();
            setUpOrderButton();
            setUpTouchListeners();

            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
    }

    private void setUpTouchListeners() {

        editTextName.setOnFocusChangeListener(focusChangeListener);
        editTextPrice.setOnFocusChangeListener(focusChangeListener);
        editTextQuantity.setOnFocusChangeListener(focusChangeListener);
        editTextSupplierName.setOnFocusChangeListener(focusChangeListener);
        editTextSupplierPhone.setOnFocusChangeListener(focusChangeListener);
        editTextSupplierEmail.setOnFocusChangeListener(focusChangeListener);
    }

    private void setUpDeleteButton() {

        Button button = (Button) findViewById(R.id.buttonDelete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, " -> delete -> " + currentId);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
                builder.setMessage(R.string.delete_item_dialog_msg);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int rowsAffected = getContentResolver().delete(currentUri, null, null);

                        if (rowsAffected <= 0)
                            Toast.makeText(EditorActivity.this, getString(R.string.delete_item_failed), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(EditorActivity.this, getString(R.string.delete_item_successful), Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void setUpOrderButton() {

        Button button = (Button) findViewById(R.id.buttonOrder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, " -> order -> " + currentId);

                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + editTextSupplierPhone.getText()));

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {editTextSupplierEmail.getText().toString()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_for) + " " + editTextName.getText().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi!\n\nNeed order for following product:-\n\nProduct Name: "
                    + editTextName.getText().toString() + "\nQuantity: ");

                if( storedSupplierPhone.isEmpty() && storedSupplierEmail.isEmpty() )
                    return;
                else if( !storedSupplierPhone.isEmpty() )
                    startActivity(phoneIntent);
                else if( !storedSupplierEmail.isEmpty() )
                    startActivity(emailIntent);
                else {
                    Intent chooserIntent = Intent.createChooser(phoneIntent, getString(R.string.order_by));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{emailIntent});
                    startActivity(chooserIntent);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, " -> onActivityResult");
        switch(requestCode) {
            case PICK_IMAGE_ID:
                    Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    if( bitmap == null )
                        break;
                    byteArrayImage = InventoryDbHelper.getBytes(bitmap);
                    imageView.setBackgroundResource(0);
                    imageView.setImageBitmap(bitmap);
                    isImageChanged = true;
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, " -> onSaveInstanceState");
        if( byteArrayImage != null )
            outState.putByteArray("image", byteArrayImage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(LOG_TAG, " -> onRestoreInstanceState");
        if( savedInstanceState.containsKey("image") ) {
            byteArrayImage = savedInstanceState.getByteArray("image");
            Bitmap bitmap = InventoryDbHelper.getImage(byteArrayImage);
            imageView.setBackgroundResource(0);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, " -> onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, " -> onOptionsItemSelected");

        switch (item.getItemId()) {
            case R.id.action_save:
                if( currentUri == null )
                    saveNewItem();
                else
                    updateExistingItem();
                return true;
            case android.R.id.home:
                upOrBackPressed(FROM_UP);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNewItem() {
        Log.v(LOG_TAG, " -> saveNewItem");

        sanityCheck();

        if( isNameChanged ) {

            ContentValues values = new ContentValues();
            if(isImageChanged)
                values.put(InventoryEntry.COLUMN_IMAGE, byteArrayImage);
            values.put(InventoryEntry.COLUMN_NAME, name);
            values.put(InventoryEntry.COLUMN_PRICE, price);
            values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
            values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        } else {

            if( isImageChanged || isPriceChanged || isQuantityChanged ||
                    isSupplierNameChanged || isSupplierPhoneChanged || isSupplierEmailChanged )
                Toast.makeText(this, getString(R.string.name_compulsory), Toast.LENGTH_SHORT).show();
            else
                finish();
        }
    }

    private void sanityCheck() {
        Log.v(LOG_TAG, " -> sanityCheck");

        if( byteArrayImage == null )
            isImageChanged = false;
        else
            isImageChanged = true;

        name = editTextName.getText().toString().trim();
        isNameChanged = !name.isEmpty();

        String priceString = editTextPrice.getText().toString();
        if( priceString.isEmpty() ) {
            price = -1;
            isPriceChanged = false;
        }
        else {
            price = Double.parseDouble(priceString);
            isPriceChanged = true;
        }

        String quantityString = editTextQuantity.getText().toString();
        if( quantityString.isEmpty() ) {
            quantity = -1;
            isQuantityChanged = false;
        }
        else {
            quantity = Integer.parseInt(quantityString);
            isQuantityChanged = true;
        }

        supplierName = editTextSupplierName.getText().toString().trim();
        isSupplierNameChanged = !supplierName.isEmpty();

        supplierPhone = editTextSupplierPhone.getText().toString().trim();
        isSupplierPhoneChanged = !supplierPhone.isEmpty();

        supplierEmail = editTextSupplierEmail.getText().toString().trim();
        isSupplierEmailChanged = !supplierEmail.isEmpty();
    }

    @Override
    public void onBackPressed() {
        Log.v(LOG_TAG, " -> onBackPressed");

        upOrBackPressed(FROM_BACK);
    }

    private void upOrBackPressed(final int FROM_KEY) {
        Log.v(LOG_TAG, " -> upOrBackPressed");

        if( !itemHasChanged && currentUri!=null ) {
            finishOrNavigateUp(FROM_KEY);
            return;
        }

        sanityCheck();

        if( isNameChanged || isImageChanged || isPriceChanged || isQuantityChanged ||
                isSupplierNameChanged || isSupplierPhoneChanged || isSupplierEmailChanged) {

            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.v(LOG_TAG, " -> discard");

                            finishOrNavigateUp(FROM_KEY);
                        }
                    };

            showUnsavedChangesDialog(discardButtonClickListener);
        }
        else
            finishOrNavigateUp(FROM_KEY);
    }

    private void finishOrNavigateUp( final int FROM_KEY ) {

        if( FROM_KEY == FROM_BACK )
            finish();
        else if( FROM_KEY == FROM_UP )
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateExistingItem() {
        Log.v(LOG_TAG, " -> updateExistingItem");

        sanityCheck();

        if( isNameChanged ) {

            ContentValues values = new ContentValues();
            if(isImageChanged)
                values.put(InventoryEntry.COLUMN_IMAGE, byteArrayImage);
            values.put(InventoryEntry.COLUMN_NAME, name);
            values.put(InventoryEntry.COLUMN_PRICE, price);
            values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
            values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierName);
            values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
            values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);

            int rowsAffected = getContentResolver().update(currentUri, values, null, null);

            if (rowsAffected <= 0)
                Toast.makeText(this, getString(R.string.update_item_failed), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.update_item_updated), Toast.LENGTH_SHORT).show();

            finish();
        }
        else
            Toast.makeText(this, getString(R.string.name_compulsory), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, " -> onCreateLoader");

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_IMAGE,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL
        };

        return new CursorLoader(this,
                currentUri,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, " -> onLoadFinished");

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if( cursor.moveToFirst() ) {

            COLUMN_IMAGE = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);
            COLUMN_NAME = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
            COLUMN_PRICE = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            COLUMN_QUANTITY = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            COLUMN_SUPPLIER_NAME = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            COLUMN_SUPPLIER_PHONE = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            COLUMN_SUPPLIER_EMAIL = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);

            byteArrayImage = cursor.getBlob(COLUMN_IMAGE);

            if( byteArrayImage != null ) {
                imageView.setBackgroundResource(0);
                imageView.setImageBitmap(InventoryDbHelper.getImage(byteArrayImage));
            }

            editTextName.setText(cursor.getString(COLUMN_NAME));

            price = cursor.getDouble(COLUMN_PRICE);
            if( price == -1.0 )
                editTextPrice.setText("");
            else
                editTextPrice.setText(String.valueOf(price));

            quantity = cursor.getInt(COLUMN_QUANTITY);
            if( quantity == -1 )
                editTextQuantity.setText("");
            else
                editTextQuantity.setText(String.valueOf(quantity));

            editTextSupplierName.setText(cursor.getString(COLUMN_SUPPLIER_NAME));
            editTextSupplierPhone.setText(cursor.getString(COLUMN_SUPPLIER_PHONE));
            storedSupplierPhone = cursor.getString(COLUMN_SUPPLIER_PHONE);
            editTextSupplierEmail.setText(cursor.getString(COLUMN_SUPPLIER_EMAIL));
            storedSupplierEmail = cursor.getString(COLUMN_SUPPLIER_EMAIL);

            // To make multiline EditText with Next button
            editTextName.setHorizontallyScrolling(false);
            editTextName.setMaxLines(Integer.MAX_VALUE);

            editTextSupplierName.setHorizontallyScrolling(false);
            editTextSupplierName.setMaxLines(Integer.MAX_VALUE);

            editTextSupplierPhone.setHorizontallyScrolling(false);
            editTextSupplierPhone.setMaxLines(Integer.MAX_VALUE);

            editTextSupplierEmail.setHorizontallyScrolling(false);
            editTextSupplierEmail.setMaxLines(Integer.MAX_VALUE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, " -> onLoaderReset");
    }
}
