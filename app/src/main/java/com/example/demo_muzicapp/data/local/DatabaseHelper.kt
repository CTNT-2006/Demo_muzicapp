package com.example.demo_muzicapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.demo_muzicapp.data.model.Song

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "MusicDB", null, 4) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE Song(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                artist TEXT,
                image TEXT,
                lyric TEXT,
                file TEXT,
                isFavorite INTEGER DEFAULT 0
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
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS History")
            db.execSQL("DROP TABLE IF EXISTS Song")
            db.execSQL("DROP TABLE IF EXISTS User")
            onCreate(db)
        } else if (oldVersion == 3) {
            // Check if column exists before adding it to avoid crash if it was partially added
            try {
                db.execSQL("ALTER TABLE Song ADD COLUMN isFavorite INTEGER DEFAULT 0")
            } catch (e: Exception) {
                // Column might already exist
            }
        }
    }

    fun insertSong(song: Song) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("title", song.title)
        values.put("artist", song.artist)
        values.put("image", song.image)
        values.put("lyric", song.lyric)
        values.put("file", song.file)
        values.put("isFavorite", if (song.isFavorite) 1 else 0)

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
                cursor.getString(5),
                cursor.getInt(6) == 1
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
                cursor.getString(5),
                cursor.getInt(6) == 1
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

    fun updateFavorite(songId: Int, isFavorite: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("isFavorite", if (isFavorite) 1 else 0)
        }
        db.update("Song", values, "id=?", arrayOf(songId.toString()))
        db.close()
    }

    fun getFavoriteSongs(): ArrayList<Song> {
        val songList = ArrayList<Song>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Song WHERE isFavorite = 1", null)
        if (cursor.moveToFirst()) {
            do {
                val song = Song(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 1
                )
                songList.add(song)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return songList
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