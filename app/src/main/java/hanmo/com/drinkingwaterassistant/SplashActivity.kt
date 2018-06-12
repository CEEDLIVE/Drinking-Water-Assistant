package hanmo.com.drinkingwaterassistant

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.splashscreen.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent



/**
 * Created by hanmo on 2018. 5. 28..
 */
class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH : Long = 1500

    /** 처음 액티비티가 생성될때 불려진다.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        Handler().postDelayed({
            /* 메뉴액티비티를 실행하고 로딩화면을 죽인다.*/
            val mainIntent = android.content.Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH)

    }

}