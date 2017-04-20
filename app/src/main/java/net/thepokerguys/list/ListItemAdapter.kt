package net.thepokerguys.list

import android.view.LayoutInflater
import android.view.ViewGroup
import net.thepokerguys.AppActivity
import net.thepokerguys.R
import net.thepokerguys.util.view.recycler.RecyclerAdapter

class ListItemAdapter constructor(private val appActivity: AppActivity) :
        RecyclerAdapter<ListItem, ListItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(LayoutInflater.from(appActivity)
                .inflate(R.layout.podcast_list_item, parent, false), appActivity)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val isLastItem = position == itemCount - 1
        holder.bindItem(getItem(position), isLastItem)
    }

    fun notifyPlayTimeChanged(url: String, currentPlayTime: Int, setToFile: Boolean) {
        indexOf(url).let { index ->
            if (index > -1) {
                itemList[index] = itemList[index].copy(
                        currentPlayTime = currentPlayTime,
                        isPlayerSetToFile = setToFile)

                notifyItemChanged(index)
            }
        }
    }

    private fun indexOf(url: String): Int {
        if (itemList.isNotEmpty()) {
            for (index in 0..itemList.size) {
                if (itemList[index].url == url) {
                    return index
                }
            }
        }
        return -1
    }

}
