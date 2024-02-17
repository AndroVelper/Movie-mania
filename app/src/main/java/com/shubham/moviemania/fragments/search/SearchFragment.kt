package com.shubham.moviemania.fragments.search


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shubham.moviemania.R
import com.shubham.moviemania.adapters.HistoryAdapter
import com.shubham.moviemania.adapters.SearchAdapter
import com.shubham.moviemania.communicator.HistoryCommunicator
import com.shubham.moviemania.communicator.SearchCommunicator
import com.shubham.moviemania.databinding.FragmentSearchBinding
import com.shubham.moviemania.databinding.ItemSearchBinding
import com.shubham.moviemania.enums.HomeEnums
import com.shubham.moviemania.models.MovieData
import com.shubham.moviemania.models.MovieModal
import com.shubham.moviemania.utility.PreferenceManager
import com.shubham.moviemania.utility.Utility
import com.shubham.moviemania.utility.Utility.Companion.makeKeyboardDown
import com.shubham.moviemania.utility.Utility.Companion.movieDataArray


class SearchFragment : Fragment(), SearchCommunicator, HistoryCommunicator {


    private val preferenceManager: PreferenceManager by lazy {
        PreferenceManager.getInstance()
    }

    companion object {
        private var page = 1
        private var movieName = ""
        private var pageSize = 0
        private var indexToRemoveAndInsert = 0;
        private var isUserTypedNew: Boolean = true
        private lateinit var searchArray: ArrayList<String>
        private const val DEBOUNCE_TIMEOUT = 1000

    }


