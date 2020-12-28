package atomsandbots.android.spotifyvideos.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import atomsandbots.android.spotifyvideos.R

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // delay thread by 3 secs
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, VideoActivity::class.java))
            finish()
        }, 3000)
    }
}
