package hanmo.com.drinkingwaterassistant

import android.support.test.InstrumentationRegistry
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import io.realm.Realm
import io.realm.RealmConfiguration
import junit.framework.Assert
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
        val goals = Goals()
        goals.id = 1
        goals.goal = 100
        goals.hasLockScreen = true

        realm.executeTransaction {
            realm.copyToRealm(goals)
        }

        val testGoals = realm.where(Goals::class.java).findFirst()
        assertNotNull(testGoals)
        testGoals?.let {
            assertEquals(1, it.id)
            assertEquals(100, it.goal)
            assertEquals(true, it.hasLockScreen)
        }

    }

    @Test
    fun B_updateGoal() {
        val goals = realm.where(Goals::class.java).findFirst()
        assertNotNull(goals)
        goals?.let {
            assertEquals(1, it.id)
            assertEquals(100, it.goal)
            assertEquals(true, it.hasLockScreen)
        }
        realm.executeTransaction {
            goals?.goal = 1200
        }

        val testGoals = realm.where(Goals::class.java).findFirst()
        assertNotNull(testGoals)
        testGoals?.let {
            assertEquals(1, it.id)
            assertEquals(1200, it.goal)
            assertEquals(true, it.hasLockScreen)
        }
    }

    @Test
    fun C_updateHasLockScreen() {
        val goals = realm.where(Goals::class.java).findFirst()
        assertNotNull(goals)
        goals?.let {
            assertEquals(1, it.id)
            assertEquals(1200, it.goal)
            assertEquals(true, it.hasLockScreen)
        }
        realm.executeTransaction {
            goals?.hasLockScreen = false
        }

        val testGoals = realm.where(Goals::class.java).findFirst()
        assertNotNull(testGoals)
        testGoals?.let {
            assertEquals(1, it.id)
            assertEquals(1200, it.goal)
            assertEquals(false, it.hasLockScreen)
        }
    }


    @After
    fun Z_closeDB() {
        /*if (!realm.isClosed) {
            realm.close()
        }*/
    }
}