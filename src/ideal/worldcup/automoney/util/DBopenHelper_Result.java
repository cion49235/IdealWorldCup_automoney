package ideal.worldcup.automoney.util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBopenHelper_Result extends SQLiteOpenHelper {
	public DBopenHelper_Result(Context context) {
		super(context, "result.db", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
			String createtable = "create table result_list ( _id integer primary key autoincrement,"+
			"ct_seq integer);";
			db.execSQL(createtable);

	}		                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exits result_list"); 
		onCreate(db);
	}
}