package com.example.notepad.dao

import android.annotation.SuppressLint
import android.content.Context
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class PagesDB(context: Context) {
    private var db: DAO = RoomDB.getInstance(context)!!.pageDAO()

    @SuppressLint("SimpleDateFormat")
    fun getAll(): MutableList<Page> {
        return try {
            db.getAll(true)
        } catch (e_: Exception) {
            mutableListOf<Page>()
        }
    }

    fun getByDate(date: String): Page {
        return db.getByDate(date)
    }

    fun add(page: Page) {
        db.add(page)
    }

    fun update(page: Page) {
        db.update(page)
    }

    fun deleteByDate(date: String) {
        db.deleteByDate(date)
    }

    fun deleteAll() {
        db.deleteAll()
    }

    fun delete(page: Page) {
        db.delete(page)
    }
}