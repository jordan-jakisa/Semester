package com.bawano.semester.models

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bawano.semester.databinding.RvCourseUnitItemBinding
import com.bawano.semester.databinding.RvHeaderItemBinding
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
    var notes: ArrayList<String> = ArrayList(),
    var image: String = "",
) : Parcelable {
    val isEmpty: Boolean
        get() = code == "" || courseCode == "" || creditUnits == 0
}

object CourseUnitCallback : DiffUtil.ItemCallback<CourseUnit>() {
    override fun areItemsTheSame(oldItem: CourseUnit, newItem: CourseUnit) = oldItem == newItem
    override fun areContentsTheSame(oldItem: CourseUnit, newItem: CourseUnit) =
        oldItem.code == newItem.code
}

enum class Tier {
    CORE, ELECTIVE
}

enum class Grade {
    Year1, Year2, Year3, Year4
}

class CourseUnitAdapter(fragment: Fragment) :
    ListAdapter<CourseUnit, RecyclerView.ViewHolder>(CourseUnitCallback) {
    private var position: Int = 0
    private var courseUnitItemClick: Utils.OnCourseUnit

    private val HEADER = 1
    private val CU = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        CU -> CourseUnitViewHolder.from(parent, courseUnitItemClick)
        else -> HeaderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(getItem(position))
            else -> (holder as CourseUnitViewHolder).bind(getItem(position))
        }
        this.position = holder.adapterPosition
    }


    override fun getItemViewType(position: Int) = if (getItem(position).isEmpty) HEADER else CU

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

    class HeaderViewHolder private constructor(b: RvHeaderItemBinding) :
        RecyclerView.ViewHolder(b.root) {
        private val b: RvHeaderItemBinding
        fun bind(courseUnit: CourseUnit) {
            b.headerTv.text = courseUnit.title
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val binding: RvHeaderItemBinding =
                    RvHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return HeaderViewHolder(binding)
            }
        }

        init {
            this.b = b
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
