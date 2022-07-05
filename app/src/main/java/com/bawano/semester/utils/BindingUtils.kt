package com.bawano.semester.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("setImage")
fun setImage(view:ImageView, uri: String){
      Glide.with(view).load(uri).into(view);

}
