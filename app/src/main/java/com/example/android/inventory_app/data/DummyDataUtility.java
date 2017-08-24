package com.example.android.inventory_app.data;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.android.inventory_app.R;
import com.example.android.inventory_app.data.InventoryContract.InventoryEntry;

public class DummyDataUtility {

    public static ContentValues getContentValues(Context context, int id) {

        ContentValues values = new ContentValues();
        ImageView imageView;
        Bitmap bitmap;

        switch(id) {
            case R.id.action_insert_dummy_data_1:
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.image_gatorade);
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                values.put(InventoryEntry.COLUMN_IMAGE, InventoryDbHelper.getBytes(bitmap));
                values.put(InventoryEntry.COLUMN_NAME, "Gatorade Sports Drink (Blue Bolt)");
                values.put(InventoryEntry.COLUMN_PRICE, 45.0);
                values.put(InventoryEntry.COLUMN_QUANTITY, 100);
                values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Pooja Wholesaler");
                values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "+919595956444");
                values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "hrkadam.92@gmail.com");
                break;
            case R.id.action_insert_dummy_data_2:
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.image_fuse);
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                values.put(InventoryEntry.COLUMN_IMAGE, InventoryDbHelper.getBytes(bitmap));
                values.put(InventoryEntry.COLUMN_NAME, "Cadbury Fuse, 45g");
                values.put(InventoryEntry.COLUMN_PRICE, 35.0);
                values.put(InventoryEntry.COLUMN_QUANTITY, 50);
                values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Pooja Wholesaler");
                values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "+919595956444");
                values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "hrkadam.92@gmail.com");
                break;
            case R.id.action_insert_dummy_data_3:
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.image_on_whey);
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                values.put(InventoryEntry.COLUMN_IMAGE, InventoryDbHelper.getBytes(bitmap));
                values.put(InventoryEntry.COLUMN_NAME, "Optimum Nutrition (ON) 100% Whey Gold Standard - 10 lbs (Double Rich Chocolate)");
                values.put(InventoryEntry.COLUMN_PRICE, 14500.0);
                values.put(InventoryEntry.COLUMN_QUANTITY, 10);
                values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Pooja Wholesaler");
                values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "+919595956444");
                values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "hrkadam.92@gmail.com");
                break;
            case R.id.action_insert_dummy_data_4:
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.image_gatorade_sachet);
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                values.put(InventoryEntry.COLUMN_IMAGE, InventoryDbHelper.getBytes(bitmap));
                values.put(InventoryEntry.COLUMN_NAME, "Gatorade Sports Powder Mix - Lemon Flavor,100g (4 Sachets x 25g)");
                values.put(InventoryEntry.COLUMN_PRICE, 80.0);
                values.put(InventoryEntry.COLUMN_QUANTITY, 20);
                values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Pooja Wholesaler");
                values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "+919595956444");
                values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "hrkadam.92@gmail.com");
                break;
            case R.id.action_insert_dummy_data_5:
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.image_on_caesin);
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                values.put(InventoryEntry.COLUMN_IMAGE, InventoryDbHelper.getBytes(bitmap));
                values.put(InventoryEntry.COLUMN_NAME, "Optimum Nutrition (ON) 100% Casein Protein - 4 lbs (Cookies and Cream)");
                values.put(InventoryEntry.COLUMN_PRICE, 6500.0);
                values.put(InventoryEntry.COLUMN_QUANTITY, 10);
                values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Pooja Wholesaler");
                values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, "+919595956444");
                values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "hrkadam.92@gmail.com");
                break;
        }

        return values;
    }

}
