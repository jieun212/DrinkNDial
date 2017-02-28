package edu.uw.tacoma.team8.drinkndial.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.User;

/**
 * Created by leejieun on 2/19/17.
 */

public class UserDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "dnd_user.db";
    private static final String USER_TABLE = "dnd_user";

    private UserDBHelper mUserDBHelper;
    private SQLiteDatabase mSQLiteDatabase;


    public UserDB(Context context) {
        mUserDBHelper = new UserDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mUserDBHelper.getWritableDatabase();
    }



    public User getUser() {

        String[] columns = {"email", "fname", "lname", "pw", "phone"};

        Cursor c = mSQLiteDatabase.query(USER_TABLE, columns,
                                            null, // where
                                            null, // value of where
                                            null, // don't group
                                            null, // dont' filter
                                            null); // order
        c.moveToFirst();

        String email = c.getString(0);
        String fname = c.getString(1);
        String lname = c.getString(2);
        String pw = c.getString(3);
        String phone = c.getString(4);

        return new User(email, fname, lname,pw, phone);
    }



    public boolean insertUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", user.getEmail());
        contentValues.put("fname", user.getFname());
        contentValues.put("lname", user.getLname());
        contentValues.put("pw", user.getPw());
        contentValues.put("phone", user.getPhone());

        long rowId = mSQLiteDatabase.insert("dnd_user", null, contentValues);
        return rowId != -1;
    }


    public void deleteUser() {
        mSQLiteDatabase.delete(USER_TABLE, null, null);
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * Inner class
     */
    class UserDBHelper extends SQLiteOpenHelper {

        private final String CREATE_USER_SQL;

        private final String DROP_USER_SQL;

        public UserDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_USER_SQL = context.getString(R.string.CREATE_DND_USER_SQL);
            DROP_USER_SQL = context.getString(R.string.DROP_DND_USER_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_USER_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_USER_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
