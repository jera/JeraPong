package br.com.jera.jpong;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class OpenHelper extends SQLiteOpenHelper {

	public OpenHelper(Context context) {
		super(context, DataHelper.DATABASE_NAME, null,1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e(DataHelper.LOG_TAG, "Creating database");
		db.execSQL("CREATE TABLE ranking ( "
				+  "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+  "rank_player text NOT NULL ,"
				+  "rank_score DECIMAL( 10, 2 ) NOT NULL"
				+  ");");
		db.setVersion(1);
		populateDatabase(db);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	private void populateDatabase(SQLiteDatabase db) {
		db.beginTransaction();
		Log.e(DataHelper.LOG_TAG, "Creating table wait");
		
		Log.e(DataHelper.LOG_TAG, "Creating table ok");
		Log.e(DataHelper.LOG_TAG, "inserting data into table wait");
		db.execSQL("INSERT INTO ranking(rank_player,rank_score) VALUES ('MacGyver',35.50)");
		db.execSQL("INSERT INTO ranking(rank_player,rank_score) VALUES ('Chuck Norris',27.30)");
		db.execSQL("INSERT INTO ranking(rank_player,rank_score) VALUES ('Yoda',25.50)");
		db.execSQL("INSERT INTO ranking(rank_player,rank_score) VALUES ('Awei',22.00)");
		db.execSQL("INSERT INTO ranking(rank_player,rank_score) VALUES ('Bruce Lee',21.50)");
		
		Log.e(DataHelper.LOG_TAG, "inserting data into table OK");
 
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}