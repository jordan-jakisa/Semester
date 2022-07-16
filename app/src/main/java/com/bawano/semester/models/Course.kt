package com.bawano.semester.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    var title: String?,
    var nickname: String?,
    var courseCode: String?,
    var image: String?,
    var description: String?
) : Parcelable

object CourseCallback : DiffUtil.ItemCallback<Course>() {
    override fun areItemsTheSame(oldItem: Course, newItem: Course) = oldItem == newItem
    override fun areContentsTheSame(oldItem: Course, newItem: Course) = oldItem.courseCode == newItem.courseCode
}


