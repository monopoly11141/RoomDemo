package com.example.roomdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.db.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this, factory).get(SubscriberViewModel::class.java)

        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this

        initRecyclerView()

        subscriberViewModel.message.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun initRecyclerView() {
        val rvSubscriber = binding.rvSubscriber

        rvSubscriber.layoutManager = LinearLayoutManager(this)

        adapter =
            MyRecyclerViewAdapter({ selectedItem: Subscriber -> listItemClicked(selectedItem) })

        rvSubscriber.adapter = adapter

        displaySubscribersList()
    }


    private fun displaySubscribersList() {
        subscriberViewModel.subscribers.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(subscriber: Subscriber) {
        //Toast.makeText(this, "selected name is ${subscriber.name}", Toast.LENGTH_SHORT).show()
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}