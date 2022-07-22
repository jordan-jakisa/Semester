package com.bawano.semester.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bawano.semester.models.Course
import com.bawano.semester.models.CourseUnit
import com.bumptech.glide.Glide


@BindingAdapter("setImage")
fun setImage(view:ImageView, course: Course)=
      Glide.with(view).load(course.image.ifEmpty { course.title.letterDrawable()} ).into(view)

@BindingAdapter("setImage")
fun setImage(view:ImageView, course: CourseUnit) {
      val d = course.title.letterDrawable()
      Glide.with(view).load(course.image.ifEmpty { d }).into(view)
}
@BindingAdapter("setImage")
fun setImage(view:ImageView, string: String)=
      Glide.with(view).load(string.ifEmpty { "User".letterDrawable()} ).into(view)


