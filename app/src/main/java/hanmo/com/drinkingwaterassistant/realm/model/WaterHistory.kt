package hanmo.com.drinkingwaterassistant.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by hanmo on 2018. 7. 3..
 */
open class WaterHistory : RealmObject() {

    @PrimaryKey
    open var id : Int = 0

    @Required
    open var todayDate : Int? = null

    @Required
    open var todayMonth : Int? = null

    @Required
    open var todayYear : Int? = null

    @Required
    open var waterType : Int? = null

    @Required
    open var addWaterTime : Long? = null

    @Required
    open var todayWaterGoal : Int? = null

}