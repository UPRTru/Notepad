package com.example.notepad.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Pages")
class Page (@PrimaryKey var date: String, var text: String, var calendarDate: Long)