    private val binding: FragmentSearchBinding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get()
    }

    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(requireContext())
    }


    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var runnable: Runnable? = null

    private lateinit var searchDataArray: ArrayList<MovieData>
    private lateinit var adapter: SearchAdapter
    private lateinit var histroyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchDataArray = java.util.ArrayList()
        searchArray = preferenceManager.getInsertedArray(requireContext())
        setUpListeners()
        initializeRecyclerView()
        handleObservers()
        setUpClickListeners()
        initializeSpeechRecognizer()

    }

    private fun setUpClickListeners() {
        binding.apply {
            micRecorder.setOnClickListener {
                if (isPermissionGranted()) {
                    startListening()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                }

            }
            forwardArrow.setOnClickListener {
                findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
            }
        }
    }


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                startListening()
            } else {
                checkForPermission()
            }
        }

    private fun initializeRecyclerView() {
        binding.apply {
            searchRecycler.layoutManager = LinearLayoutManager(requireContext())
            adapter = SearchAdapter(searchDataArray, this@SearchFragment)
            searchRecycler.adapter = adapter


            historyRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            histroyAdapter = HistoryAdapter(searchArray, this@SearchFragment)
            historyRecycler.adapter = histroyAdapter

        }
    }


    private fun performDebouncedApiCall() {
        // Cancel the previous runnable if it exists
        runnable?.let {
            handler.removeCallbacks(it)
        }

        // Create a new runnable with the API call
        runnable = Runnable { // Make the API call with the provided query
            if (movieName.isNotEmpty()) {
                viewSwitching(true, noResult = false)
                loadMoreItems()

            }
        }

        // Schedule the API call after the debounce timeout
        runnable?.let { handler.postDelayed(it, DEBOUNCE_TIMEOUT.toLong()) }
    }

    private fun setUpListeners() {
        binding.apply {
            searchBarText.doAfterTextChanged { editable ->
                movieName = editable.toString()
                performDebouncedApiCall()
                isUserTypedNew = true
            }
            searchRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val visibleItemCount = layoutManager?.childCount
                    val totalItemCount = layoutManager?.itemCount
                    val lastVisibleItemPosition =
                        layoutManager?.findLastCompletelyVisibleItemPosition()
                    Log.e(
                        "Position",
                        "onScrolled: $visibleItemCount $totalItemCount $lastVisibleItemPosition $pageSize"
                    )
                    if (pageSize > searchDataArray.size) {
                        if ((lastVisibleItemPosition ?: 0) + 1 >= (totalItemCount ?: 0)) {
                            page += 1
                            loadMoreItems(page)
                            binding.progressBarRecycler.visibility = View.VISIBLE
                            isUserTypedNew = false
                        }
                    }
                }
            })
        }
    }

    private fun insertDataIntoArray() {
        if (searchArray.contains(movieName)) {
            return
        }
        if (searchArray.size >= 10) {
            swapElementAndInsert();
        } else {
            searchArray.add(movieName)
            binding.historyRecycler.adapter?.notifyItemInserted(searchArray.size - 1)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun swapElementAndInsert() {
        var i = searchArray.size - 1
        while (i > 0) {
            searchArray[i] = searchArray[i - 1]
            i--
        }
        searchArray[0] = movieName
        binding.historyRecycler.adapter?.notifyDataSetChanged()
    }


    private fun loadMoreItems(page: Int = 1) {
        if (movieName.isNotEmpty()) {
            viewModel.getTheMoviesForDashBoard(page.toString(), movieName)
            makeKeyboardDown(requireContext())
        }
    }


    private fun handleObservers() {
        viewModel.searchScreenData.observe(viewLifecycleOwner) {
            when (it.second) {
                HomeEnums.SEARCH.ordinal -> {
                    if (isUserTypedNew) {
                        searchDataArray.clear()
                    }

                    val data = it.first as MovieModal
                    try {
                        insertDataIntoArray()
                        pageSize = data.totalResults
                        searchDataArray.addAll(data.search)
                        viewSwitching(searchBarVisibility = false, noResult = false)
                    } catch (e: Exception) {
                        viewSwitching(searchBarVisibility = false, noResult = true)
                        e.printStackTrace()
                    }
                    adapter.updateData(searchDataArray)
                    binding.progressBarRecycler.visibility = View.GONE


                }

                HomeEnums.ERROR.ordinal -> {
                    Utility.showToast(requireContext(), it.first as String, true)
                }
            }
        }
    }


    /*
* Handle the movie recycler view.
* As this is a generic adapter can be used anyWhere with more readability and usage
* */

    override fun searchCommunicator(item: ItemSearchBinding, position: Int, lastPosition: Int) {

        if (lastPosition != position) {
            searchDataArray[position].isVisible = true
            searchDataArray[SearchAdapter.lastPosition].isVisible = false
            adapter.notifyItemChanged(SearchAdapter.lastPosition)
        } else {
            searchDataArray[position].isVisible = !searchDataArray[position].isVisible
        }

        adapter.notifyItemChanged(position)
        SearchAdapter.lastPosition = position
    }

    override fun searchFavouriteListener(position: Int, item: ItemSearchBinding) {
        val singleItem = searchDataArray[position]
        if (movieDataArray.contains(singleItem)) {
            movieDataArray.remove(singleItem)
            searchDataArray[position].isInFavourite = false
        } else {
            searchDataArray[position].isInFavourite = true
            movieDataArray.add(singleItem)
        }
        adapter.notifyItemChanged(position)
    }


    private fun initializeSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(speechListener)
    }


    private fun startListening() {
        speechRecognizer.startListening(getRecognizerIntent())
        binding.pleaseSpeakText.visibility = View.VISIBLE
        binding.micRecorder.setBackgroundResource(R.drawable.mic_background_dark)
    }

    private fun getRecognizerIntent(): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.ACTION_WEB_SEARCH)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 0)
        return intent
    }


    fun stopListening() {
        speechRecognizer.stopListening()
        binding.pleaseSpeakText.visibility = View.GONE
        binding.micRecorder.setBackgroundResource(R.drawable.mic_background)
    }

    private val speechListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Utility.debugLogs("On onReadyForSpeech method is called")
        }

        override fun onBeginningOfSpeech() {
            Utility.debugLogs("On onBeginningOfSpeech method is called")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Utility.debugLogs("On onRmsChanged method is called")
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Utility.debugLogs("On onBufferReceived method is called")
        }

        override fun onEndOfSpeech() {
            Utility.debugLogs("On onEndOfSpeech method is called")
        }

        override fun onError(error: Int) {
            Utility.debugLogs("On onError method is called $error")
            if (error == 7) {
                stopListening()
            }
        }

        override fun onResults(results: Bundle?) {
            Utility.debugLogs("On onResults method is called")
            // Called when recognition results are ready
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            // Process the recognized speech here
            if (matches != null) {
                movieName = matches[0]
                loadMoreItems()
                binding.searchBarText.setText(matches[0])
                Utility.debugLogs(matches[0])
            }
            stopListening()
        }

        override fun onPartialResults(partialResults: Bundle?) {
            Utility.debugLogs("On partial Result method is called")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Utility.debugLogs("On Event method is called")
        }

    }

    override fun historyCommunicator(position: Int) {
        movieName = searchArray[position]
        binding.searchBarText.setText(movieName)
        loadMoreItems()
        isUserTypedNew = true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun cancelButtonClicked(position: Int) {
        searchArray.removeAt(position)
        histroyAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.insertArray(requireContext(), searchArray)
        stopListening()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.RECORD_AUDIO
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun checkForPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.RECORD_AUDIO)) {
            Utility.showCustomDialog(
                getString(R.string.grant_permission),
                getString(R.string.cancel),
                getString(R.string.record_audio),
                getString(R.string.permission_grant_message),

                layoutInflater,
                requireContext(),
                null,
                null, {}
            ) {
                openSettings()
            }

        } else {
            Utility.showCustomDialog(
                getString(R.string.grant_permission),
                getString(R.string.cancel),
                getString(R.string.record_audio),
                getString(R.string.permission_grant_message),
                layoutInflater,
                requireContext(),
                null,
                null, {}
            ) {
                openSettings()
            }

        }
    }

    private fun viewSwitching(searchBarVisibility: Boolean, noResult: Boolean) {
        binding.apply {
            searchingProgress.isVisible = searchBarVisibility
            noResultFound.isVisible = noResult
            noResultImage.isVisible = noResult
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }


}