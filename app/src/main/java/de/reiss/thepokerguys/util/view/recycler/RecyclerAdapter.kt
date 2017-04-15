package de.reiss.thepokerguys.util.view.recycler

import android.support.v7.widget.RecyclerView
import de.reiss.thepokerguys.util.emptyMutableList

abstract class RecyclerAdapter<Item : HasId, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected var itemList: MutableList<Item> = emptyMutableList()

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun update(items: MutableList<Item>) {
        this.itemList = items
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Item {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).itemId
    }

}
