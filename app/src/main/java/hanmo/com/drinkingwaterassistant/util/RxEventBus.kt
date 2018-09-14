package hanmo.com.drinkingwaterassistant.util

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by hanmo on 2018. 9. 14..
 */
class RxEventBus {

    private val mBusSubject: PublishSubject<Any> = PublishSubject.create()

    /**
     * Posts an object (usually an Event) to the bus
     */
    fun post(event: Any) {
        mBusSubject.onNext(event)
    }

    /**
     * Observable that will emmit everything posted to the event bus.
     */
    fun observable(): Observable<Any> {
        return mBusSubject
    }

    /**
     * Observable that only emits events of a specific class.
     * Use this if you only want to subscribe to one type of events.
     */
    fun <T> filteredObservable(eventClass: Class<T>): Observable<T> {
        return mBusSubject.ofType(eventClass)
    }
}