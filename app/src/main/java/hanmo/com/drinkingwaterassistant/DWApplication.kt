package hanmo.com.drinkingwaterassistant

import android.support.multidex.MultiDexApplication
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.workers.DailyWorkerUtil
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by hanmo on 2018. 5. 22..
 */
class DWApplication : MultiDexApplication() {

    companion object {
        var lockScreenShow = false
        val notificationId: Int = 1
    }

    override fun onCreate() {
        super.onCreate()
        DLog.e("apllication midnight worker start!!")
        DLog.e("apllication ${DailyWorkerUtil.getWorksState()}")
        DailyWorkerUtil.applyMidnightWorker()
        initDB()
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

    override fun onTerminate() {
        super.onTerminate()
        if (!Realm.getDefaultInstance().isClosed) {
            Realm.getDefaultInstance().close()
        }
    }

}