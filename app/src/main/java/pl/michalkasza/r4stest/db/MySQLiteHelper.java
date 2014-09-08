package pl.michalkasza.r4stest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import pl.michalkasza.r4stest.model.Link;
/**
 * Miałem problemy przy implementacji biblioteki ORMLite, dlatego wykorzystałem tą wbudowaną.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LINK_TABLE = "CREATE TABLE links ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "original_URL TEXT, "+
                "shrinked_URL TEXT )";

        db.execSQL(CREATE_LINK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS links");

        this.onCreate(db);
    }

    private static final String TABLE_LINKS = "links";
    private static final String KEY_ID = "id";
    private static final String KEY_ORIGINALURL = "original_URL";
    private static final String KEY_SHRINKEDURL = "shrinked_URL";
    private static final String[] COLUMNS = {KEY_ID,KEY_ORIGINALURL,KEY_SHRINKEDURL};

    public void addLink(Link link){
        Log.d("addLink", link.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ORIGINALURL, link.getOriginalURL());
        values.put(KEY_SHRINKEDURL, link.getShrinkedURL());

        db.insert(TABLE_LINKS, null, values);
        db.close();
    }

/*
    public Link getLink(int id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LINKS, COLUMNS, " id = ?",
                                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Link link = new Link();
        link.setOriginalURL(cursor.getString(1));
        link.setShrinkedURL(cursor.getString(2));

        Log.d("getLink("+id+")", link.toString());

        return link;
    }
*/
    public ArrayList getAllLinks() {
        ArrayList links = new ArrayList();
        String query = "SELECT  * FROM " + TABLE_LINKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Link link = null;
        if (cursor.moveToFirst()) {
            do {
                link = new Link();
                link.setOriginalURL(cursor.getString(1));
                link.setShrinkedURL(cursor.getString(2));

                links.add(link.getOriginalURL());
                links.add(link.getShrinkedURL());
            } while (cursor.moveToNext());
        }
        return links;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS links");
        this.onCreate(db);
    }
}