package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by yishuyan on 10/10/16.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    private TextView productQuantity;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final int quantity, rowId;
        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView productPrice = (TextView) view.findViewById(R.id.product_price);
        productQuantity = (TextView) view.findViewById(R.id.product_quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        rowId = cursor.getInt(idColumnIndex);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        quantity = cursor.getInt(quantityColumnIndex);


        productName.setText(name);
        productPrice.setText(Integer.toString(price));
        productQuantity.setText(Integer.toString(quantity));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = quantity;
                if (currentQuantity > 0) {
                    currentQuantity = currentQuantity - 1;
                    String curQuantity = Integer.toString(currentQuantity);
                    ContentValues values = new ContentValues();
                    /**Only pass the updated part in contentValues, then contentProvider will
                     * check which columns updated in contentValues.*/
                    values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, curQuantity);
                    Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, rowId);
                    int rowsAffected = context.getContentResolver().update(currentUri, values, null, null);
                    if (rowsAffected != 0) {
                        //update success
                        productQuantity.setText(curQuantity);
                    }
                }
            }
        });
    }
}
