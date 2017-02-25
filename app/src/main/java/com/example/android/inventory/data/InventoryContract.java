package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yishuyan on 10/4/16.
 */

public final class InventoryContract {

    private InventoryContract() {
        throw new AssertionError("No InventoryContract instances for you!");
    }

    public static final class InventoryEntry implements BaseColumns {

        public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

        public static final String PATH_INVENTORY = "inventories";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /** MIME type for a content: URI containing a Cursor of a single item.*/
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String TABLE_NAME = "inventories";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_INVENTORY_NAME = "name";

        public static final String COLUMN_INVENTORY_QUANTITY = "quantity";

        public static final String COLUMN_INVENTORY_PRICE = "price";

        public static final String COLUMN_INVENTORY_IMAGE = "images";

    }
}
