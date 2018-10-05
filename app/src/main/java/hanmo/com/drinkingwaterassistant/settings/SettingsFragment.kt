package hanmo.com.drinkingwaterassistant.settings

import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.anim.RevealAnimationUtil
import kotlinx.android.synthetic.main.fragment_settings.view.*
import hanmo.com.drinkingwaterassistant.anim.RevealAnimationUtil.AnimationFinishedListener
import hanmo.com.drinkingwaterassistant.lockscreen.util.LockScreen
import hanmo.com.drinkingwaterassistant.main.MainFragment
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.SettingsFragmentEventsBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit


/**
 * Created by hanmo on 2018. 9. 6..
 */
class SettingsFragment : Fragment() {

    companion object {

        fun newInstance(): SettingsFragment {
            val args = Bundle()
            //args.putSerializable(dataModel, dataModel as Serializable)
            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RevealAnimationUtil.registerStartRevealAnimation(rootView, ContextCompat.getColor(context!!, R.color.mainColor), ContextCompat.getColor(context!!, R.color.whiteColor), object : AnimationFinishedListener {
                override fun onAnimationFinished() {
                    if (!MainFragment.possibleDeleteItem) {
                        SettingsFragmentEventsBus.postFragmentAction(SettingsFragmentEventsBus.ACTION_FRAGMENT_START_ANIMATION_FINISHED)
                        setLockscreenSwitch()
                    }
                }
            })
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        setCloseButton()
        setBackButtonPressed()
    }

    private fun initLockScreenSwitch() {
        view?.run {
            val hasLockScreen = RealmHelper.instance.getHasLockScreenBool()
            lockscreenSwitch.isChecked = hasLockScreen
            if (hasLockScreen) {
                LockScreen.active()
            }
            else { LockScreen.deactivate() }
        }
    }

    private fun setBackButtonPressed() {
        view?.run {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    settingsCloseButton.performClick()
                    settingsCloseButton.isEnabled = false
                    return@setOnKeyListener true
                }
                false
            }
        }
    }

    private fun setLockscreenSwitch() {
        view?.run {
            //rxCompoundButton Switch
            //lockscreenSwitch.checkedChanges().skipInitialValue().subscribe {  }

            initLockScreenSwitch()

            lockscreenSwitch.setOnCheckedChangeListener { _, isChecked ->
                RealmHelper.instance.updateHasLockScreen(isChecked)
                if (isChecked) {
                    LockScreen.active()
                    Snackbar.make(this, getString(R.string.lockscrenOn), Snackbar.LENGTH_LONG).show()
                } else {
                    LockScreen.deactivate()
                    Snackbar.make(this, getString(R.string.lockscrenOff), Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun setCloseButton() {
        view?.settingsCloseButton?.run {
            clicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .filter { isEnabled }
                    .doOnNext {
                        reverseAnimationSpeed()
                        playAnimation()
                        isEnabled = false
                    }
                    .subscribe {
                        SettingsFragmentEventsBus.postFragmentAction(SettingsFragmentEventsBus.ACTION_FRAGMENT_DESTROYED)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            RevealAnimationUtil.registerExitRevealAnimation(view, ContextCompat.getColor(context!!, R.color.mainColor), ContextCompat.getColor(context!!, R.color.whiteColor), object : AnimationFinishedListener {
                                override fun onAnimationFinished() {
                                    DLog.e("fade reveal animation Finished")
                                    SettingsFragmentEventsBus.postFragmentAction(SettingsFragmentEventsBus.ACTION_FRAGMENT_END_ANIMATION_FINISHED)
                                    activity?.run { supportFragmentManager.beginTransaction().remove(this@SettingsFragment).commitAllowingStateLoss() }
                                }
                            })
                        } else {
                            activity?.run { supportFragmentManager.beginTransaction().remove(this@SettingsFragment).commitAllowingStateLoss() }
                        }
                    }.apply { compositeDisposable.add(this) }
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

}