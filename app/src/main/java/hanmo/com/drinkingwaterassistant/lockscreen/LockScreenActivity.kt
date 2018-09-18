package hanmo.com.drinkingwaterassistant.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.lockscreen.util.UnLock
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_lockscreen.*
import java.util.concurrent.TimeUnit
import android.view.animation.AnimationUtils
import hanmo.com.drinkingwaterassistant.lockscreen.util.LockScreenMenuAdapter
import hanmo.com.drinkingwaterassistant.realm.model.WaterHistory
import java.util.*
import io.realm.Realm
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.support.v7.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.lockscreen.background.Background
import hanmo.com.drinkingwaterassistant.lockscreen.background.ChangeBackgroundActivity
import hanmo.com.drinkingwaterassistant.lockscreen.settings.LockScreenSettingsActivity
import hanmo.com.drinkingwaterassistant.lockscreen.util.PathFromURIUtil.getRealPathFromURI
import hanmo.com.drinkingwaterassistant.lockscreen.util.UnLockSwipe
import hanmo.com.drinkingwaterassistant.realm.model.LockScreenTable
import hanmo.com.drinkingwaterassistant.tracking.LockScreenTrackingUtil
import hanmo.com.drinkingwaterassistant.util.DLog
import java.io.File


/**
 * Created by hanmo on 2018. 5. 22..
 */
class LockScreenActivity : AppCompatActivity() {

    private var waterTable : Goals? = null
    private var lockscreenTable : LockScreenTable? = null

