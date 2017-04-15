package de.reiss.thepokerguys.download

data class DownloadItem(val id: Long,
                        val url: String,
                        val status: Int,
                        val progress: Long,
                        val total: Long) : Comparable<DownloadItem> {


    fun percentProgress(): Int {
        if (total > 0) {
            val percent = progress.toDouble() / total * 100
            return percent.toInt()
        }
        return 0
    }

    override fun compareTo(other: DownloadItem): Int {
        return id.compareTo(other.id)
    }

}