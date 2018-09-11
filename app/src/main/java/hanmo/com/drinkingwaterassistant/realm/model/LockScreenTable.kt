package hanmo.com.drinkingwaterassistant.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by hanmo on 2018. 9. 10..
 */
open class LockScreenTable : RealmObject() {

    @PrimaryKey
    open var id : Int = 0

    @Required
    open var hasLockScreen : Boolean? = null

    @Required
    open var hasVibrate : Boolean? = null

    @Required
    open var hasSound : Boolean? = null

    @Required
    open var hasDrawable : Boolean? = null

    @Required
    open var background : String? = null

}