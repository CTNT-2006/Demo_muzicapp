package com.example.demo_muzicapp
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "MusicDB", null, 3) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE Song(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                artist TEXT,
                image TEXT,
                lyric TEXT,
                file TEXT
            )
        """
        )

        db.execSQL(
            """
            CREATE TABLE History(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                songId INTEGER,
                FOREIGN KEY(songId) REFERENCES Song(id)
            )
        """
        )
        db.execSQL("""
    CREATE TABLE User(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        username TEXT UNIQUE,
        password TEXT,
        role TEXT
    )
""")
        db.execSQL("""
    INSERT INTO User(username, password, role)
    VALUES ('admin', '123', 'admin')
""")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS History")
        db.execSQL("DROP TABLE IF EXISTS Song")
        db.execSQL("DROP TABLE IF EXISTS User")
        onCreate(db)
    }

    fun insertSong(song: Song) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("title", song.title)
        values.put("artist", song.artist)
        values.put("image", song.image)
        values.put("lyric", song.lyric)
        values.put("file", song.file)

        db.insert("Song", null, values)
    }

    fun insertHistory(songId: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("songId", songId)

        db.insert("History", null, values)
    }

    fun getHistorySongs(): ArrayList<Song> {
        val list = ArrayList<Song>()
        val db = readableDatabase


        val cursor = db.rawQuery(
            """
            SELECT Song.* FROM History
            INNER JOIN Song ON History.songId = Song.id
            ORDER BY History.id DESC
        """, null
        )


        while (cursor.moveToNext()) {
            val song = Song(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
            )
            list.add(song)
        }

        cursor.close()
        return list

    }
    fun getAllSongs(): ArrayList<Song> {
        val list = ArrayList<Song>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Song", null)
        while (cursor.moveToNext()) {
            val song = Song(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
            )
            list.add(song)
        }
        cursor.close()
        return list
    }
    fun register(username: String, password: String): Boolean {
        val db = writableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM User WHERE username=?",
            arrayOf(username)
        )

        if (cursor.moveToFirst()) {
            cursor.close()
            return false
        }
        cursor.close()

        val values = ContentValues()
        values.put("username", username)
        values.put("password", password)
        values.put("role", "user")

        return db.insert("User", null, values) != -1L
    }
    fun login(username: String, password: String): String? {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT role FROM User WHERE username=? AND password=?",
                arrayOf(username, password)
            )

            var role: String? = null
            if (cursor.moveToFirst()) {
                role = cursor.getString(0)
            }

            cursor.close()
            role
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}