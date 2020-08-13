package com.example.moviesretrofit.mediaDetails.credits

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.moviesretrofit.dataClasses.*
import com.example.moviesretrofit.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

object CreditsRepository {

    private lateinit var database: AppDatabase

    private var creditsList: List<Person> = listOf()
    private val creditsLiveData: MutableLiveData<List<Person>> = MutableLiveData()
    private var currentId = 0


    fun createDatabase(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    fun getMultimediaCredits(multimedia: MultiMedia): LiveData<List<Person>>{
        val castedMultimedia = castMultimedia(multimedia)
        creditsLiveData.value = null
        returnCachedOrNetworkData(castedMultimedia)
        return creditsLiveData
    }

    private fun castMultimedia(multimedia: MultiMedia): MultiMedia{
        return when (multimedia.mediaType) {
            "movie" -> Movie(multimedia.title, multimedia.id,
                multimedia.totalVotes, multimedia.poster, multimedia.cover, multimedia.rating,
                multimedia.releaseDate ,multimedia.mediaType, multimedia.overview, multimedia.popularity,
                multimedia.budget, multimedia.revenue)

            "tv" -> Series(multimedia.title, multimedia.id,
                multimedia.totalVotes, multimedia.poster, multimedia.cover, multimedia.rating,
                multimedia.releaseDate, multimedia.mediaType, multimedia.overview, multimedia.popularity)

            else -> throw Exception("Couldn't cast multimedia in cast repo")
        }
    }

    private fun returnCachedOrNetworkData(multimedia: MultiMedia){
        if(multimedia.id == currentId){
            sendCachedData()
        }
        else{
            sendNetworkData(
                multimedia
            )
        }
    }

    private fun sendCachedData(){
        creditsLiveData.postValue(creditsList)
    }

    private fun sendNetworkData(multimedia: MultiMedia){
        multimedia.makeCreditsRequest().apply {
            enqueueCallback(this!!, multimedia)
        }
    }

    private fun enqueueCallback(call: Call<CreditsResponse>, multimedia: MultiMedia){
        call.enqueue(object: Callback<CreditsResponse>{
            override fun onResponse(call: Call<CreditsResponse>, response: Response<CreditsResponse>) {
                response.body()?.let {
                    val creditsList = it.appendCastAndCrewLists()
                    creditsLiveData.postValue(creditsList)
                    updateRepository(
                        creditsList,
                        multimedia.id
                    )
                    updateDatabase(
                        creditsList,
                        multimedia
                    )
                }
            }

            override fun onFailure(call: Call<CreditsResponse>, t: Throwable) {
                Log.i("CastFragmentModel", "Couldn't load cast")
                if(creditsList.isEmpty() || multimedia.id != currentId)
                    returnDatabaseData(
                        multimedia
                    )
            }

        })
    }

    private fun returnDatabaseData(multimedia: MultiMedia){
        var databaseData: List<Person> = listOf()
        CoroutineScope(IO).launch {
            databaseData = multimedia.getCreditsFromDatabase(database)
        }.invokeOnCompletion {
            creditsLiveData.postValue(databaseData)
        }
    }

    private fun updateRepository(creditsList: List<Person>, id: Int){
        updateCurrentId(id)
        updateCachedCredits(creditsList)
    }

    private fun updateCurrentId(id: Int){
        currentId = id
    }

    private fun updateCachedCredits(creditsList: List<Person>){
        CreditsRepository.creditsList = creditsList
    }

    private fun updateDatabase(creditsList: List<Person>, multimedia: MultiMedia) {
        CoroutineScope(IO).launch {
            multimedia.saveCreditsInDatabase(database, creditsList)
        }

    }

}