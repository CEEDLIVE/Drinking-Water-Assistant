package hanmo.com.drinkingwaterassistant.settings

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.anim.RevealAnimationUtil
import kotlinx.android.synthetic.main.fragment_settings.view.*
import hanmo.com.drinkingwaterassistant.anim.RevealAnimationUtil.AnimationFinishedListener
import hanmo.com.drinkingwaterassistant.main.MainFragment
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.FragmentEventsBus
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RevealAnimationUtil.registerStartRevealAnimation(rootView, resources.getColor(R.color.mainColor), resources.getColor(R.color.whiteColor), object : AnimationFinishedListener {
                override fun onAnimationFinished() {
                    DLog.e("animation start")
                    FragmentEventsBus.instance.postFragmentAction(FragmentEventsBus.ACTION_FRAGMENT_CREATED)

                }

            })
        }


        rootView.settingsCloseButton.setOnClickListener {
            rootView.settingsCloseButton.reverseAnimationSpeed()
            rootView.settingsCloseButton.playAnimation()
            rootView.settingsCloseButton.isEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RevealAnimationUtil.registerExitRevealAnimation(view!!, resources.getColor(R.color.mainColor), resources.getColor(R.color.whiteColor), object : AnimationFinishedListener {
                    override fun onAnimationFinished() {
                        DLog.e("animation Close")
                        FragmentEventsBus.instance.postFragmentAction(FragmentEventsBus.ACTION_FRAGMENT_DESTROYED)
                        activity?.run {
                            supportFragmentManager.beginTransaction().remove(this@SettingsFragment).commitAllowingStateLoss()
                        }
                    }

                })
            }
        }

        return rootView
    }


}