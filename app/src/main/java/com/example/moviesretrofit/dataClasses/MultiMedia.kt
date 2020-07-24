package com.example.moviesretrofit.dataClasses

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MultiMedia(@SerializedName("name", alternate = ["title"]) val title: String,
                      val id: Int,
                      @SerializedName("vote_count") val totalVotes: Int,
                      @SerializedName("poster_path") val poster: String,
                      @SerializedName("backdrop_path") val cover: String,
                      @SerializedName("vote_average") val rating: Float,
                      @SerializedName("media_type") val mediaType: String,
                      val overview: String): Serializable{

    object Constants{
        const val POPULAR = 0
        const val RATED = 1
    }
}

