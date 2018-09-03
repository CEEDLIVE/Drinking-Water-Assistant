package hanmo.com.drinkingwaterassistant.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import hanmo.com.drinkingwaterassistant.history.WaterHistoryFragment
import hanmo.com.drinkingwaterassistant.main.MainFragment

/**
 * View pager adapter
 * Created by hanmo on 2018. 9. 3..
 */

class MyViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        var fragment : Fragment? = null
        when(position) {
            0 -> {
                fragment = MainFragment.newInstance()
            }
            1 -> {
                fragment = WaterHistoryFragment.newInstance()
            }
        }

        return fragment
    }

}