package com.example.tmdb.main.dataRepositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tmdb.dataClasses.Movie
import com.example.tmdb.dataClasses.MultiMedia
import com.example.tmdb.networking.MultiMediaAPI
import com.example.tmdb.dataClasses.MultiMediaResponse
import com.example.tmdb.dataClasses.MovieResponse
import com.example.tmdb.database.AppDatabase
import com.example.tmdb.networking.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RatedMoviesRepository{

    private const val key = "097aa1909532e2d795f4f414cf4bc13f"

    private var multiMediaAPI: MultiMediaAPI = RetrofitClient.getRetrofitClient().create(MultiMediaAPI::class.java)
    private lateinit var database: AppDatabase

    private var ratedMovies = mutableListOf<MultiMedia>()
    private var currentPage = 1
    private var ratedMoviesTotalPages = 0

    private val ratedMoviesResponseLiveData: MutableLiveData<List<MultiMedia>> = MutableLiveData()

    private lateinit var userId: String

    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun makeRatedMoviesRequest(firstRequest: Boolean, userId: String): LiveData<List<MultiMedia>> {
        if(firstRequest) {
            sendCachedOrNetworkData(userId)
        }

        else if(currentPage < ratedMoviesTotalPages){
            returnNetworkData(currentPage + 1)
        }

        return ratedMoviesResponseLiveData
    }

    private fun sendCachedOrNetworkData(userId: String) {
        if(ratedMovies.isNotEmpty() && this.userId == userId){
            returnCachedData()
        }

        else{
            this.userId = userId
            returnNetworkData(1)
        }

    }

    private fun returnNetworkData(page: Int){
        multiMediaAPI.getHighRatedMovies(key, page)
            .apply { enqueueCallback(this) }
    }

    private fun returnCachedData(){
        ratedMoviesResponseLiveData.value = ratedMovies
    }

    private fun enqueueCallback(call: Call<MovieResponse>) {
        call.enqueue(object: Callback<MovieResponse> {

            override fun onResponse(call: Call<MovieResponse>,
                                    response: Response<MovieResponse>
            ) {
                response.body()?.let {
                    ratedMoviesResponseLiveData.postValue(it.results)
                    updateRepository(it)
                    updateDatabase(it.results)
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("Movies error", "Couldn't get movies list from API")
                if(ratedMovies.isEmpty())
                    getRatedMoviesFromDatabase()
            }
        })
    }

    private fun getRatedMoviesFromDatabase(){
        var databaseData: List<Movie> = listOf()
        CoroutineScope(IO).launch {
            databaseData = database.getMultimediaDao().getTopRatedMovies()
        }.invokeOnCompletion {
            if(databaseData.isNotEmpty()){
                ratedMoviesResponseLiveData.postValue(databaseData)
                appendResultItemsToList(databaseData)
            }
        }
    }

    private fun updateRepository(response: MultiMediaResponse){
        updateCurrentPage(response.page)
        appendResultItemsToList(response.results)
        saveTotalNumberOfPages(response.totalPages)
    }

    private fun updateCurrentPage(page: Int){
        currentPage = page
    }

    private fun appendResultItemsToList(results: List<MultiMedia>) {
        ratedMovies.addAll(results)
    }

    private fun saveTotalNumberOfPages(totalPages: Int){
        ratedMoviesTotalPages = totalPages
    }

    private fun updateDatabase(movies: List<Movie>){
        CoroutineScope(IO).launch {
            database.getMultimediaDao().insertMovies(movies)
        }

    }
}