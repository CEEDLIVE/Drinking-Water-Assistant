package hanmo.com.drinkingwaterassistant.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.anim.RevealAnimationUtil
import kotlinx.android.synthetic.main.fragment_settings.view.*
import hanmo.com.drinkingwaterassistant.anim.RevealAnimationUtil.AnimationFinishedListener
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.main.MainFragment
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.FragmentEventsBus
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
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RevealAnimationUtil.registerStartRevealAnimation(rootView, resources.getColor(R.color.mainColor), resources.getColor(R.color.whiteColor), object : AnimationFinishedListener {
                override fun onAnimationFinished() {
                    if (!MainFragment.possibleDeleteItem) {
                        FragmentEventsBus.instance.postFragmentAction(FragmentEventsBus.ACTION_FRAGMENT_START_ANIMATION_FINISHED)
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
    }

    private fun setLockscreenSwitch() {
        view?.run {
            Lockscreen.instance.init(activity)
            val goals = RealmHelper.instance.queryFirst(Goals::class.java)
            goals?.let {
                lockscreenSwitch.isChecked = it.hasLockScreen!!
                if (it.hasLockScreen!!) { Lockscreen.instance.active() }
                else { Lockscreen.instance.deactivate() }
            }

            //rxCompoundButton Switch
            //lockscreenSwitch.checkedChanges().skipInitialValue().subscribe {  }

            lockscreenSwitch.setOnCheckedChangeListener({ _, isChecked ->
                RealmHelper.instance.updateHasLockScreen(isChecked)
                if (isChecked) {
                    Lockscreen.instance.active()
                    Snackbar.make(this, getString(R.string.lockscrenOn), Snackbar.LENGTH_LONG).show()
                }
                else {
                    Lockscreen.instance.deactivate()
                    Snackbar.make(this, getString(R.string.lockscrenOff), Snackbar.LENGTH_LONG).show()
                }
            })
        }
    }


    private fun setCloseButton() {
        view?.settingsCloseButton?.run {
            clicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .doOnNext {
                        reverseAnimationSpeed()
                        settingsCloseButton.playAnimation()
                        settingsCloseButton.isEnabled = false
                    }
                    .subscribe {
                        FragmentEventsBus.instance.postFragmentAction(FragmentEventsBus.ACTION_FRAGMENT_DESTROYED)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            RevealAnimationUtil.registerExitRevealAnimation(view, resources.getColor(R.color.mainColor), resources.getColor(R.color.whiteColor), object : AnimationFinishedListener {
                                override fun onAnimationFinished() {
                                    DLog.e("animation Close")
                                    FragmentEventsBus.instance.postFragmentAction(FragmentEventsBus.ACTION_FRAGMENT_END_ANIMATION_FINISHED)
                                    activity?.run { supportFragmentManager.beginTransaction().remove(this@SettingsFragment).commitAllowingStateLoss() }
                                }
                            })
                        }
                    }.apply { compositeDisposable.add(this) }
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

}