package ss.anoop.awesomeloaders.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_launcher.*
import ss.anoop.awesomeloaders.PinWheelLoader
import ss.anoop.awesomeloaders.TwoArcLoader

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        awesomeLoadersList.adapter = AwesomeLoaderListAdapter(getLoaderList())
    }

    private fun getLoaderList(): List<AwesomeLoaderData> {
        return listOf(
            AwesomeLoaderData(TwoArcLoader(this), "Two Arc Loader"),
            AwesomeLoaderData(PinWheelLoader(this), "Pin Wheel Loader")
        )
    }
}
