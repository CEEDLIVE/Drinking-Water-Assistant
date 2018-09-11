package hanmo.com.drinkingwaterassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import hanmo.com.drinkingwaterassistant.util.MyViewPagerAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.ViewPager
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.anim.TabletTransformer
import hanmo.com.drinkingwaterassistant.main.MainFragment
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.FragmentEventsBus
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()

        if (RealmHelper.instance.queryFirst(Goals::class.java)?.goalWater == 0) {
            startActivity(MyTargetWaterActivity.newIntent(this@MainActivity))
        }

        initViewPagerControls()
        setHistroyButton()
        getSettingsFragmentObserve()

    }

    private fun getSettingsFragmentObserve() {
        FragmentEventsBus.instance.fragmentEventObservable.subscribe {
            when (it) {
                FragmentEventsBus.ACTION_FRAGMENT_CREATED -> {
                    waterHistoryIcon.visibility = View.GONE
                }
                FragmentEventsBus.ACTION_FRAGMENT_END_ANIMATION_FINISHED -> {
                    waterHistoryIcon.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun setHistroyButton() {
        waterHistoryIcon.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter { waterHistoryIcon.alpha > 0.5f }
                .subscribe {
                    mainViewPager.setCurrentItem(1, true)
                }.apply { compositeDisposable.add(this) }
    }

    //  viewpager change listener
    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {

        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            historyFrame.alpha = arg1
            waterHistoryIcon.alpha = (1 - arg1)
            when (arg0) {
                1 -> {
                    historyFrame.alpha = 1.0f
                    waterHistoryIcon.alpha = 0f
                    walesLogo.playAnimation()
                }
                else -> {
                    walesLogo.pauseAnimation()
                }
            }
        }

        override fun onPageScrollStateChanged(arg0: Int) {
            DLog.e("state : $arg0")
            //AlphaAnim.startFadeAlphaAnim(historyFrame)
        }
    }

    private fun initViewPagerControls() {

        val myViewPagerAdapter = MyViewPagerAdapter(supportFragmentManager)

        mainViewPager.adapter = myViewPagerAdapter
        mainViewPager.setPageTransformer(true, TabletTransformer())
        mainViewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        tabLayout.setupWithViewPager(mainViewPager, true)

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

