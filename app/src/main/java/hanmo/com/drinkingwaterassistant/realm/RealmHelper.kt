package hanmo.com.drinkingwaterassistant.realm

import android.util.Log
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.realm.model.WaterHistory
import hanmo.com.drinkingwaterassistant.util.DLog
import io.realm.*
import java.util.*

/**
 *
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
        goals.todayDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        goals.todayMonth = Calendar.getInstance().get(Calendar.MONTH)
        goals.todayYear = Calendar.getInstance().get(Calendar.YEAR)
        goals.hasLockScreen = true
        addData(goals)
    }

    fun updateGoal(goal : Int?) {
        val goals = queryFirst(Goals::class.java)
        goals?.apply {
            realm.executeTransaction {
                todayDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                todayMonth = Calendar.getInstance().get(Calendar.MONTH)
                todayYear = Calendar.getInstance().get(Calendar.YEAR)
                goalWater = goal
            }
        }
    }

    fun updateHasLockScreen(hasLockScreen: Boolean?) {
        val goals = queryFirst(Goals::class.java)
        goals?.apply {
            realm.executeTransaction {
                this.hasLockScreen = hasLockScreen
            }
        }
    }

    fun updateTodayWater(todayWater: Int?, cal: String) {
        val goals = queryFirst(Goals::class.java)
        goals?.run {
            realm.executeTransaction {
                if (cal.equals(Const.MINUS)){
                    this.todayWater = this.todayWater!! - todayWater!!
                } else {
                    this.todayWater = this.todayWater!! + todayWater!!
                }
            }
        }
    }

    fun getHasLockScreenBool() : Boolean {
        return realm.where(Goals::class.java).findFirst()?.hasLockScreen!!
    }

    fun getSortWaterHistory(sortValue : String) : RealmResults<WaterHistory>? {
        return realm.where(WaterHistory::class.java).distinct(sortValue).sort(sortValue, Sort.ASCENDING).findAll()
    }

    fun getTotalTodayWater(todayDate : Int?): RealmResults<WaterHistory>? {
        return realm.where(WaterHistory::class.java).equalTo("todayDate", todayDate).findAll()
    }

    fun getTodayWaterGoal() : Goals? {
        val todayWaterGoals = realm.where(Goals::class.java).equalTo("todayDate", Calendar.getInstance().get(Calendar.DAY_OF_YEAR)).findFirst()
        todayWaterGoals?.apply {
            DLog.e("오늘 마신 물이 없습니다!!")
        } ?: kotlin.run {
            realm.executeTransaction {
                todayWaterGoals?.todayWater = 0
            }
        }
        return realm.where(Goals::class.java).equalTo("todayDate", Calendar.getInstance().get(Calendar.DAY_OF_YEAR)).findFirst()
    }

    fun addWaterButtonClick() {

        val goals = queryFirst(Goals::class.java)
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
        addWater.waterType = goals?.waterType
        addWater.todayDate = goals?.todayDate
        addWater.todayMonth = goals?.todayMonth
        addWater.todayYear = goals?.todayYear
        addWater.addWaterTime = System.currentTimeMillis()

        addData(addWater)

    }

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

    fun todayWaterHistory(): RealmResults<WaterHistory>? {

        return realm.where(WaterHistory::class.java)
                .equalTo("todayYear", Calendar.getInstance().get(Calendar.YEAR))
                .and()
                .equalTo("todayMonth", Calendar.getInstance().get(Calendar.MONTH))
                .and()
                .equalTo("todayDate", Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
                .sort("addWaterTime", Sort.DESCENDING)
                .findAll()

    }

    fun deleteHistory(id: Int) {
        realm.executeTransaction {
            val results = realm.where(WaterHistory::class.java).equalTo("id", id).findFirst()
            results?.deleteFromRealm()

        }
        /*realm.executeTransactionAsync({ bgRealm ->
            val results = bgRealm.where(WaterHistory::class.java).equalTo("id", id).findFirst()
            results?.deleteFromRealm()
        }, {
            // 트랜잭션이 성공하였습니다.
        }, {
            // 트랜잭션이 실패했고 자동으로 취소되었습니다.
        })*/
    }
}