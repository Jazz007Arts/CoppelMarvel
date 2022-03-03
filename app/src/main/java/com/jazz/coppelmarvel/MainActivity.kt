package com.jazz.coppelmarvel

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jazz.coppelmarvel.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CharacterAdapter
    private var CharacterImages= mutableListOf<Character>()
    lateinit var dlg__espera:AlertDialog
    lateinit var recyclerView: RecyclerView
    lateinit var contexto:Context
    var isLoading = false
    var nLoader=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contexto=this
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svHeros.setOnQueryTextListener(this)
        initRecyclerView()
        recyclerView = findViewById(R.id.rvCharacters)
        initScrollListener()
    }

    private fun initRecyclerView() {
        adapter= CharacterAdapter(CharacterImages)
        binding.rvCharacters.layoutManager=LinearLayoutManager(this)
        binding.rvCharacters.adapter=adapter

        CoroutineScope(Dispatchers.IO).launch {
            val call  = getRetrofit().create(APIService::class.java).getHerosByLimit("characters?limit=20&ts=1&apikey=b24a207b05f3054a9a01c7ddcd6c3608&hash=84b388c92da8488b2898b488ef615b1a")
            val heroes=call.body()
            runOnUiThread {
                if (call.isSuccessful)
                {
                    val images =heroes?.data?.results ?: emptyList()
                    CharacterImages.clear()
                    CharacterImages.addAll(images)
                    adapter.notifyDataSetChanged()
                }
                else
                {

                }
            }
        }
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
                .baseUrl("https://gateway.marvel.com:443/v1/public/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun searchByName(query: String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val call  = getRetrofit().create(APIService::class.java).getHerosByLimit("characters?nameStartsWith=$query&ts=1&apikey=b24a207b05f3054a9a01c7ddcd6c3608&hash=84b388c92da8488b2898b488ef615b1a")
            val heroes=call.body()
            runOnUiThread {
                if (call.isSuccessful)
                {
                    val images =heroes?.data?.results ?: emptyList()
                    CharacterImages.clear()
                    CharacterImages.addAll(images)
                    adapter.notifyDataSetChanged()
                }
                else
                {

                }
            }
        }
    }

    private fun showError()
    {
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty())
        {
            searchByName(query.toLowerCase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun initScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == 19) {
                        //bottom of list!
                            showProgressDialog(contexto, "Espere mientras se carga la lista...")
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        nLoader+=20
        CoroutineScope(Dispatchers.IO).launch {
            val call  = getRetrofit().create(APIService::class.java).getHerosByLimit("characters?offset=$nLoader&ts=1&apikey=b24a207b05f3054a9a01c7ddcd6c3608&hash=84b388c92da8488b2898b488ef615b1a")
            val heroes=call.body()
            runOnUiThread {
                if (call.isSuccessful)
                {
                    val images =heroes?.data?.results ?: emptyList()
                    CharacterImages.clear()
                    CharacterImages.addAll(images)
                    adapter.notifyDataSetChanged()
                    isLoading=false
                    dlg__espera.dismiss()
                    recyclerView.scrollToPosition(0)
                }
                else
                {

                }
            }
        }
    }


    fun showProgressDialog(contexto: Context?, mensaje: String?) {
        val llPadding = 30
        val ll = LinearLayout(contexto)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        val progressBar = ProgressBar(contexto)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(contexto)
        tvText.text = mensaje
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 15f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(
            contexto!!
        )
        builder.setCancelable(false)
        builder.setView(ll)
        dlg__espera = builder.create()
        dlg__espera.show()
        val window: Window? = dlg__espera.getWindow()
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dlg__espera.getWindow()?.getAttributes())
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dlg__espera.getWindow()?.setAttributes(layoutParams)
        }
    }
}  