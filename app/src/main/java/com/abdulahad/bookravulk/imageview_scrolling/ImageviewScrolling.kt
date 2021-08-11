package com.abdulahad.bookravulk.imageview_scrolling

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.abdulahad.bookravulk.R
import kotlin.random.Random

class ImageViewScrolling: FrameLayout {

    private var AMIMATION_DUR = arrayListOf<Long>(50, 60, 70, 80).random()

    lateinit var currentImage: ImageView
    lateinit var nextImage: ImageView

    var last_result: Int = 0
    var old_result: Int = 0

    lateinit var eventEnd: IEventEnd

    constructor(context: Context) : super(context) {
        init(context)
    }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            init(context)
        }

    private fun init(context: Context) {
LayoutInflater.from(context).inflate(R.layout.imageview_scrolling, this )
        currentImage = rootView.findViewById(R.id.current_image)
        nextImage = rootView.findViewById(R.id.next_image)

        currentImage.visibility = View.INVISIBLE
val imgRand = arrayListOf<Int>(0, 1, 2).random()

        setImage(nextImage,imgRand )
        nextImage.translationY = height.toFloat()

    }

fun setValueRandom(image: Int, rotate_count: Int, chooseImg: Int ){
    currentImage.visibility = View.VISIBLE
currentImage.animate().translationY((-height).toFloat()).setDuration(AMIMATION_DUR).start()
    nextImage.translationY = nextImage.height.toFloat()
    nextImage.animate().translationY(0F).setDuration(AMIMATION_DUR).setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {

        }
        override fun onAnimationRepeat(animation: Animator?) {

        }
        override fun onAnimationEnd(animation: Animator?) {

            currentImage.translationY = 0f
            currentImage.visibility = View.INVISIBLE
            if(old_result != rotate_count){
                setImage(currentImage, old_result%3)
                setValueRandom(image, rotate_count, chooseImg)
                old_result++
            }else{
                last_result = 0
                old_result = 0
                setImage(nextImage, chooseImg)
                eventEnd.eventEnd(chooseImg%3, rotate_count)
            }

        }
        override fun onAnimationCancel(animation: Animator?) {

        }
    })


}

    private fun setImage(image_view: ImageView, value: Int) {
        if (value == Util().SLOT1)
            image_view.setImageResource(R.drawable.slot1)
        else if (value == Util().SLOT2)
            image_view.setImageResource(R.drawable.slot2)
        else if (value == Util().SLOT3)
            image_view.setImageResource(R.drawable.slot3)

        image_view.tag = value
        last_result = value
    }

    public fun getValue(): Int{
        return nextImage.tag.toString().toInt()
    }

    public fun setedEventEnd(eventEnd: IEventEnd){
this.eventEnd = eventEnd
    }

}

