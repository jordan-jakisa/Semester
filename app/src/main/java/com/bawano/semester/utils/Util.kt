package com.bawano.semester.utils

import com.bawano.semester.models.Course
import com.bawano.semester.models.CourseUnit
import com.bawano.semester.models.LastPage

interface Utils {

    interface FragmentPage{
        fun setLastPage(page: LastPage)
    }

    interface OnPdf {
        fun click(name: String)
    }

    interface OnCourse {
        fun click(course: Course)
        fun setPosition(pos: Int)
    }

    interface OnCourseUnit {
        fun click(courseUnit: CourseUnit)
        fun setPosition(pos: Int)
    }

    interface ActionBarVisibility {
        fun hide()
        fun show()
    }
}
