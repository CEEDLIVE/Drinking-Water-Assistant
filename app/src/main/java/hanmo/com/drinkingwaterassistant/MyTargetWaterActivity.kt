package hanmo.com.drinkingwaterassistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_mytarget.*
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

/**
 * Created by hanmo on 2018. 6. 12..
 */
class MyTargetWaterActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var status = false

    companion object {
        fun newIntent(context: Context?) : Intent {
            val intent = Intent(context, MyTargetWaterActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mytarget)

        compositeDisposable = CompositeDisposable()

        setMyTarget()
        setButton()
    }

    private fun setMyTarget() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            if (it.goal != 0){
                status = true
                myTargetText.setText(it.goal.toString())
            }
        }
    }

    private fun setButton() {
        myTargetConfirmButton.clicks()
                .debounce(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .doOnNext {
                    Snackbar.make(myTargetConfirmButton, "입력 완료!!", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
                .subscribe {
                    if (status) {
                        RealmHelper.instance.updateGoal(myTargetText.text.toString().toInt())
                        myTargetText.text.clear()
                        finish()
                    } else {
                        toast("섭취량을 입력해 주세요")
                    }
                }
                .apply { compositeDisposable.add(this) }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}