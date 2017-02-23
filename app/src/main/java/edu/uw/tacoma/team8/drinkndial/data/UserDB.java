package edu.uw.tacoma.team8.drinkndial.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team8.drinkndial.R;
import edu.uw.tacoma.team8.drinkndial.model.User;

/**
 * Created by leejieun on 2/19/17.
 */

public class UserDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "dnd_user.db";
    private static final String COURSE_TABLE = "dnd_user";

    private UserDBHelper mUserDBHelper;
    private SQLiteDatabase mSQLiteDatabase;


    public UserDB(Context context) {
        mUserDBHelper = new UserDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mUserDBHelper.getWritableDatabase();
    }


    /**
     * Returns a list of users from local User table
     * @return
     */
    public List<User> getUser() {

        String[] columns = {"fname", "lname", "email", "pw", "phone"};

        Cursor c = mSQLiteDatabase.query(COURSE_TABLE, columns,
                                            null, // where
                                            null, // value of where
                                            null, // don't group
                                            null, // dont' filter
                                            null); // order
        c.moveToFirst();
        List<User> list = new ArrayList<User>();

        for (int i = 0; i < c.getCount(); i++) {
            String fname = c.getString(0);
            String lname = c.getString(1);
            String email = c.getString(2);
            String pw = c.getString(3);
            String phone = c.getString(4);

            User user = new User(fname, lname,email, pw, phone);
            list.add(user);

            c.moveToNext();
        }

        return list;
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
