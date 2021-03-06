package com.example.tmdb.recyclersAdapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdb.R
import com.example.tmdb.dataClasses.MultiMedia
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_media.view.*
import kotlin.math.roundToInt


class MultiMediaRecyclerAdapter(private val recyclerType: Int,
                                private val interactionListener: MultiMediaRecyclerInteraction
)
    : RecyclerView.Adapter<MultiMediaRecyclerAdapter.ViewHolder>() {

    object Type{
        const val BROWSE = 1
        const val SEARCH = 2
        const val FAVORITES = 3
    }

    private var multiMediaList = mutableListOf<MultiMedia>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val poster = itemView.mediaImage

        init{
            setClickListener()
        }

        fun bindMovieData(multiMedia: MultiMedia){
            setItemImage(multiMedia)
            customizeBrowsingItems(multiMedia)
        }

        private fun setClickListener(){
            itemView.setOnClickListener{
                interactionListener.onItemClicked(multiMediaList[layoutPosition])
            }
        }

        private fun setItemImage(multiMedia: MultiMedia) {
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w200${multiMedia.poster}")
                .fit()
                .placeholder(R.drawable.loading_movie_image)
                .into(poster)
        }

        private fun customizeBrowsingItems(multiMedia: MultiMedia){
            if(recyclerType == Type.BROWSE || recyclerType == Type.FAVORITES)
                setMediaRating(multiMedia)
        }

        private fun setMediaRating(multiMedia: MultiMedia){
            itemView.movieRating.text = multiMedia.rating.toString()
            setProgressBarBasedOnRating((multiMedia.rating * 10f).roundToInt())
        }

        private fun setProgressBarBasedOnRating(rating: Int) {
            itemView.ratingProgressBar.progress = rating
            when(rating){
                in 0 until 60 ->
                    itemView.ratingProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FF0000"))
                in 60 until 75 ->
                    itemView.ratingProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#FF9800"))
                else ->
                    itemView.ratingProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#00FF00"))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflated = determineInflatedLayout(parent)
        return ViewHolder(inflated)
    }

    private fun determineInflatedLayout(parent: ViewGroup): View {
        return when(recyclerType){
            Type.BROWSE, Type.FAVORITES ->
                LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
            else ->
                LayoutInflater.from(parent.context).inflate(R.layout.item_media_search, parent, false)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMovieData(multiMediaList[position])
        if(reachedEndOfPage(position) && recyclerType != Type.FAVORITES){
            interactionListener.onEndOfMultiMediaPage()
        }
    }

    private fun reachedEndOfPage(position: Int): Boolean{
        return position == multiMediaList.lastIndex
    }

    override fun getItemCount(): Int {
        return multiMediaList.size
    }

    fun appendToList(extraList: List<MultiMedia>?){
        extraList?.let{multiMediaList.addAll(it)}
        notifyDataSetChanged()
    }

    fun overwriteList(newList: List<MultiMedia>?){
        multiMediaList.clear()
        newList?.let { multiMediaList = it as MutableList<MultiMedia>}
        notifyDataSetChanged()
    }

    interface MultiMediaRecyclerInteraction{
        fun onEndOfMultiMediaPage()
        fun onItemClicked(multiMedia: MultiMedia)
    }

}