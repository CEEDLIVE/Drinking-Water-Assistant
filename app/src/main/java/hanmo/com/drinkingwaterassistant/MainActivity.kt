package hanmo.com.drinkingwaterassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSwitch()
    }

    private fun setSwitch() {
        Lockscreen.instance.init(this@MainActivity)

        lockscreenSwitch.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                Lockscreen.instance.active()
            } else {
                Lockscreen.instance.deactivate()
            }
        })
    }
}
