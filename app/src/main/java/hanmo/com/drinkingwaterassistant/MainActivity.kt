package hanmo.com.drinkingwaterassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import hanmo.com.drinkingwaterassistant.util.MyViewPagerAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.view.ViewPager
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.anim.TabletTransformer
import hanmo.com.drinkingwaterassistant.lockscreen.util.LockScreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.tracking.MainActivityTrackingUtil
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.FragmentEventsBus
import io.reactivex.android.schedulers.AndroidSchedulers
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

        setLockScreen()
        initViewPagerControls()
        setHistroyButton()
        getSettingsFragmentObserve()

    }

    private fun setLockScreen() {
        if (RealmHelper.instance.getHasLockScreenBool()) {
            LockScreen.instance.init(this@MainActivity)
            LockScreen.instance.active()
        }
    }

    override fun onResume() {
        super.onResume()
        setAdmobBanner()
    }

    private fun setAdmobBanner() {
        mainAdView.visibility = View.INVISIBLE
        val adRequest = AdRequest.Builder().build()
        mainAdView.loadAd(adRequest)
        //adStatus = "Request - noCallback"
        mainAdView.adListener = object: AdListener(){
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                DLog.e("not loaded  : $p0")
                //adStatus = "Request - failedToLoad"
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                DLog.e("loaded loaded loaded loaded")
                //adStatus = "Request - load"
                mainAdView.visibility = View.VISIBLE
            }
        }
    }

    private fun getSettingsFragmentObserve() {
        FragmentEventsBus.instance.fragmentEventObservable.subscribe {
            when (it) {
                FragmentEventsBus.ACTION_FRAGMENT_CREATED -> {
                    waterHistoryIcon.visibility = View.GONE
                    waterAlarmIcon.visibility = View.GONE
                }
                FragmentEventsBus.ACTION_FRAGMENT_END_ANIMATION_FINISHED -> {
                    waterHistoryIcon.visibility = View.VISIBLE
                    waterAlarmIcon.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setHistroyButton() {
        waterHistoryIcon.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter { waterHistoryIcon.alpha > 0.5f }
                .doOnNext { MainActivityTrackingUtil.clickedHistoryButton() }
                .subscribe {
                    mainViewPager.setCurrentItem(1, true)
                }.apply { compositeDisposable.add(this) }

        waterAlarmIcon.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter { waterAlarmIcon.alpha > 0.5f }
                .doOnNext { MainActivityTrackingUtil.clickedAlarmButton() }
                .subscribe {
                    Snackbar.make(waterAlarmIcon, getString(R.string.prepareAlarmService), Snackbar.LENGTH_LONG).show()
                }.apply { compositeDisposable.add(this) }
    }

    //  viewpager change listener
    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            when(position) {
                0 -> { MainActivityTrackingUtil.showMainView() }
                1 -> { MainActivityTrackingUtil.showHistoyView() }
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            historyFrame.alpha = arg1
            waterHistoryIcon.alpha = (1 - arg1)
            waterAlarmIcon.alpha = (1 - arg1)
            when (arg0) {
                1 -> {
                    historyFrame.alpha = 1.0f
                    waterHistoryIcon.alpha = 0f
                    waterAlarmIcon.alpha = 0f
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

