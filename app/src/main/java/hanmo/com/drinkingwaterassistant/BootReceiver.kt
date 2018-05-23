package hanmo.com.drinkingwaterassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog

/**
 * Created by hanmo on 2018. 5. 23..
 */
class BootReceiver : BroadcastReceiver() {
    // BroadcastReceiver를 상속하여 처리 해줍니다.
    override fun onReceive(context: Context, intent: Intent) {
        // TODO Auto-generated method stub
        // 전달 받은 Broadcast의 값을 가져오기
        // androidmanifest.xml에 정의한 인텐트 필터를 받아 올 수 있습니다.
        val action = intent.action
        // 전달된 값이 '부팅완료' 인 경우에만 동작 하도록 조건문을 설정 해줍니다.
        if (action == "android.intent.action.BOOT_COMPLETED") {
            // TODO
            // 부팅 이후 처리해야 코드 작성
            // Ex.서비스 호출, 특정 액티비티 호출등등
            DLog.e("LOCKSCREEN ONONONONONONONONON")
            val lockscreen = RealmHelper.instance.queryFirst(Goals::class.java)
            if (lockscreen?.hasLockScreen!!) {
                Lockscreen.instance.init(context)
                Lockscreen.instance.active()
            }

        }
    }
}