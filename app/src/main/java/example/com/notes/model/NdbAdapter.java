package example.com.notes.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class NdbAdapter {

    private static final String DATABASE_NAME = "NOTES_DB";
    private static final int DATABASE_VERSION = 1;

    private DbHelper dbHelper;
    private SQLiteDatabase mDb;
    private Context context;

    private static final String TABLE_NOTES = "notes";
    private static final String NOTE_ID = "_id";
    private static final String NOTE_TITLE = "title";
    private static final String NOTE_CONTENT = "content";
    private static final String TABLE_NOTES_CREATE = "create table " + TABLE_NOTES + "(" + NOTE_ID +
            " integer primary key autoincrement, " + NOTE_TITLE + " text, " + NOTE_CONTENT + " text);";

    public long addUpdateNote(NotesInfo notesInfo) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(NOTE_TITLE, notesInfo.getTitle());
        initialValues.put(NOTE_CONTENT, notesInfo.getContent());

        if (notesInfo.getId() == 0) {
            return mDb.insert(TABLE_NOTES, null, initialValues);
        } else {
            return mDb.update(TABLE_NOTES, initialValues, NOTE_ID + "='" +
                    notesInfo.getId() + "'", null);
        }
    }

    public ArrayList<NotesInfo> getAllNotes() {
        ArrayList<NotesInfo> notesList = new ArrayList<>();
        Cursor notesCursor = mDb.rawQuery("select * from " + TABLE_NOTES, null);
        if (notesCursor.moveToFirst()) {
            do {
                NotesInfo notesInfo = new NotesInfo();
                notesInfo.setId(notesCursor.getInt(notesCursor.getColumnIndex(NOTE_ID)));
                notesInfo.setTitle(notesCursor.getString(notesCursor.getColumnIndex(NOTE_TITLE)));
                notesInfo.setContent(notesCursor.getString(notesCursor.getColumnIndex(NOTE_CONTENT)));
                notesList.add(notesInfo);
            } while (notesCursor.moveToNext());
        }
        return notesList;
    }

    public boolean deleteNoteById(int noteId) {
        return mDb.delete(TABLE_NOTES, NOTE_ID + "=" + noteId, null) > 0;
    }

    private static class DbHelper extends SQLiteOpenHelper {

        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_NOTES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be opened/created
     *
     * @param context the Context with in which to work
     */
    public NdbAdapter(Context context) {
        this.context = context;
    }


    /**
     * Open the NdbAdapter database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialisation call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NdbAdapter open() throws SQLException {
        dbHelper = new DbHelper(context);
        mDb = dbHelper.getWritableDatabase();
        return this;
    }


    public void close() {
        if (isOpen()) {
            dbHelper.close();
            mDb.close();
        }
    }

    public boolean isOpen() {
        boolean open = false;
        if (mDb != null && mDb.isOpen()) {
            open = true;
        }
        return open;
    }

}
