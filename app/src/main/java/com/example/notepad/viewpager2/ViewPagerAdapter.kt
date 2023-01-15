package com.example.notepad.viewpager2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.Notepad
import com.example.notepad.R
import com.example.notepad.dao.Page

class ViewPagerAdapter(private val textViewPage: TextViewPage):
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    var page: Int = 1
    var allPages: MutableList<Page> = mutableListOf()

    inner class ViewPagerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: LinearLayout = view.findViewById(R.id.Text)
        val date: TextView = view.findViewById(R.id.BookDatText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return ViewPagerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        if (allPages.isNotEmpty()) {
            val page: Page = allPages[0]
            allPages.removeAt(0)
            val allText: List<String> = page.text.split(Notepad.space)
            for (s in allText) {
                holder.text.addView(textViewPage.textView(s, page.date))
            }
            holder.date.text = page.date
        } else {
            holder.date.text = Notepad.date()
        }
    }

    override fun getItemCount(): Int = page

    interface TextViewPage {
        fun textView(text: String, date: String): TextView
    }

}