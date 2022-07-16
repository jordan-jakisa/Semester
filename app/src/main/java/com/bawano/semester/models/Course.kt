package com.bawano.semester.models

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bawano.semester.databinding.RvCourseItemBinding
import com.bawano.semester.utils.Utils
import kotlinx.parcelize.Parcelize


@Parcelize
data class Course(
    var title: String = "",
    var nickname: String = "",
    var courseCode: String = "",
    var image: String = "",
    var description: String = ""
) : Parcelable

object CourseCallback : DiffUtil.ItemCallback<Course>() {
    override fun areItemsTheSame(oldItem: Course, newItem: Course) = oldItem == newItem
    override fun areContentsTheSame(oldItem: Course, newItem: Course) =
        oldItem.courseCode == newItem.courseCode
}

class CourseAdapter(fragment: Fragment) :
    ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseCallback) {
    private val fragment: Fragment? = null
    private var courseItemClick: Utils.OnCourse
    private val flag = 0

    companion object {
        private const val HEADER = 1
        private const val ENROLL = 2
        const val COURSE = 3
    }

    init {
        courseItemClick = fragment as Utils.OnCourse
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CourseViewHolder.from(parent, courseItemClick)


    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) =
        holder.bind(getItem(position))


    class CourseViewHolder private constructor(
        b: RvCourseItemBinding,
        courseItemClick: Utils.OnCourse
    ) : RecyclerView.ViewHolder(b.root) {
        private val b: RvCourseItemBinding
        var courseItemClick: Utils.OnCourse
        fun bind(course: Course) {
            b.course = course
            b.root.setOnClickListener { courseItemClick.click(course) }
        }

        companion object {
            fun from(parent: ViewGroup, courseItemClick: Utils.OnCourse): CourseViewHolder {
                val binding = RvCourseItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return CourseViewHolder(binding, courseItemClick)
            }
        }

        init {
            this.b = b
            this.courseItemClick = courseItemClick
        }
    }

}


