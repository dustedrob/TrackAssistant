package me.roberto.fixiecalc.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_gear_list.*
import me.roberto.fixiecalc.R
import me.roberto.fixiecalc.Rollout
import me.roberto.fixiecalc.calculations.Calculations
import me.roberto.fixiecalc.di.ApplicationClass
import me.roberto.fixiecalc.di.ViewModelFactory
import me.roberto.kitso.ui.GearViewModel
import javax.inject.Inject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [FavoritesFragment.OnFragmentInteractionListener] interface.
 */
class FavoritesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory:ViewModelFactory
    @Inject
    lateinit var prefs: SharedPreferences
    private lateinit var viewModel:GearViewModel
    private lateinit var rollout: Rollout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationClass.appComponent.inject(this)
        activity?.let {
            viewModel = ViewModelProviders.of(it,viewModelFactory).get(GearViewModel::class.java)
        }
        viewModel.gears.observe(this, observer)
        rollout = Rollout.values()[prefs.getInt(BottomActivity.PREFS_ROLLOUT, 0)]
    }


    private val observer: Observer<List<Gear>> = Observer { list ->


        if (list.isEmpty()) {
            recycler_list.visibility = View.GONE
            empty_list.visibility = View.VISIBLE
        } else {

            empty_list.visibility = View.GONE
            recycler_list.visibility = View.VISIBLE

            val sortedlist = list.sortedWith(Comparator<Gear> { p0, p1 ->
                val gear0 = Calculations.calculateGear(p0!!.wheelSize, p0.chainRing, p0.cog, rollout)

                val gear1 = Calculations.calculateGear(p1!!.wheelSize, p1.chainRing, p1.cog, rollout)


                if (gear0 < gear1) {
                    -1
                } else if (gear0 == gear1) {
                    0
                } else {
                    1
                }
            })
            gearAdapter.gears.clear()


            gearAdapter.addAll(sortedlist)
        }

    }

    private lateinit var gearAdapter: GearRecyclerViewAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gear_list, container, false)


        gearAdapter = GearRecyclerViewAdapter()
        // Set the gearAdapter


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_list.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(activity)
            adapter = gearAdapter
        }
    }
}
