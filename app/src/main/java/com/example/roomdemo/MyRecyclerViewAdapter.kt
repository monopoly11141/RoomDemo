package com.example.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.ListItemBinding
import com.example.roomdemo.db.Subscriber

class MyRecyclerViewAdapter(
    private val clickListener: (Subscriber) -> Unit
) : RecyclerView.Adapter<MyViewHolder>() {

    private val subscribersArrayList = ArrayList<Subscriber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)

        return MyViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return subscribersArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscribersArrayList[position], clickListener)
    }

    fun setList(subscribers: List<Subscriber>) {
        subscribersArrayList.clear()
        subscribersArrayList.addAll(subscribers)
    }


}

class MyViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(subscriber: Subscriber, clickListener: (Subscriber) -> Unit) {
        val tvName = binding.tvName
        val tvEmail = binding.tvEmail

        tvName.text = subscriber.name
        tvEmail.text = subscriber.email

        binding.ll.setOnClickListener {
            clickListener(subscriber)
        }
    }

}