package com.example.demodatabasecrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "simplenotes.db";
    //To make the app calls onUpgrade(), increment the variable DATABASE_VERSION from 1 to 2
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NOTE = "note";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NOTE_CONTENT = "note_content";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createNoteTableSql = "CREATE TABLE " + TABLE_NOTE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NOTE_CONTENT + " TEXT ) ";
        db.execSQL(createNoteTableSql);
        Log.i("info", "created tables");

        //Dummy records, to be inserted when the database is created
        for (int i = 0; i< 4; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTE_CONTENT, "Data number " + i);
            db.insert(TABLE_NOTE, null, values);
        }
        Log.i("info", "dummy records inserted");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //To add a column called module_name to an existing table
        db.execSQL("ALTER TABLE " + TABLE_NOTE + " ADD COLUMN module_name TEXT");

               // onCreate(db);
    }


    public long insertNote(String noteContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_CONTENT, noteContent);

        //if the id will be -1 if the insert failed. Thus, we can use it to check if a record is inserted successfully
        //long result = db.insert(TABLE_NOTE, null, values);
        //if (result == -1){
        //Log.d(“DBHelper”, “Insert failed”);
        // }

        long result = db.insert(TABLE_NOTE, null, values);
        db.close();
        Log.d("SQL Insert ",""+ result); //id returned, shouldn’t be -1
        return result;
    }


    public ArrayList<String> getAllNotes() {
        ArrayList<String> notes = new ArrayList<String>();

        String selectQuery = "SELECT " + COLUMN_ID + "," + COLUMN_NOTE_CONTENT + " FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String content = cursor.getString(1);
                notes.add("ID:" + id + ", " + content);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }


    //The Select statement can be used to filter some results instead of retrieving all records from the database.
    //Below is an example of a method used to retrieve only records that contains a given keyword.
    public ArrayList<Note> getAllNotes(String keyword) {
        ArrayList<Note> notes = new ArrayList<Note>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns= {COLUMN_ID, COLUMN_NOTE_CONTENT};
        String condition = COLUMN_NOTE_CONTENT + " Like ?";
        String[] args = { "%" +  keyword + "%"};
        Cursor cursor = db.query(TABLE_NOTE, columns, condition, args, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String noteContent = cursor.getString(1);
                Note note = new Note(id, noteContent);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }




    //updateNote
    public int updateNote(Note data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_CONTENT, data.getNoteContent());
        String condition = COLUMN_ID + "= ?";
        String[] args = {String.valueOf(data.getId())};

        int result = db.update(TABLE_NOTE, values, condition, args);

        //or
        //we can use it to check if a record is updated successfully if the affected record is 1.
        //int result = db.update(TABLE_NOTE, values, condition, args);
        //if (result < 1){
        //    Log.d(“DBHelper”, “Update failed”);
        //}

        db.close();
        return result;
    }

    //deleteNote
    public int deleteNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String condition = COLUMN_ID + "= ?";
        String[] args = {String.valueOf(id)};

        int result = db.delete(TABLE_NOTE, condition, args);
        //or
        //we can use it to check if a record is updated successfully if the affected record is 1.
        //int result = db.delete(TABLE_NOTE, condition, args);
        //if (result < 1){
        //    Log.d(“DBHelper”, “Update failed”);
        //}

        db.close();
        return result;
    }







}

