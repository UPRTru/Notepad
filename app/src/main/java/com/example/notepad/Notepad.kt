package com.example.notepad

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.notepad.dao.Page
import com.example.notepad.dao.PagesDB
import com.example.notepad.viewpager2.ViewPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.relex.circleindicator.CircleIndicator3
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class Notepad : AppCompatActivity(), ViewPagerAdapter.TextViewPage {
    companion object {
        const val space: String = ";=::space::=;"

        @SuppressLint("SimpleDateFormat")
        fun date(): String {
            val date = Calendar.getInstance().time
            val formatDate = SimpleDateFormat("dd.MM.yyy")
            return formatDate.format(date)
        }
    }
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var btnDelete: FloatingActionButton
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnExit: FloatingActionButton
    private val db: PagesDB by lazy {
        PagesDB(this)
    }
    private val textViewPage: ViewPagerAdapter.TextViewPage = this

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        window.setDecorFitsSystemWindows(false)
        if (window.insetsController != null) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars()
                    or WindowInsets.Type.navigationBars())
            window.insetsController!!.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun maxPage(): Int {
        return if (db.getAll().isNotEmpty()) {
            if (db.getAll().last().date != date()) {
                db.getAll().size + 1
            } else {
                db.getAll().size
            }
        } else {
            1
        }
    }

    private fun createViewPager() {
        viewPagerAdapter = ViewPagerAdapter(textViewPage)
        viewPagerAdapter.page = maxPage()
        viewPagerAdapter.allPages = db.getAll()
        viewPager.offscreenPageLimit = 2000
        viewPager.adapter = viewPagerAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        indicator.setViewPager(viewPager)
        viewPager.setCurrentItem(viewPager.adapter!!.itemCount - 1, true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateViewPager(numberPage: Int) {
        viewPagerAdapter.page = maxPage()
        viewPagerAdapter.allPages = db.getAll()
        viewPager.adapter = viewPagerAdapter
        viewPager.adapter!!.notifyDataSetChanged()
        indicator.setViewPager(viewPager)
        viewPager.setCurrentItem(numberPage, true)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        castView()
        createViewPager()
        val context: Context = this
        btnDelete.setOnClickListener {
            viewPager.apply {
                val builder = AlertDialog.Builder(this.context)
                val v: View = layoutInflater.inflate(R.layout.delete_layout, null)
                builder.setView(v)
                val alert = builder.create()
                val text = v.findViewById<TextView>(R.id.TextViewDelPage)
                val ok = v.findViewById<Button>(R.id.BtDPok)
                val back = v.findViewById<Button>(R.id.BtDPback)
                text.text = "Delete page?"
                alert.setView(v)
                alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alert.show()
                alert.setCancelable(false)
                ok.setOnClickListener {
                    if (viewPager.adapter!!.itemCount != db.getAll().size
                        && viewPager.currentItem == viewPager.adapter!!.itemCount - 1) {
                        Toast.makeText(context, "Empty page", Toast.LENGTH_SHORT).show()
                    } else {
                        val idBeforePage: Int = if (viewPager.currentItem == 0) {
                            viewPager.currentItem
                        } else {
                            viewPager.currentItem - 1
                        }
                        db.delete(db.getAll()[viewPager.currentItem])
                        updateViewPager(idBeforePage)
                    }
                    alert.dismiss()
                }

                back.setOnClickListener {
                    alert.dismiss()
                }
            }
        }

        btnAdd.setOnClickListener {
            viewPager.apply {
                val builder = AlertDialog.Builder(this.context)
                val v: View = layoutInflater.inflate(R.layout.write_text, null)
                builder.setView(v)
                val alert = builder.create()
                val ok = v.findViewById<Button>(R.id.BtWTok)
                val back = v.findViewById<Button>(R.id.BtWTback)
                val editText = v.findViewById<EditText>(R.id.ETMl)
                alert.setView(v)
                alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alert.show()
                alert.setCancelable(false)
                ok.setOnClickListener {
                    val text = "${dateTime()}\n${editText.text}$space"
                    var plusPage: Int = -1
                    try {
                        val loadPage: Page = db.getByDate(date())
                        loadPage.text += text
                        db.update(loadPage)
                    } catch (e_: Exception) {
                        db.add(Page(date(), text, Calendar.getInstance().time.time))
                        plusPage = 0
                    }
                    updateViewPager(viewPager.adapter!!.itemCount + plusPage)
                    alert.dismiss()
                }
                back.setOnClickListener {
                    alert.dismiss()
                }
            }
        }

        btnExit.setOnClickListener {
            viewPager.apply {
                finishAffinity()
                moveTaskToBack(true)
                exitProcess(-1)
            }
        }
    }

    private fun castView() {
        viewPager = findViewById(R.id.view_pager2)
        indicator = findViewById(R.id.indicator)
        btnDelete = findViewById(R.id.btn_delete)
        btnAdd = findViewById(R.id.btn_add)
        btnExit = findViewById(R.id.btn_exit)
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateTime(): String{
        val date = Calendar.getInstance().time
        val hh = SimpleDateFormat("HH")
        val formatDateHH = hh.format(date)
        val mm = SimpleDateFormat("mm")
        val formatDateMm = mm.format(date)
        return "${formatDateHH}:${formatDateMm}"
    }

    @SuppressLint("SetTextI18n")
    override fun textView(text: String, date: String): TextView {
        val textView = TextView(viewPager.context)
        textView.text = "$text\n\n"
        textView.setOnClickListener {
            viewPager.apply {
                val builder = AlertDialog.Builder(this.context)
                val v: View = layoutInflater.inflate(R.layout.delete_layout, null)
                builder.setView(v)
                val alert = builder.create()
                val textAlert = v.findViewById<TextView>(R.id.TextViewDelPage)
                val ok = v.findViewById<Button>(R.id.BtDPok)
                val back = v.findViewById<Button>(R.id.BtDPback)
                textAlert.text = "Delete text?"
                alert.setView(v)
                alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                alert.show()
                alert.setCancelable(false)

                ok.setOnClickListener {
                    val idBeforePage: Int
                    val page: Page = db.getByDate(date)
                    page.text = page.text.replace(text + space, "")
                    if (page.text == "") {
                        idBeforePage = if (viewPager.currentItem == 0) {
                            viewPager.currentItem
                        } else {
                            viewPager.currentItem - 1
                        }
                        db.delete(page)
                    } else {
                        idBeforePage = viewPager.currentItem
                        db.update(page)
                    }
                    updateViewPager(idBeforePage)
                    alert.dismiss()
                }

                back.setOnClickListener {
                    alert.dismiss()
                }
            }
        }
        return textView
    }

}