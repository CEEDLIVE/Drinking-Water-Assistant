package hanmo.com.drinkingwaterassistant

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.tracking.GATracker
import hanmo.com.drinkingwaterassistant.tracking.MyTargetActivityTrackingUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mytarget.*
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

/**
 * Created by hanmo on 2018. 6. 12..
 */
class MyTargetWaterActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var status = false
    private var waterType : Int? = null

    companion object {
        fun newIntent(context: Context?) : Intent {
            return Intent(context, MyTargetWaterActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mytarget)

        compositeDisposable = CompositeDisposable()
        GATracker.setupAppview("MyTargetWaterActivity Created")

        setMyTarget()
        setConfirmButton()
        setMyTargetText()
        initWaterTypeCheck()
        setMyWaterType()

    }

    private fun initWaterTypeCheck() {
        val selectWaterType = waterType?.run {
            this
        } ?: kotlin.run {
            RealmHelper.instance.queryFirst(Goals::class.java)?.waterType
        }

        when(selectWaterType) {
            Const.type200 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    waterType200.transitionName = "typeImage"
                }
                waterType200checkmark.visibility = View.VISIBLE
                waterType300checkmark.visibility = View.INVISIBLE
                waterType500checkmark.visibility = View.INVISIBLE
            }
            Const.type300 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    waterType300.transitionName = "typeImage"
                }
                waterType200checkmark.visibility = View.INVISIBLE
                waterType300checkmark.visibility = View.VISIBLE
                waterType500checkmark.visibility = View.INVISIBLE
            }
            Const.type500 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    waterType500.transitionName = "typeImage"
                }
                waterType200checkmark.visibility = View.INVISIBLE
                waterType300checkmark.visibility = View.INVISIBLE
                waterType500checkmark.visibility = View.VISIBLE
            }
        }
    }

    private fun setMyWaterType() {

        waterType200Button.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    waterType = Const.type200
                    initWaterTypeCheck()
                }.apply { compositeDisposable.add(this) }

        waterType300Button.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    waterType = Const.type300
                    initWaterTypeCheck()
                }.apply { compositeDisposable.add(this) }

        waterType500Button.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    waterType = Const.type500
                    initWaterTypeCheck()
                }.apply { compositeDisposable.add(this) }

        waterTypeCustomButton.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .doOnNext { MyTargetActivityTrackingUtil.clickedCustomWaterTypeButton() }
                .subscribe {
                    Snackbar.make(waterTypeCustomButton, getString(R.string.prepareCustomWaterService), Snackbar.LENGTH_LONG).show()
                }.apply { compositeDisposable.add(this) }

    }

    private fun setMyTarget() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            if (it.goalWater != 0){
                status = true
                myTargetText.setText(it.goalWater.toString())
            }
        }
    }

    private fun setMyTargetText() {
        //Trim 제거해야할듯 (화이트스페이스)
        //00이면? 0으로 처리해야함
        //숫자이외의 것을 적으면 버튼 활성화 안되도록

        myTargetText.textChanges()
                .skipInitialValue()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    status = if (it.isEmpty()) {
                        false
                    } else {
                        val number : Int = it.toString().toInt()
                        200 < number
                    }
                }
                .apply { compositeDisposable.add(this) }
    }

    private fun setConfirmButton() {
        myTargetConfirmButton.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    if (status) {
                        RealmHelper.instance.updateGoal(myTargetText.text.toString().toInt())
                        waterType?.run { RealmHelper.instance.updateWaterType(this) }
                        onBackPressed()
                    } else {
                        toast(getString(R.string.inputGoals))
                    }
                }
                .apply { compositeDisposable.add(this) }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}