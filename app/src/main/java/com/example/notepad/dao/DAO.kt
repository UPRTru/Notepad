package com.example.notepad.dao

import androidx.room.*

@Dao
interface DAO {
    @Query("SELECT * FROM Pages ORDER BY " +
            "CASE WHEN :isAsc = 1 THEN calendarDate END ASC, " +
            "CASE WHEN :isAsc = 0 THEN calendarDate END DESC")
    fun getAll(isAsc: Boolean): MutableList<Page>

    @Query("SELECT * FROM Pages WHERE date = :date")
    fun getByDate(date: String): Page

    @Insert
    fun add(page: Page)

    @Update
    fun update(page: Page)

    @Query("DELETE FROM Pages WHERE date = :date")
    fun deleteByDate(date: String)

    @Query("DELETE FROM Pages")
    fun deleteAll()

    @Delete
    fun delete(page: Page)
}