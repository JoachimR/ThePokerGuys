package net.thepokerguys.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import net.thepokerguys.util.logError
import rx.Observable
import rx.Observable.fromCallable
import java.util.*

open class DbProxy constructor(val context: Context) {

    private val db: PodcastDatabaseHelper

    init {
        db = PodcastDatabaseHelper(context)
        db.writableDatabase // call for first initialization
    }

    @Synchronized
    open fun loadAllPodcastDatabaseItemsObservable(): Observable<MutableList<PodcastDatabaseItem>> {
        return fromCallable { getAllPodcastDatabaseItems() }
    }

    @Synchronized
    open fun loadPodcastDatabaseItemObservable(url: String): Observable<PodcastDatabaseItem?> {
        return fromCallable { findByMp3Url(url) }
    }

    @Synchronized
    open fun findByMp3Url(mp3Url: String): PodcastDatabaseItem? {
        var cursor: Cursor? = null
        try {
            cursor = db.readableDatabase.query(
                    databaseTable.name,
                    null,
                    databaseTable.columns.mp3url.name + " =? ",
                    arrayOf(mp3Url),
                    "", "", "")

            if (cursor.moveToFirst()) {
                return converter.from(cursor)

            }
        } finally {
            cursor?.close()
        }
        return null
    }

    @Synchronized
    open fun getAllPodcastDatabaseItems(): MutableList<PodcastDatabaseItem> {
        val result = ArrayList<PodcastDatabaseItem>()

        val cursor = db.readableDatabase?.query(
                databaseTable.name,
                null, null, null, null, null, databaseTable.columns.publishDate.name + " DESC")

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    result.add(converter.from(cursor))
                }
                cursor.moveToFirst()
            } catch (e: Exception) {
                logError(e) {
                    "Error when converting from database"
                }
            } finally {
                try {
                    cursor.close()
                } catch(ignored: Exception) {
                }
            }
        }
        return result
    }

    @Synchronized
    open fun insertOrUpdatePodcasts(podcastItems: List<PodcastDatabaseItem>,
                                    keepPlayProgress: Boolean) {
        podcastItems.forEach {
            insertOrUpdatePodcast(it, keepPlayProgress)
        }
    }

    @Synchronized
    open fun insertOrUpdatePodcast(podcastItem: PodcastDatabaseItem,
                                   keepCurrentPlayProgress: Boolean) {
        val itemInDatabase = findByMp3Url(podcastItem.url)

        if (itemInDatabase != null) {
            updateItem(itemInDatabase, podcastItem, keepCurrentPlayProgress)
        } else {
            insertItem(podcastItem)
        }
    }

    @Synchronized
    fun updatePodcastDuration(url: String, duration: Int) {
        findByMp3Url(url)?.let { itemInDatabase ->
            updateItem(itemInDatabase = itemInDatabase,
                    newItem = itemInDatabase.copy(duration = duration),
                    keepPlayProgress = true)
        }
    }

    private fun updateItem(itemInDatabase: PodcastDatabaseItem,
                           newItem: PodcastDatabaseItem,
                           keepPlayProgress: Boolean) {
        if (itemInDatabase == newItem) {
            return
        }

        if (keepPlayProgress) {
            val storedDuration = itemInDatabase.duration
            if (storedDuration > 0) {
                newItem.duration = storedDuration
            }

            val storedProgress = itemInDatabase.progress
            newItem.progress = storedProgress
        }

        db.writableDatabase.update(databaseTable.name,
                converter.to(newItem),
                databaseTable.columns.mp3url.name + " = ?", arrayOf(itemInDatabase.url))
    }

    private fun insertItem(podcastItem: PodcastDatabaseItem) {
        db.writableDatabase.insert(databaseTable.name, null, converter.to(podcastItem))
    }

    class PodcastDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "PodcastItems.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase?) {
            val statement = "CREATE TABLE ${databaseTable.name} (${columns()});"
            db?.execSQL(statement)
        }

        private fun columns(): String = databaseTable.columns.id.name + " " + databaseTable.columns.id.type + " , " +
                databaseTable.columns.mp3url.name + " " + databaseTable.columns.mp3url.type + " , " +
                databaseTable.columns.publishDate.name + " " + databaseTable.columns.publishDate.type + " , " +
                databaseTable.columns.title.name + " " + databaseTable.columns.title.type + " , " +
                databaseTable.columns.description.name + " " + databaseTable.columns.description.type + " , " +
                databaseTable.columns.progress.name + " " + databaseTable.columns.progress.type + " , " +
                databaseTable.columns.duration.name + " " + databaseTable.columns.duration.type + " , " +
                databaseTable.columns.speed.name + " " + databaseTable.columns.speed.type + " , " +
                databaseTable.columns.filePath.name + " " + databaseTable.columns.filePath.type

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS " + databaseTable.name)
            onCreate(db)
        }

    }

    class DbColumn(val name: String, val type: String)

    object databaseTable {

        val name = "PodcastItems"

        object columns {

            val id = DbColumn("_id", "INT PRIMARY KEY")
            val mp3url = DbColumn("MP3URL", "TEXT")
            val publishDate = DbColumn("PUBLISH_DATE", "INT")
            val title = DbColumn("TITLE", "TEXT")
            val description = DbColumn("DESCRIPTION", "TEXT")
            val progress = DbColumn("PROGRESS", "INT")
            val duration = DbColumn("DURATION", "INT")
            val speed = DbColumn("SPEED", "REAL")
            val filePath = DbColumn("FILE_PATH", "TEXT")
        }
    }

    private object converter {

        fun to(podcastItem: PodcastDatabaseItem?): ContentValues {
            val columns = databaseTable.columns
            val values = ContentValues()
            val item = podcastItem
            if (item != null) {
                values.put(columns.mp3url.name, item.url)
                values.put(columns.publishDate.name, item.publishedDate.time)
                values.put(columns.title.name, item.title)
                values.put(columns.description.name, item.description)
                values.put(columns.progress.name, item.progress)
                values.put(columns.duration.name, item.duration)
                values.put(columns.speed.name, item.speed)
                values.put(columns.filePath.name, item.filePath)
            }
            return values
        }

        fun from(cursor: Cursor): PodcastDatabaseItem {
            val columns = databaseTable.columns
            return PodcastDatabaseItem(
                    url = cursor.getString(cursor.getColumnIndex(columns.mp3url.name)),
                    publishedDate = Date(cursor.getLong(cursor.getColumnIndex(columns.publishDate.name))),
                    title = cursor.getString(cursor.getColumnIndex(columns.title.name)),
                    description = cursor.getString(cursor.getColumnIndex(columns.description.name)),
                    progress = cursor.getInt(cursor.getColumnIndex(columns.progress.name)),
                    duration = cursor.getInt(cursor.getColumnIndex(columns.duration.name)),
                    speed = cursor.getFloat(cursor.getColumnIndex(columns.speed.name)),
                    filePath = cursor.getString(cursor.getColumnIndex(columns.filePath.name)))
        }
    }

}