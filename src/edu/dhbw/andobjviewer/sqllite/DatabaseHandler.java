package edu.dhbw.andobjviewer.sqllite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "MagazineManager";
	private static final String TABLE_MAGAZINE = "Magazine";
	private static final String TABLE_CONTENT = "MagazineContent";
	
	private static final String KEY_ID = "Magazine_ID";
	private static final String YEAR = "Publishing_Year";
	private static final String MONTH = "Publishing_Month";
	
	private static final String CONTENT_ID="Content_ID";
	private static final String HEADLINE = "Head_Line";
	private static final String MEDIA_NAME = "Media";
	private static final String CONTENT_DESC = "Content_Description";
	private static final String MAGAZINE_ID = "Magazine_ID";
	private static final String CONTENT_TYPE = "Content_Type";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAGAZINE);
		onCreate(db);
	}
	
	public void createTables(SQLiteDatabase db) {
		
		String sqlMagazine = "CREATE TABLE IF NOT EXISTS " + TABLE_MAGAZINE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + YEAR + " TEXT,"
                + MONTH + " TEXT" + ")";
		db.execSQL(sqlMagazine);
		String sqlContent = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTENT + "("
                + CONTENT_ID + " INTEGER PRIMARY KEY," + HEADLINE + " TEXT,"
                + MEDIA_NAME + " TEXT,"+CONTENT_DESC + " TEXT,"+CONTENT_TYPE+" TEXT,"+ MAGAZINE_ID+" INTEGER, FOREIGN KEY("+ MAGAZINE_ID +")" +
                		" REFERENCES "+TABLE_MAGAZINE+"("+KEY_ID+"))";
		db.execSQL(sqlContent);
		
	}
	 
	public List<MagazineContent> getContents(String month,String year,String type) {
		
		List<MagazineContent> contents = new ArrayList<MagazineContent>();
		SQLiteDatabase db = this.getReadableDatabase();
		Log.d("message", "Querying data....");
		Cursor cursor = db.query(TABLE_MAGAZINE, new String[] {KEY_ID},
				MONTH+"=? AND "+YEAR+"=?", new String[]{month,year}, null, null, null);
		Log.d("message", "Query completed..");
		if(cursor!=null)
		{
			cursor.moveToFirst();
			Log.d("message", "Extracting key from "+cursor);
			String keyId = String.valueOf(cursor.getInt(0));
			Log.d("message", "Data loaded...Key ID:"+keyId);
			Cursor contentCursor = db.query(TABLE_CONTENT, new String[]{HEADLINE,MEDIA_NAME,CONTENT_DESC,CONTENT_TYPE},
					MAGAZINE_ID+"=? AND "+CONTENT_TYPE+"=?", new String[]{keyId,type}, null, null, null);
			Log.d("message", "Magazine content loaded....Data extraction started...");
			if(contentCursor!=null)
			{
				if(contentCursor.moveToFirst())
				{
					do
					{
						MagazineContent content = new MagazineContent(0, contentCursor.getString(0),
								contentCursor.getString(1),contentCursor.getString(2), 0);
						contents.add(content);
						Log.d("content", content.getHeadLine());
					} while(contentCursor.moveToNext());
				}
			}
			Log.d("message", "Data extraction completed");
		}
		return contents;
	}
	
	public void addMagazine(Magazine magazine) {
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, magazine.getId());
        values.put(YEAR, magazine.getYear());
        values.put(MONTH, magazine.getMonth());
        
        db.insert(TABLE_MAGAZINE, null, values);
        db.close();
	}
	
	public void addMagazineContent(MagazineContent content, String type) {
		SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CONTENT_ID, content.getContentId());
        values.put(HEADLINE, content.getHeadLine());
        values.put(MEDIA_NAME, content.getImage());
        
        values.put(CONTENT_DESC, content.getContent());
        values.put(MAGAZINE_ID, content.getMagazineId());
        values.put(CONTENT_TYPE, type);
        
        db.insert(TABLE_CONTENT, null, values);
        db.close();
	}
	
}
