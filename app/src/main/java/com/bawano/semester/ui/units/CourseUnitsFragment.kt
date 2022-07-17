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
import com.bawano.semester.models.*
import com.bawano.semester.ui.courses.CoursesFragment
import com.bawano.semester.utils.Constants
import com.bawano.semester.utils.PreferenceManager
import com.bawano.semester.utils.Utils
import com.bawano.semester.utils.errorDialog
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CourseUnitsFragment : Fragment(), Utils.OnCourseUnit {

    private lateinit var b: FragmentCourseUnitsBinding
    private lateinit var course: Course
    private var scroll: Int =0
    private val gson = Gson()
    private val courseUnitAdapter by lazy { CourseUnitAdapter(this) }
    private lateinit var page: Utils.FragmentPage
    private val args by navArgs<CourseUnitsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        course = args.course
    }

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
        b.shimmerLayout.startShimmer()
        b.recyclerView.adapter = courseUnitAdapter
        b.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        b.recyclerView.scrollToPosition(scroll)
        lifecycleScope.launch {
            vm.fetchCourseUnits(course.code).collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        list?.let {
                            courseUnitAdapter.submitList(list)
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