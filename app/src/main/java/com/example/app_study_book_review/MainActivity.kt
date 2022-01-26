package com.example.app_study_book_review

import HistoryAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.app_study_book_review.Adapter.BookAdapter
import com.example.app_study_book_review.Model.BestSellerDto
import com.example.app_study_book_review.Model.History
import com.example.app_study_book_review.Model.SearchBookDto
import com.example.app_study_book_review.api.BookService
import com.example.app_study_book_review.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object: Callback<BestSellerDto>{

                override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {

                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS ... ")
                        return
                    }

                    response.body()?.let {
                        Log.d(TAG, it.toString())

                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }

                        adapter.submitList(it.books)
                    }

                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })



        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true // 이벤트 처리 했음
            }

            return@setOnKeyListener false    // 처리가 안됐고 다른 이벤트 처리?
        }

    }

    private fun search(keyword: String) {
        bookService.getBookByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object: Callback<SearchBookDto>{

                override fun onResponse(call: Call<SearchBookDto>, response: Response<SearchBookDto>) {

                    saveSearchKeyword(keyword)
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS ... ")
                        return
                    }

                    adapter.submitList(response.body()?.books.orEmpty())

                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })
    }

    private fun initRecyclerView() {
        adapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()
        }
        binding.historyRecyclerView.isVisible = true
    }
    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}