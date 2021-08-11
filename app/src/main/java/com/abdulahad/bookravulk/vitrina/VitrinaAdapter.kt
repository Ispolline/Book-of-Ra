package com.abdulahad.bookravulk.vitrina

import android.content.Context
import android.graphics.Color
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.abdulahad.bookravulk.R
import com.abdulahad.bookravulk.common.PreferenceHelper.url
import com.abdulahad.bookravulk.extension.inflate
import com.abdulahad.bookravulk.models.ProductModel
import com.squareup.picasso.Picasso


class VitrinaAdapter(
        private val items: ArrayList<ProductModel>,
        val listner: (Int) -> Unit
): RecyclerView.Adapter<VitrinaAdapter.FourthExtraHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FourthExtraHolder {
        val inflatedView = parent.inflate(R.layout.vitrina_layout, false)
        return FourthExtraHolder(inflatedView, listner)

    }

    override fun onBindViewHolder(holder: FourthExtraHolder, position: Int) {
        val item = items[position]
        holder.positionSelected = position
        if(position == items.count()-1){
            holder.imageView.setImageResource(R.drawable.telegram)
            holder.vitrinaBtn.text = "Написать"

        }else{
            holder.setImg(item.image.toString())
        }

        holder.vitrinaBtn.setOnClickListener {
            holder.onClick(position)
        }

    }


    override fun getItemCount(): Int {
        return items.count()
    }


    //1
    class FourthExtraHolder(v: View, val listner: (Int) -> Unit) : RecyclerView.ViewHolder(v), View.OnClickListener {
        //2
        private var view: View = v
        var positionSelected: Int = 0
        var itemsCount: Int = 0
        lateinit var imageView: ImageView
        lateinit var vitrinaBtn: Button
        //3
        init {
            view = v
            imageView = view.findViewById(R.id.vitrina_img)
            vitrinaBtn = view.findViewById(R.id.vitrinaBtn)
            v.setOnClickListener(this)
        }

        fun setImg(url: String){
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .into(imageView);
        }

        fun onClick(position: Int) {
            listner(position)
        }

        override fun onClick(v: View?) {

        }

    }


}