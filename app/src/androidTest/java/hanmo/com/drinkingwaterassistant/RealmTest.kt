package hanmo.com.drinkingwaterassistant

import android.support.test.InstrumentationRegistry
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import io.realm.Realm
import io.realm.RealmConfiguration
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

/**
 * Created by hanmo on 2018. 5. 22..
 */
@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RealmTest {

    private lateinit var realm: Realm

    @Before
    fun initDB() {
        Realm.init(InstrumentationRegistry.getTargetContext())

        val realmConfiguration = RealmConfiguration.Builder()
                .name("test.realm")
                .inMemory()
                .build()

        Realm.setDefaultConfiguration(realmConfiguration)

        realm = Realm.getDefaultInstance()
    }

    @Test
    fun A_initUserPreference() {
        val currentIdNum = realm.where(Goals::class.java).max("id")
        val nextId = when (currentIdNum) {
            null -> {
                1
            }
            else -> {
                currentIdNum.toInt() + 1
            }
        }

        val goals = Goals()
        with(goals) {
            id = 1
            goalWater = 100
            todayWater = 0
            waterType = Const.type200
        }


        realm.executeTransaction {
            realm.copyToRealm(goals)
        }

        val testGoals = realm.where(Goals::class.java).findFirst()
        assertNotNull(testGoals)
        testGoals?.run {
            assertEquals(1, id)
            assertEquals(0, todayWater)
            assertEquals(100, goalWater)
            assertEquals(Const.type200, waterType)
        }

    }

    @Test
    fun B_updateGoal() {
        val goals = realm.where(Goals::class.java).findFirst()
        assertNotNull(goals)
        goals?.let {
            assertEquals(1, it.id)
            assertEquals(100, it.goalWater)
        }
        realm.executeTransaction {
            goals?.goalWater = 1200
        }

        val testGoals = realm.where(Goals::class.java).findFirst()
        assertNotNull(testGoals)
        testGoals?.let {
            assertEquals(1, it.id)
            assertEquals(1200, it.goalWater)
        }
    }

    @Test
    fun C_updateHasLockScreen() {
        val goals = realm.where(Goals::class.java).findFirst()
        assertNotNull(goals)
        goals?.let {
            assertEquals(1, it.id)
            assertEquals(1200, it.goalWater)
        }

        val testGoals = realm.where(Goals::class.java).findFirst()
        assertNotNull(testGoals)
        testGoals?.let {
            assertEquals(1, it.id)
            assertEquals(1200, it.goalWater)
        }
    }

    @Test
    fun D_delete() {
        val goals = realm.where(Goals::class.java).findFirst()
        realm.executeTransaction {
            goals?.deleteFromRealm()
        }
    }

    @After
    fun Z_closeDB() {
        /*if (!realm.isClosed) {
            realm.close()
        }*/
    }
}