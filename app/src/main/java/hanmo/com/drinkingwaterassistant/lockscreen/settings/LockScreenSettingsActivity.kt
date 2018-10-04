package hanmo.com.drinkingwaterassistant.lockscreen.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.LockScreenTable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_lockscreen_settings.*
import java.util.concurrent.TimeUnit

/**
 * 잠금화면 환경설정
 * Created by hanmo on 2018. 9. 16..
 */
class LockScreenSettingsActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    companion object {

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, LockScreenSettingsActivity::class.java)
            return intent
        }
    }

    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        super.onAttachedToWindow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lockscreen_settings)

        setCancelButton()
        initSwitch()
        setVibrateSwitch()
        setSoundsSwitch()
    }

    private fun initSwitch() {
        val lockScreenSet = RealmHelper.instance.queryFirst(LockScreenTable::class.java)
        lockScreenSet?.run {
            lockscreenSoundsSwitch.isChecked = hasSound!!
            lockscreenVibrateSwitch.isChecked = hasVibrate!!
        }
    }

    private fun setSoundsSwitch() {
        lockscreenSoundsSwitch.setOnCheckedChangeListener({ _, isChecked ->
            RealmHelper.instance.updateHasSounds(isChecked)
            if (isChecked) {
                Snackbar.make(lockscreenSoundsSwitch, getString(R.string.lockscrenSoundsOn), Snackbar.LENGTH_SHORT).show()
            }
            else {
                Snackbar.make(lockscreenSoundsSwitch, getString(R.string.lockscrenSoundsOff), Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun setVibrateSwitch() {
        lockscreenVibrateSwitch.setOnCheckedChangeListener({ _, isChecked ->
            RealmHelper.instance.updateHasVibrate(isChecked)
            if (isChecked) {
                Snackbar.make(lockscreenSoundsSwitch, getString(R.string.lockscrenVibrateOn), Snackbar.LENGTH_SHORT).show()
            }
            else {
                Snackbar.make(lockscreenSoundsSwitch, getString(R.string.lockscrenVibrateOff), Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun setCancelButton() {
        cancelButton.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    finish()
                }.apply { compositeDisposable.add(this) }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.slide_out_right)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

}