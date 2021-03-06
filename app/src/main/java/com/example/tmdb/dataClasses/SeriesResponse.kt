package com.example.tmdb.dataClasses

import com.google.gson.annotations.SerializedName

class SeriesResponse(override val page: Int,
                     override val results: List<Series>,
                     @SerializedName("total_pages") override val totalPages: Int)
    : MultiMediaResponse(page, results, totalPages)