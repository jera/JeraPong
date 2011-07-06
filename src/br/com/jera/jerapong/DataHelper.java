package br.com.jera.jerapong;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataHelper {

	static final String DATABASE_NAME = "jpong.db";

	static final String LOG_TAG = "DataBase";

	private Context context;
	private SQLiteDatabase db;
	
	 public DataHelper(Context context){
		 this.context = context;
		 OpenHelper open = new OpenHelper(this.context);
		 db = open.getWritableDatabase();
	 }
	
	public void close() {
		this.db.close();
	}
	
	public Cursor select() {
		Cursor cursor = db.rawQuery("SELECT rank_id,rank_player,rank_score FROM ranking ORDER BY rank_id DESC",new String [] {});
		return cursor;
	}
	
	public void insert(String player,String score) {
		db.beginTransaction();
		db.execSQL("INSERT INTO ranking VALUES (" + player + "," + Double.parseDouble(score.replace(",", ".")) + ")");
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public void update(int id) {
		db.beginTransaction();
		db.execSQL("UPDATE ranking SET rank_id = " + id);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void delete(String button) {
		db.beginTransaction();
		db.execSQL("DELETE FROM sounds WHERE fileName = ?");
		db.setTransactionSuccessful();
		db.endTransaction();

	}

}