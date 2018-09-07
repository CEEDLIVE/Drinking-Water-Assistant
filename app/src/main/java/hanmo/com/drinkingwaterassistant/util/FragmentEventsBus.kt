package hanmo.com.drinkingwaterassistant.util

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by hanmo on 2018. 9. 6..
 */
class FragmentEventsBus private constructor() {

    private val fragmentEventSubject = PublishSubject.create<Int>()

    val fragmentEventObservable: Observable<Int>
        get() = fragmentEventSubject

    fun postFragmentAction(actionId: Int?) {
        fragmentEventSubject.onNext(actionId!!)
    }

    companion object {
        private val TAG = FragmentEventsBus::class.java.simpleName
        private val TAG2 = FragmentEventsBus::class.java.canonicalName

        val ACTION_FRAGMENT_CREATED = 1
        val ACTION_FRAGMENT_DESTROYED = 2

        private var mInstance: FragmentEventsBus? = null

        val instance: FragmentEventsBus
            get() {
                if (mInstance == null) {
                    mInstance = FragmentEventsBus()
                }
                return mInstance as FragmentEventsBus
            }
    }
}