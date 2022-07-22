package com.bawano.semester.ui.units

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bawano.semester.databinding.FragmentCourseUnitsBinding
import com.bawano.semester.models.Course
import com.bawano.semester.models.CourseUnit
import com.bawano.semester.models.CourseUnitAdapter
import com.bawano.semester.models.LastPage
import com.bawano.semester.ui.courses.CoursesFragment
import com.bawano.semester.utils.Constants
import com.bawano.semester.utils.PreferenceManager
import com.bawano.semester.utils.Utils
import com.bawano.semester.utils.errorDialog
import com.google.gson.Gson
import kotlinx.coroutines.launch


class CourseUnitsFragment : Fragment(), Utils.OnCourseUnit {

    private lateinit var b: FragmentCourseUnitsBinding
    private var course: Course? = null
    private var scroll: Int =0
    private val gson = Gson()
    private val courseUnitAdapter by lazy { CourseUnitAdapter(this) }
    private lateinit var page: Utils.FragmentPage
    private val args by navArgs<CourseUnitsFragmentArgs>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        page = context as Utils.FragmentPage
        lifecycleScope.launch {
            PreferenceManager(context).lastPage.collect {
                if (it.name == Constants.COURSE_UNITS) {
                    it.pageVariables[Constants.COURSES]?.let { c ->
                        course = gson.fromJson(c, Course::class.java)
                    }
                    it.pageVariables[CoursesFragment.SCROLL_KEY]?.let { s ->
                        scroll = Integer.valueOf(s)
                    }
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vm = ViewModelProvider(this)[CourseUnitsViewModel::class.java]
        b = FragmentCourseUnitsBinding.inflate(inflater, container, false)
        if (course == null) course = args.course
        b.shimmerLayout.startShimmer()
        b.recyclerView.adapter = courseUnitAdapter
        b.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        b.recyclerView.scrollToPosition(scroll)
        lifecycleScope.launch {
            vm.fetchCourseUnits(course!!.code).collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        list?.let {
                            courseUnitAdapter.submitList(listWithHeaders(list))
                        }
                    }
                    it.isFailure -> requireContext().errorDialog(
                        "Couldn't Fetch Courses",
                        (it.exceptionOrNull() ?: "Unknown error") as String
                    )
                }

                b.shimmerLayout.stopShimmer()
                b.shimmerLayout.visibility = View.GONE
            }
        }
        return b.root
    }

    private fun listWithHeaders(courseUnits: List<CourseUnit>): List<CourseUnit> {
        val list: MutableList<CourseUnit> = ArrayList()
        var semester = ""
        for (c in courseUnits) {
            if (semester != c.code.substring(0, c.code.length - 2)) {
                val unit = CourseUnit()
                if (c.code.contains("11")) unit.title =
                    "Year One Semester One" else if (c.code.contains("12")) unit.title =
                    "Year One Semester Two" else if (c.code.contains("21")) unit.title =
                    "Year Two Semester one" else if (c.code.contains("22")) unit.title =
                    "Year Two Semester Two" else if (c.code.contains("31")) unit.title =
                    "Year Three Semester one" else if (c.code.contains("32")) unit.title =
                    "Year Three Semester Two" else if (c.code.contains("41")) unit.title =
                    "Year Four Semester one" else unit.title = "Year Four Semester Two"
                list.add(unit)
                semester = c.code.substring(0, c.code.length - 2)
            }
            list.add(c)
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val map: HashMap<String, String> = HashMap()
        map[CoursesFragment.SCROLL_KEY] = scroll.toString()
        map[Constants.COURSES] = gson.toJson(course)
        page.setLastPage(LastPage(Constants.COURSES, map))
    }

    override fun click(courseUnit: CourseUnit) {
        val action = CourseUnitsFragmentDirections.actionNavCourseUnitsToDetailsFragment(courseUnit)
        findNavController().navigate(action)
    }

    override fun setPosition(pos: Int) {
        scroll = pos
    }
}