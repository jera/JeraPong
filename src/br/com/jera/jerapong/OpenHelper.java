package br.com.jera.jerapong;

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
		db.execSQL("CREATE DATABASE jpong");
		db.setVersion(1);
		populateDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

	private void populateDatabase(SQLiteDatabase db) {
		db.beginTransaction();
		Log.e(DataHelper.LOG_TAG, "Creating table wait");
		db.execSQL("CREATE TABLE ranking" +
					"(" +
						"rank_id INT UNSIGNED NOT NULL AUTO_INCREMENT," +
						"rank_player VARCHAR(40) NOT NULL," +
						"rank_score DOUBLE NOT NULL," +
						"PRIMARY KEY(rank_id)" +
					")");
		Log.e(DataHelper.LOG_TAG, "Creating table ok");
		Log.e(DataHelper.LOG_TAG, "inserting data into table wait");
		db.execSQL("INSERT INTO ranking VALUES ('Rasley',35.50)");
		Log.e(DataHelper.LOG_TAG, "inserting data into table OK");
 
		db.setTransactionSuccessful();
		db.endTransaction();
	}
}