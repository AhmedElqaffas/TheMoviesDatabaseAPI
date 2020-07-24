package com.example.moviesretrofit.main.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.moviesretrofit.mediaDetails.MultiMediaDetailsActivity
import com.example.moviesretrofit.recyclersAdapters.MultiMediaRecyclerAdapter
import com.example.moviesretrofit.R
import com.example.moviesretrofit.main.FindMultiMediaViewModel
import com.example.moviesretrofit.dataClasses.MultiMedia
import com.example.moviesretrofit.dataClasses.MultiMediaResponse
import kotlinx.android.synthetic.main.fragment_find_movie.*
class FindMultiMediaFragment : Fragment(),
    MultiMediaRecyclerAdapter.MultiMediaRecyclerInteraction {

    private var page = 1
    private var totalPages = 0

    private var multiMediaRecyclerAdapter =
        MultiMediaRecyclerAdapter(
            MultiMediaRecyclerAdapter.Type.SEARCH,
            this
        )

    private lateinit var inflated: View
    private var searchTextChanged = true

    private val findMediaViewModel: FindMultiMediaViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflated = inflater.inflate(R.layout.fragment_find_movie, container, false)
        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerViewAdapter()
        setSearchBarChangeListener()
    }



    private fun initializeRecyclerViewAdapter(){
        multiMediaRecycler.adapter = multiMediaRecyclerAdapter
    }

    private fun makeRequest(){
        val foundMediaLiveData = findMediaViewModel.findMediaByName(page, searchBar.text.toString(), searchTextChanged)
        createDataObserverIfNotExists(foundMediaLiveData)
    }

    private fun createDataObserverIfNotExists(liveData: LiveData<MultiMediaResponse>){
        if(!liveData.hasActiveObservers()){
            liveData.observe(viewLifecycleOwner, Observer {
                extractObservedItems(it)
            })
        }
    }

    private fun extractObservedItems(response: MultiMediaResponse){
        overwriteOrAppendToRecycler(response.results)
        page = response.page
        totalPages = response.totalPages
    }

    private fun overwriteOrAppendToRecycler(mediaList: List<MultiMedia>){
        if(searchTextChanged) {
            multiMediaRecyclerAdapter.overwriteList(mediaList)
        }
        else{
            multiMediaRecyclerAdapter.appendToList(mediaList)
        }
    }

    private fun setSearchBarChangeListener(){
        searchBar.addTextChangedListener( object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                returnRecyclerViewToTop()
                resetSearchData()
            }

        })
    }

    private fun returnRecyclerViewToTop(){
        multiMediaRecycler.scrollToPosition(0)
    }

    private fun resetSearchData(){
        searchTextChanged = true
        page = 1
        makeRequest()
    }

    private fun getNextPageMovies(){
        if(page < totalPages) {
            page++
            searchTextChanged = false
            makeRequest()
        }
    }

    override fun onEndOfMultiMediaPage() {
        getNextPageMovies()
    }


    override fun onItemClicked(multiMedia: MultiMedia) {
        val intent = Intent(activity, MultiMediaDetailsActivity::class.java)
        intent.putExtra("media", multiMedia)
        startActivity(intent)
    }
}