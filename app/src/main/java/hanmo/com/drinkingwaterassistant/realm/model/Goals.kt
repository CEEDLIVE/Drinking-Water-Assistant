package hanmo.com.drinkingwaterassistant.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by hanmo on 2018. 5. 22..
 */
open class Goals : RealmObject() {

    @PrimaryKey
    open var id : Int = 0

    @Required
    open var goalWater : Int? = null

    @Required
    open var todayWater : Int? = null

    @Required
    open var todayDate : Long? = null

    @Required
    open var waterType : Int? = null

    @Required
    open var hasLockScreen : Boolean? = null

}