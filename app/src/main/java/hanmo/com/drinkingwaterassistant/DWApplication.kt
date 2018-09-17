package hanmo.com.drinkingwaterassistant

import android.content.Context
import android.support.multidex.MultiDexApplication
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.workers.DailyWorkerUtil
import io.realm.Realm
import io.realm.RealmConfiguration
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric


/**
 * Created by hanmo on 2018. 5. 22..
 */
class DWApplication : MultiDexApplication() {

    init {
        instance = this@DWApplication
        firebaseAnalytics = FirebaseAnalytics.getInstance(this@DWApplication)
    }

    private val sAnalytics = GoogleAnalytics.getInstance(this@DWApplication)
    private var sTracker: Tracker? = null

    companion object {
        var lockScreenShow = false
        val notificationId: Int = 1
        var firebaseAnalytics : FirebaseAnalytics? = null
        private var instance: DWApplication? = null

        fun applicationContext() : Context? {
            return instance?.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        DLog.e("apllication midnight worker start!!")
        DailyWorkerUtil.applyMidnightWorker()
        initDB()
        MobileAds.initialize(this@DWApplication, "ca-app-pub-2452228545701512~5634059465")
    }

    @Synchronized
    internal fun getTracker(): Tracker? {
        sTracker?.run {
            DLog.e("sTracker is not null")
        } ?: kotlin.run {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker)
        }

        return sTracker
    }



    private fun initDB() {
        Realm.init(this)

        val realmConfiguration = RealmConfiguration.Builder()
                .name("drink_water.realm")
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(realmConfiguration)


        val user = RealmHelper.instance.queryFirst(Goals::class.java)

        user?.let {
            DLog.e("기존 사용자")
        } ?: kotlin.run {
            RealmHelper.instance.initDB()
        }
    }

    private fun clearApplicationCache(dir: java.io.File?) {
        var dir = dir
        if (dir == null)
            dir = cacheDir
        if (dir == null)
            return

        val children = dir.listFiles()
        try {
            for (i in children.indices) {
                if (children[i].isDirectory)
                    clearApplicationCache(children[i])
                else
                    children[i].delete()
            }
        } catch (e: Exception) {
            DLog.e(e.toString())
        }
    }

    @Synchronized
    fun getDefaultTracker(): Tracker {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker)
        }

        return sTracker
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
        clearApplicationCache(null)
        if (!Realm.getDefaultInstance().isClosed) {
            Realm.getDefaultInstance().close()
        }
    }

}