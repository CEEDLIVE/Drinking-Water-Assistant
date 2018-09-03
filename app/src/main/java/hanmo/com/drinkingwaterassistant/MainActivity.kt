package hanmo.com.drinkingwaterassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import hanmo.com.drinkingwaterassistant.util.MyViewPagerAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.ViewPager
import hanmo.com.drinkingwaterassistant.anim.TabletTransformer


class MainActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()

        /*if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.mainViewPager, MainFragment.newInstance(), "mainFragment")
                    .commit()
        }*/

        initViewPagerControls()

    }

    //  viewpager change listener
    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {

        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }
    }

    private fun initViewPagerControls() {


        val myViewPagerAdapter = MyViewPagerAdapter(supportFragmentManager)

        mainViewPager.adapter = myViewPagerAdapter
        mainViewPager.setPageTransformer(true, TabletTransformer())
        mainViewPager.addOnPageChangeListener(viewPagerPageChangeListener)

    }


    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun replaceFragment(fragment: Fragment, tag : String) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.mainViewPager, fragment, tag)
                .commit()
    }

}