    private lateinit var compositeDisposable: CompositeDisposable
    private val mTimeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                Intent.ACTION_TIME_TICK -> {
                    setTime()
                }
            }
        }
    }

    private val onItemClickListener = object : LockScreenMenuAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            when (position) {
                0 -> {
                    val backgroundIntent = ChangeBackgroundActivity.newIntent(applicationContext)
                    backgroundIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    backgroundIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(backgroundIntent)
                    overridePendingTransition(R.anim.slide_in_right, 0)
                }
                1 -> {
                    val settingsIntent = LockScreenSettingsActivity.newIntent(applicationContext)
                    settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(settingsIntent)
                    overridePendingTransition(R.anim.slide_in_right, 0)
                }
            }
            setLottieAnimator(false)
            lcMenuList.visibility = View.GONE
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lockscreen)
        LockScreenTrackingUtil.showLockScreenView()

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasFocus) window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
        }
    }
    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        super.onAttachedToWindow()
    }

    override fun onResume() {
        super.onResume()

        setAdmob()

        compositeDisposable = CompositeDisposable()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK)

        registerReceiver(mTimeReceiver, intentFilter)
        waterTable = RealmHelper.instance.getTodayWaterGoal()
        lockscreenTable =  RealmHelper.instance.queryFirst(LockScreenTable::class.java)
        setAddButton()
        setProgressBar()
        setWaterType()
        setUnlock()
        setTime()
        setMenu()
        setBackground()
        DWApplication.lockScreenShow = true
    }

    private fun setWaterType() {
        when(waterTable?.waterType) {
            Const.type200 -> {
                waterTypeImage.setImageResource(R.drawable.water_type_01)
            }
            Const.type300 -> {
                waterTypeImage.setImageResource(R.drawable.water_type_03)
            }
            Const.type500 -> {
                waterTypeImage.setImageResource(R.drawable.water_type_02)
            }
        }
    }

    private fun setAdmob() {

        adView.visibility = View.INVISIBLE
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        //adStatus = "Request - noCallback"
        adView.adListener = object: AdListener(){
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                DLog.e("not loaded  : $p0")
                //adStatus = "Request - failedToLoad"
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                DLog.e("loaded loaded loaded loaded")
                //adStatus = "Request - load"
                adView.visibility = View.VISIBLE
            }
        }
    }

    private fun setAddButton() {
        addWaterButton.clicks()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val addEffect = LottieAnimationView(this@LockScreenActivity)
                    addWaterEffectFrame.addView(addEffect)
                    with(addEffect) {
                        setAnimation("add_effect.json")
                        loop(false)
                        playAnimation()
                    }

                    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    if (audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE || audioManager.ringerMode == AudioManager.RINGER_MODE_SILENT) {
                        if (lockscreenTable?.hasVibrate!!) {
                            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            vibrator.vibrate(100)
                        }
                    } else if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                        if (lockscreenTable?.hasSound!!) {
                            val mp = MediaPlayer.create(this@LockScreenActivity, R.raw.dropwater)
                            mp.start()
                            mp.setOnCompletionListener {
                                it.release()
                            }
                        }
                    }
                }
                .subscribe {
                    updateProgressBar()
                }.apply { compositeDisposable.add(this) }
    }

    private fun updateProgressBar() {
        val realm = Realm.getDefaultInstance()
        val goals = realm.where(Goals::class.java).findFirst()
        val currentIdNum = realm.where(WaterHistory::class.java).max("id")
        val nextId = when (currentIdNum) {
            null -> { 1 }
            else -> { currentIdNum.toInt() + 1 }
        }

        goals?.apply {

            val addWater = WaterHistory()
            addWater.id = nextId
            addWater.waterType = waterType
            addWater.todayDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            addWater.todayMonth = Calendar.getInstance().get(Calendar.MONTH)
            addWater.todayYear = Calendar.getInstance().get(Calendar.YEAR)
            addWater.addWaterTime = System.currentTimeMillis()
            addWater.todayWaterGoal = goalWater

            realm.executeTransaction {
                todayWater = todayWater?.plus(waterType!!)
                todayDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                todayMonth = Calendar.getInstance().get(Calendar.MONTH)
                todayYear = Calendar.getInstance().get(Calendar.YEAR)

                realm.copyToRealm(addWater)

                waterTable = realm.where(Goals::class.java).findFirst()
            }
        }
        setProgressBar()
    }

    private fun setProgressBar() {
        waterTable?.let {
            waterTable = RealmHelper.instance.getTodayWaterGoal()
            waterTable?.let {
                val percent : Int = (100 * (it.todayWater!!.toDouble() / it.goalWater!!.toDouble())).toInt()
                percentLoop(0.0, percent.toDouble())
                waterProgressbar.setProgress(it.todayWater!!, it.goalWater!!)
            }
        }
    }

    private fun percentLoop(current : Double, percent: Double) {
        val mHandler = Handler()
        var k = current
        Thread(Runnable {
            if (percent != 0.toDouble()) {
                while (k < percent) {
                    k += 1.0
                    SystemClock.sleep(10L)
                    mHandler.post {
                        val percentTxt = Math.floor(k).toInt().toString() + "%"
                        waterPercent.text = percentTxt
                    }
                }
            } else {
                mHandler.post {
                    waterPercent.text = "0%"
                }

            }
            Const.waterPercent = Math.floor(k).toInt()
        }).start()
    }

    private fun setMenu() {
        lcMenuList.visibility = View.GONE

        menuButton.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    if (lcMenuList.visibility == View.GONE) {
                        lcMenuList.visibility = View.VISIBLE
                        setLottieAnimator(true)
                        getMenuList()
                    } else {
                        setLottieAnimator(false)
                        lcMenuList.visibility = View.GONE
                    }
                }.apply { compositeDisposable.add(this) }
    }

    private fun setBackground() {
        lockscreenTable?.run{
            DLog.e(background.toString())
            if (background?.isEmpty()!!) lockScreenView.setBackgroundResource(R.drawable.sample)
            else {
                if (hasDrawable!!) {
                    val backgroundImage =  Background(background!!).getImageResourceId(applicationContext)
                    lockScreenView.setBackgroundResource(backgroundImage)
                } else {
                    val f = File(getRealPathFromURI(Uri.parse(background)))
                    val d = Drawable.createFromPath(f.absolutePath)
                    lockScreenView.background = d
                }

            }
        } ?: kotlin.run {
            lockScreenView.setBackgroundResource(R.drawable.sample)
        }

    }

    private fun getMenuList() {

        val resId = R.anim.layout_animation_fall_down
        val animation = AnimationUtils.loadLayoutAnimation(applicationContext, resId)

        with(lcMenuList) {
            layoutAnimation = animation
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            val menuAdapter = LockScreenMenuAdapter()
            menuAdapter.setOnItemClickListener(onItemClickListener)
            adapter = menuAdapter
        }
    }

    private fun setTime() {

        val c = Calendar.getInstance()
        val cMonth = c.get(Calendar.MONTH)
        val month = cMonth + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val week = c.get(Calendar.DAY_OF_WEEK)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        if (minute < 10) {
            lockscreenTime.text = "$hour:0$minute"
        } else {
            lockscreenTime.text = "$hour:$minute"
        }

        lockscreenDate.text = "$month${getString(R.string.month)} $day${getString(R.string.day)} ${getWeek(week)}"
    }

    private fun getWeek(week: Int): String {
        var dayOfWeek = ""
        when(week) {
            2 -> { dayOfWeek = resources.getString(R.string.monday) }
            3 -> { dayOfWeek = resources.getString(R.string.tuesday) }
            4 -> { dayOfWeek = resources.getString(R.string.wednesday) }
            5 -> { dayOfWeek = resources.getString(R.string.thursday) }
            6 -> { dayOfWeek = resources.getString(R.string.friday) }
            7 -> { dayOfWeek = resources.getString(R.string.saturday) }
            1 -> { dayOfWeek = resources.getString(R.string.sunday) }
        }

        return dayOfWeek
    }


    private fun setLottieAnimator(bool : Boolean) {
        val animator = when(bool) {
            false -> ValueAnimator.ofFloat(0.5f, 1f)
            else -> ValueAnimator.ofFloat(0f, 0.5f)
        }

        animator.duration = 500
        animator.addUpdateListener { animation ->
            with(menuButton) {
                progress = animation.animatedValue as Float
            }
        }
        animator.start()
    }

    private fun setUnlock() {
        lockScreenView.x = 0f

        swipeUnLockButton.setOnUnlockListenerRight(object : UnLockSwipe.OnUnlockListener {
            override fun onUnlock() {
                finish()
            }
        })

        lockScreenView.setOnTouchListener(object : UnLock(this, lockScreenView) {
            override fun onFinish() {
                finish()
                super.onFinish()
            }

            override fun onTouched() {
                if (lcMenuList.visibility == View.VISIBLE) {
                    setLottieAnimator(false)
                    lcMenuList.visibility = View.GONE
                }
                super.onTouched()
            }

            override fun onMoved(x: Int) {
                if (x > 0) { waveView.setProgress(x) }
                super.onMoved(x)
            }
        })


    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
        unregisterReceiver(mTimeReceiver)
        DWApplication.lockScreenShow = false
    }

    override fun onBackPressed() {
        if (lcMenuList.visibility == View.VISIBLE) {
            setLottieAnimator(false)
            lcMenuList.visibility = View.GONE
        }
    }
}

