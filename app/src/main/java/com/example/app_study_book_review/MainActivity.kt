package com.example.app_study_book_review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_study_book_review.Adapter.BookAdapter
import com.example.app_study_book_review.Model.BestSellerDto
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks("BBB3034D8EE3CA422F1419ECB365C1D283CCF3C6A001F96B0A1DECC83906BBEE")
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

    }

    fun initRecyclerView() {
        adapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}