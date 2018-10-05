package hanmo.com.drinkingwaterassistant.util

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by hanmo on 2018. 9. 6..
 */
object SettingsFragmentEventsBus {

    private val fragmentEventSubject = PublishSubject.create<Int>()
    const val ACTION_FRAGMENT_CREATED = 1
    const val ACTION_FRAGMENT_DESTROYED = 2
    const val ACTION_FRAGMENT_START_ANIMATION_FINISHED = 3
    const val ACTION_FRAGMENT_END_ANIMATION_FINISHED = 4

    val fragmentEventObservable: Observable<Int>
        get() = fragmentEventSubject

    fun postFragmentAction(actionId: Int?) {
        fragmentEventSubject.onNext(actionId!!)
    }
}