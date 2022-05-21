package com.example.google_translate.activity

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.google_translate.R
import com.example.google_translate.adapter.NoteAdapter
import com.example.google_translate.data.api.ApiClient
import com.example.google_translate.data.api.ApiService
import com.example.google_translate.data.database.AppDatabase
import com.example.google_translate.data.database.entity.Note
import com.example.google_translate.databinding.ActivityMainBinding
import com.example.google_translate.repository.TranslateRepository
import com.example.google_translate.repository.factory.TranslateViewModelFactory
import com.example.google_translate.ui.TranslateViewModel
import com.example.google_translate.utils.Constants.EN
import com.example.google_translate.utils.Constants.UZ
import com.example.google_translate.utils.SwipeToDeleteCallback
import com.example.google_translate.utils.UiStateList
import com.example.google_translate.utils.UiStateObject
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TranslateViewModel
    private lateinit var binding: ActivityMainBinding
    private var target: String = EN
    private var source: String = UZ
    lateinit var x: String
    private val adapter by lazy { NoteAdapter() }
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupViewModel()
        setupTranslationObserver()
        setupDetectionObserver()
        getTranslatesObservers()


    }

    private fun getTranslatesObservers() {
        viewModel.getAllNotes()
        lifecycleScope.launchWhenStarted {
            viewModel.getAllNotesState.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        //show progress
                    }

                    is UiStateList.SUCCESS -> {
                        adapter.submitList(it.data)
                        Log.d("@@@room", it.data.toString())
                    }
                    is UiStateList.ERROR -> {
                        Log.d("@@@", it.message)

                    }
                    else -> Unit

                }
            }
        }
    }


    private fun setupUI() {
        changeButtons(target, source)
        binding.icStart.setOnClickListener {
            if (source == UZ) {
                viewModel.addNote(
                    Note(
                        uz = binding.etEnterText.text.toString(),
                        en = binding.tvTranslatedText.text.toString()
                    )
                )
            } else {
                viewModel.addNote(
                    Note(
                        en = binding.etEnterText.text.toString(),
                        uz = binding.tvTranslatedText.text.toString()
                    )
                )
            }
            Toast.makeText(this, "Successfully saved", Toast.LENGTH_LONG).show()
            viewModel.getAllNotes()
        }
        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel.removeNote(position)
            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvMain)


        binding.rvMain.adapter = adapter
        binding.icSwapLanguage.setOnClickListener {
            changeLanguage()
            sendRequest(target, source)
            changeButtons(target, source)

            binding.llSuggestionLanguage.visibility = View.GONE

        }
        binding.llSuggestionLanguage.setOnClickListener {
            changeLanguage()
            sendRequest(target, source)
            binding.llSuggestionLanguage.visibility = View.GONE
            changeButtons(target, source)
        }
        binding.etEnterText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                job?.cancel()
                job = lifecycleScope.launchWhenStarted {
                    delay(1000)
                    if (p0.toString().isNotEmpty()) {
                        sendRequest(target, source)

                    }


                }
            }

        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            TranslateViewModelFactory(
                TranslateRepository(
                    AppDatabase.getInstance(this).noteDao(),
                    ApiClient.createServiceWithAuth(ApiService::class.java)
                )
            )
        )[TranslateViewModel::class.java]
    }


    private fun setupTranslationObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.translationState.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {
                        binding.tvTranslatedText.text = it.data.data.translations[0].translatedText

                        Log.d("@@@", it.data.toString())
                    }
                    is UiStateObject.ERROR -> {
                        Log.d("@@@", it.message)

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupDetectionObserver() {

        lifecycleScope.launchWhenStarted {
            viewModel.detectionState.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        //show progress
                    }

                    is UiStateObject.SUCCESS -> {

                        if (it.data.data.detections[0][0].language == "uz" && source == EN) {
                            binding.llSuggestionLanguage.visibility = View.VISIBLE
                        } else {
                            binding.llSuggestionLanguage.visibility = View.GONE
                        }
                        Log.d("d@@@", it.data.toString())
                    }

                    is UiStateObject.ERROR -> {
                        Log.d("@@@", it.message)

                    }
                    else -> Unit

                }
            }

        }

    }


    private fun sendRequest(target: String, source: String) {
        viewModel.getTranslation(binding.etEnterText.text.toString(), target, source)
        viewModel.getDetection(binding.etEnterText.text.toString())
    }

    private fun changeButtons(target: String, source: String) {
        binding.tvFrist.text=source
        binding.tvSecound.text=target
        binding.bFirst.text = source
        binding.bSecond.text = target
    }

    private fun changeLanguage() {
        x = target
        target = source
        source = x
    }


}