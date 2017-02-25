package com.example.android.inventory;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.example.android.inventory.data.InventoryDbHelper;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by yishuyan on 10/9/16.
 */

public class AddActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private EditText mNameEditText, mPriceEditText, mQuantityEditText;
    private Button mImageButton, mAddButton;
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 0;
    private Uri mUri;
    private static final String LOG_TAG = AddActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);


        mNameEditText = (EditText) findViewById(R.id.add_name);
        mPriceEditText = (EditText) findViewById(R.id.add_price);
        mQuantityEditText = (EditText) findViewById(R.id.add_quantity);
        imageView = (ImageView) findViewById(R.id.selected_image);
        mImageButton = (Button) findViewById(R.id.upload_image);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        mAddButton = (Button) findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUri != null) {
                    insertProduct();
                } else {
                    Snackbar.make(mImageButton, "Image not selected", Snackbar.LENGTH_LONG).setAction("Select", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectImage();
                        }
                    }).show();
                }
            }
        });
    }

    private void insertProduct() {
        String name = mNameEditText.getText().toString().trim();
        String mPrice = mPriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mPrice)) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_info), Toast.LENGTH_SHORT).show();
        } else {
            int price = Integer.parseInt(mPrice);
            String mQuantity = mQuantityEditText.getText().toString().trim();
            int quantity = 0;
            if (!TextUtils.isEmpty(mQuantity)) {
                quantity = Integer.parseInt(mQuantity);
            }
            String imageResource = mUri.toString();


            InventoryDbHelper inventoryDbHelper = new InventoryDbHelper(this);
            SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_INVENTORY_NAME, name);
            values.put(InventoryEntry.COLUMN_INVENTORY_PRICE, price);
            values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, quantity);
            values.put(InventoryEntry.COLUMN_INVENTORY_IMAGE, imageResource);

            Uri newRowId = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newRowId == null) {
                Toast.makeText(this, "Error with saving products", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product saved success", Toast.LENGTH_SHORT).show();
                Intent backIntent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(backIntent);
            }
        }
    }

    private void selectImage () {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            if (resultData != null) {
                mUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mUri.toString());
                imageView.setImageBitmap(getBitmapFromUri(mUri));
            }
        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_INVENTORY_NAME,
                InventoryEntry.COLUMN_INVENTORY_PRICE,
                InventoryEntry.COLUMN_INVENTORY_QUANTITY,
                InventoryEntry.COLUMN_INVENTORY_IMAGE
        };
        return new CursorLoader(this, null, projection, null, null, null);
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

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String imageResource = cursor.getString(imageColumnIndex);
            Uri imageUri = Uri.parse(imageResource);

            imageView.setImageBitmap(getBitmapFromUri(imageUri));
            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(currentQuantity));
            mPriceEditText.setText(Integer.toString(price));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
    }
}
