package com.example.android.inventory_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory_app.data.InventoryContract.InventoryEntry;
import com.example.android.inventory_app.data.InventoryDbHelper;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView textViewName;
        private TextView textViewPrice;
        private TextView textViewQuantity;
        private Button buttonSale;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageView = view.findViewById(R.id.imageView);
        viewHolder.textViewName = view.findViewById(R.id.textViewName);
        viewHolder.textViewPrice = view.findViewById(R.id.textViewPrice);
        viewHolder.textViewQuantity = view.findViewById(R.id.textViewQuantity);
        viewHolder.buttonSale = view.findViewById(R.id.buttonSale);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        int COLUMN_ID = cursor.getColumnIndex(InventoryEntry._ID);
        int COLUMN_IMAGE = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);
        int COLUMN_NAME = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int COLUMN_PRICE = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int COLUMN_QUANTITY = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        byte[] byteArrayImage = cursor.getBlob(COLUMN_IMAGE);
        if( byteArrayImage == null )
            viewHolder.imageView.setImageResource(R.drawable.image_no_image);
        else
            viewHolder.imageView.setImageBitmap(InventoryDbHelper.getImage(byteArrayImage));

        viewHolder.textViewName.setText(cursor.getString(COLUMN_NAME));

        double price = cursor.getDouble(COLUMN_PRICE);
        if( price == -1.0 )
            viewHolder.textViewPrice.setText("NA");
        else
            viewHolder.textViewPrice.setText("₹ " + price);

        int quantity = cursor.getInt(COLUMN_QUANTITY);
        if( quantity == -1 )
            viewHolder.textViewQuantity.setText("NA");
        else
            viewHolder.textViewQuantity.setText(String.valueOf(quantity));

        final long id = cursor.getLong(COLUMN_ID);
        viewHolder.buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("InventoryCursorAdapter", " -> onClick -> buttonSale -> " + id);

                String quantityString = viewHolder.textViewQuantity.getText().toString();

                if( quantityString.equals("NA") )
                    return;

                int quantity = Integer.parseInt(quantityString);

                if( quantity > 0 ) {
                    int newQuantity = --quantity;

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_QUANTITY, newQuantity);
                    int rowsAffected = context.getContentResolver().update(ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id),
                            values, null, null);

                    if( rowsAffected <= 0 )
                        Toast.makeText(context, context.getString(R.string.update_quantity_failed), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, context.getString(R.string.update_quantity_successful), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
