package hanmo.com.drinkingwaterassistant.realm

import android.util.Log
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.realm.model.WaterHistory
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmObject
import io.realm.RealmResults
import java.util.*

/**
 * Created by hanmo on 2018. 5. 22..
 */
class RealmHelper {

    var realm: Realm
        private set

    init {
        realm = try {

            Realm.getDefaultInstance()

        } catch (e: Exception) {

            Log.d("Realm Exception", e.toString())

            val config = RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build()
            Realm.getInstance(config)
        }
    }

    fun initDB() {
        val goals = Goals()
        goals.id = 1
        goals.goalWater = 0
        goals.todayWater = 0
        goals.waterType = Const.type200
        goals.todayDate = System.currentTimeMillis()
        goals.hasLockScreen = true
        addData(goals)
    }

    fun updateGoal(goal : Int?) {
        val goals = queryFirst(Goals::class.java)
        goals?.let {
            realm.executeTransaction {
                goals.goalWater = goal
            }
        }
    }

    fun updateHasLockScreen(hasLockScreen: Boolean?) {
        val goals = queryFirst(Goals::class.java)
        goals?.let {
            realm.executeTransaction {
                goals.hasLockScreen = hasLockScreen
            }
        }
    }

    fun updateTodayWater(todayWater: Int?) {
        val goals = queryFirst(Goals::class.java)
        goals?.let {
            realm.executeTransactionAsync {
                goals.todayWater = todayWater
            }
        }
    }

    /*fun addWaterButtonClick() {

        val currentIdNum = realm.where(WaterHistory::class.java).max("id")
        val nextId = when (currentIdNum) {
            null -> {
                1
            }
            else -> {
                currentIdNum.toInt() + 1
            }
        }

        val addWater = WaterHistory()
        addWater.id = nextId
        addWater.waterType = queryFirst(Goals::class.java)
    }*/

    //Insert To Realm
    fun <T : RealmObject> addData(data: T) {
        realm.executeTransaction {
            realm.copyToRealm(data)
        }
    }

    //Insert To Realm with RealmList
    fun <T : RealmObject> addRealmListData(data: T) {
        realm.executeTransaction {
            realm.copyToRealmOrUpdate(data)
        }
    }

    fun <T : RealmObject> queryAll(clazz: Class<T>): RealmResults<T>? {
        return realm.where(clazz).findAll()
    }

    fun <T : RealmObject> queryFirst(clazz: Class<T>): T? {
        return realm.where(clazz).findFirst()
    }

    companion object {

        private var INSTANCE: RealmHelper? = RealmHelper()

        val instance: RealmHelper
            get() {
                if (INSTANCE == null) {
                    INSTANCE = RealmHelper()
                }
                return INSTANCE as RealmHelper
            }
    }

}