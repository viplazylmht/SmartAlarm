package com.viplazy.smartalarm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*

class ListAdapter(var context: Context, var list: List<Work>) : BaseAdapter() {

    private class ViewHolder(var countView : TextView, var times: TextView) {
        fun bindData(work: Work) {

            val dateCreate = Date(work.timestamp * 1000)
            countView.text = String.format("%d", work.cycle_count)
            times.text = String.format("%tH:%tM:%tS - %tb %td, %tY", dateCreate, dateCreate, dateCreate, dateCreate, dateCreate, dateCreate)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val holder : ViewHolder
        val v : View

        if (convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

            holder = ViewHolder(v.cycle_count, v.cycle_time)
            v.tag = holder
        }
        else {
            v = convertView
            holder = convertView.tag as ViewHolder
        }

        val item = getItem(position)
        holder.bindData(item)

        return convertView ?: v
    }

    override fun getItem(position: Int): Work {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].cycle_count.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}