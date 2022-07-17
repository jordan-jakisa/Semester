package com.bawano.semester.models

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bawano.semester.databinding.RvCourseUnitItemBinding
import com.bawano.semester.utils.Utils
import kotlinx.parcelize.Parcelize


@Parcelize
class CourseUnit(
    var id: Int = 0,
    var title: String = "",
    var courseCode: String = "",
    var creditUnits: Int = 0,
    var code: String = "",
    var description: String = "",
    var role: Tier = Tier.CORE,
    var image: String = "",
) : Parcelable {
    val isEmpty: Boolean
        get() = code == "" || courseCode == "" || creditUnits == 0
}

object CourseUnitCallback : DiffUtil.ItemCallback<CourseUnit>() {
    override fun areItemsTheSame(oldItem: CourseUnit, newItem: CourseUnit) = oldItem == newItem
    override fun areContentsTheSame(oldItem: CourseUnit, newItem: CourseUnit) = oldItem.code == newItem.code
}

enum class Tier {
    CORE, ELECTIVE
}

enum class Grade {
    Year1, Year2, Year3, Year4
}

class CourseUnitAdapter(fragment: Fragment) :
    ListAdapter<CourseUnit, CourseUnitAdapter.CourseUnitViewHolder>(CourseUnitCallback) {
    private var position:Int =0
    private var courseUnitItemClick: Utils.OnCourseUnit
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= CourseUnitViewHolder.from(parent, courseUnitItemClick)


    override fun onBindViewHolder(holder: CourseUnitViewHolder, position: Int) {
        holder.bind(getItem(position))
        this.position = holder.adapterPosition
    }

    class CourseUnitViewHolder private constructor(
        b: RvCourseUnitItemBinding,
        onCourseUnit: Utils.OnCourseUnit
    ) :
        RecyclerView.ViewHolder(b.root) {
        var onCourseUnit: Utils.OnCourseUnit
        private val b: RvCourseUnitItemBinding
        fun bind(courseUnit: CourseUnit) {
            b.courseUnit = courseUnit
            b.root.setOnClickListener { onCourseUnit.click(courseUnit) }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                courseUnitItemClick: Utils.OnCourseUnit
            ): CourseUnitViewHolder {
                val b: RvCourseUnitItemBinding = RvCourseUnitItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CourseUnitViewHolder(b, courseUnitItemClick)
            }
        }

        init {
            this.b = b
            this.onCourseUnit = onCourseUnit
        }
    }

    init {
        courseUnitItemClick = fragment as Utils.OnCourseUnit
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        courseUnitItemClick.setPosition(position)
    }
}
