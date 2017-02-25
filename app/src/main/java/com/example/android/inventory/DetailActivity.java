package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by yishuyan on 10/9/16.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri currentProductUri;

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private TextView nameTextView, priceTextView, quantityTextView;

    private EditText increaseQuantity, decreaseQuantity;

    private ImageView imageView;

    private boolean mProductHasChange = false;

    private Button quantityAdd, quantityReduce, orderButton, deleteButton, saveButton, sellButton, receiveButton;

    private int currentQuantity;

    private String name;

    private int price;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        Intent intent = getIntent();
        currentProductUri = intent.getData();
        if (currentProductUri != null) {
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }


        imageView = (ImageView) findViewById(R.id.product_img);
        nameTextView = (TextView) findViewById(R.id.display_name);
        priceTextView = (TextView) findViewById(R.id.display_price);
        increaseQuantity = (EditText) findViewById(R.id.input_increase);
        decreaseQuantity = (EditText) findViewById(R.id.input_decrease);
        quantityTextView = (TextView) findViewById(R.id.display_quantity);
        quantityAdd = (Button) findViewById(R.id.quantity_add);
        quantityReduce = (Button) findViewById(R.id.quantity_reduce);
        sellButton = (Button) findViewById(R.id.sell_button);
        receiveButton = (Button) findViewById(R.id.receive_button);
        deleteButton = (Button) findViewById(R.id.delete_button);
        orderButton = (Button) findViewById(R.id.order_button);
        saveButton = (Button) findViewById(R.id.save_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuantity > 1) {
                    currentQuantity--;
                    quantityTextView.setText(Integer.toString(currentQuantity));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.quantity_invalid), Toast.LENGTH_SHORT).show();
                }
            }
        });
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuantity++;
                quantityTextView.setText(Integer.toString(currentQuantity));
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Recipient"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject: I need to order this product more");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi," + "/n This " + name + " price $" + price + " almost sold out, I need to order more!");
                startActivity(emailIntent);
            }
        });
        quantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuantity++;
                quantityTextView.setText(Integer.toString(currentQuantity));
            }
        });
        quantityReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuantity > 1) {
                    currentQuantity--;
                    quantityTextView.setText(Integer.toString(currentQuantity));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.quantity_invalid), Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryEntry.COLUMN_INVENTORY_IMAGE
        };
        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_IMAGE);

            name = cursor.getString(nameColumnIndex);
            price = cursor.getInt(priceColumnIndex);
            currentQuantity = cursor.getInt(quantityColumnIndex);
            String imageResource = cursor.getString(imageColumnIndex);
            Uri imageUri = Uri.parse(imageResource);

            imageView.setImageBitmap(getBitmapFromUri(imageUri));
            nameTextView.setText(name);
            quantityTextView.setText(Integer.toString(currentQuantity));
            priceTextView.setText(Integer.toString(price));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /**put an empty image in here.*/
//        imageView.setImageResource();
        nameTextView.setText("");
        quantityTextView.setText("");
        priceTextView.setText("");
    }

    private void delete() {
        // Only perform the delete if this is an existing pet.
        if (currentProductUri != null) {
            int rowDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowDeleted == 0) {
                Toast.makeText(this, "Error with deleting pet", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                Intent backIntent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(backIntent);
            }
        }
        finish();
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            //Open a raw file descriptor to access data under a URI.
            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image", e);
            return null;
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
                }
            }
        }
    }

    private void save() {
        String increase = increaseQuantity.getText().toString().trim();
        String decrease = decreaseQuantity.getText().toString().trim();
        if (!TextUtils.isEmpty(increase)) {
            currentQuantity = currentQuantity + Integer.parseInt(increase);
        }
        if (!TextUtils.isEmpty(decrease)) {
            if (currentQuantity - Integer.parseInt(decrease) >= 0) {
                currentQuantity = currentQuantity - Integer.parseInt(decrease);
            } else {
                Toast.makeText(this, getString(R.string.quantity_invalid), Toast.LENGTH_SHORT).show();
            }
        }
        String curQuan = Integer.toString(currentQuantity);
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, curQuan);
        int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_product, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                delete();
            }
        });
        builder.setNegativeButton(R.string.cancel_selection, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
