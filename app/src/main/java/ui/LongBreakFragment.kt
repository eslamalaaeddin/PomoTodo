package ui

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pomotodo.ForegroundService

import com.example.pomotodo.R
import com.example.pomotodo.Preferences

import com.example.pomotodo.Utils


private const val NUMBER_ID = "numberId"
private const val RUNNING_ID = "runningId"
private const val BOUND = "bound"
private const val TAG = "PomodoroFragment"

class LongBreakFragment : Fragment() {


    private lateinit var button: ToggleButton
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    private lateinit var animatorSet: AnimatorSet
    private lateinit var toolbar: Toolbar

    val handler = Handler()
    var number = Utils.NUMBER
    var uiNumber = Utils.NUMBER
    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        retainInstance = true

    }

    override fun onStart() {
        super.onStart()
        activity?.intent?.let {
            onNewIntent(it)
            if (Utils.alerted){

            }
        }
        //انتهت؟
        if (Utils.serviceEnd){
            if (!Utils.alerted){
                alert()
                Utils.alerted = true
            }
            //alert()
            uiNumber =
                Preferences.getLongBreakDuration(
                    context
                )
            button.isChecked = false
            textView.visibility = View.VISIBLE
            textView.text = Utils.format(
                Preferences.getLongBreakDuration(
                    context
                )
            )
            stopAnimation()
            handler.removeCallbacks(runnable)
        }
        //لم تنته
        else{

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ==> ISLAM")

        return View.inflate(context,
            R.layout.fragment_break_long, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view.findViewById(R.id.toggleButton)
        textView = view.findViewById(R.id.text_view)
        imageView = view.findViewById(R.id.progressBar)
        toolbar = view.findViewById(R.id.toolbar)
        animatorSet = AnimatorInflater.loadAnimator(context,
            R.animator.loading_animator
        ) as AnimatorSet
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        //Customizing toolbar
        customizeToolbar(toolbar)

        if (Preferences.getAnimationState(context)){
            imageView.visibility = View.VISIBLE
        }

        if (Utils.getTime() == 0) {
            Utils.NUMBER =
                Preferences.getLongBreakDuration(
                    context
                )
            uiNumber =
                Preferences.getLongBreakDuration(
                    context
                )
        } else {
            uiNumber =
                Preferences.getLongBreakDuration(
                    context
                )
        }


        if (isServiceRunning()) {
            if (!Utils.STATE || Utils.PRESS_STATE) {
                startAnimation()
            }
            button.isChecked = true
            uiNumber = convert()
            runTimer()
        } else {
            textView.text = Utils.format(
                Preferences.getLongBreakDuration(
                    context
                )
            )
        }

        button.setOnClickListener {
            val startIntent = Intent(context, ForegroundService::class.java)
            if (button.isChecked) {
                Utils.serviceEnd = false
                number =
                    Preferences.getLongBreakDuration(
                        context
                    )
                Utils.longBreakNotification = true
                startIntent.putExtra(NUMBER_ID, number)
                running = true
                startIntent.putExtra(RUNNING_ID, running)
                startIntent.putExtra(BOUND, true)
                runTimer()
                ContextCompat.startForegroundService(requireContext(), startIntent)
                startAnimation()

            } else {
                //showSystemUI()
                number =
                    Preferences.getLongBreakDuration(
                        context
                    )
                uiNumber =
                    Preferences.getLongBreakDuration(
                        context
                    )
                running = false
                textView.visibility = View.VISIBLE
                textView.text = Utils.format(
                    Preferences.getLongBreakDuration(
                        context
                    )
                )
                startIntent.putExtra(RUNNING_ID, running)
                ContextCompat.startForegroundService(requireContext(), startIntent)
                handler.removeCallbacks(runnable)
                activity?.stopService(startIntent)
                stopAnimation()

            }

        }
    }

    private fun convert(): Int {
        uiNumber = Utils.minutes * 60 + Utils.seconds
        return uiNumber
    }

    private val runnable = object : Runnable {

        override fun run() {

            textView.text =
                Utils.format(Utils.lbFinalNumber)
            handler.postDelayed(this, 0)
            if (Utils.lbFinalNumber == 1) {
                Utils.longBreakEnd = true
                alert()
                Utils.alerted = true
                uiNumber =
                    Preferences.getLongBreakDuration(
                        context
                    )
                button.isChecked = false
                textView.visibility = View.VISIBLE
                textView.text = Utils.format(
                    Preferences.getLongBreakDuration(
                        context
                    )
                )
                stopAnimation()
                handler.removeCallbacks(this)
            }
        }
    }

    private fun runTimer() {
        handler.post(runnable)
    }

    private fun isServiceRunning(): Boolean {
        val manager =
            activity?.getSystemService(ACTIVITY_SERVICE) as ActivityManager

        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if ("com.example.simplepomodoro.ForegroundService" == service.service.className) {
                return true
            }
        }
        return false
    }

    fun alert() {
        findNavController().navigate(R.id.action_longBreakFragment_to_pomodoroFragment)
        findNavController().popBackStack(R.id.longBreakFragment,true)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

    private fun startAnimation() {
        if (Preferences.getAnimationState(context)){
            animatorSet.setTarget(imageView)
            imageView.setColorFilter(resources.getColor(R.color.orange), PorterDuff.Mode.SRC_IN)
            animatorSet.start()
            imageView.visibility = View.VISIBLE
        }
        else{
            imageView.visibility = View.INVISIBLE
        }

    }

    private fun stopAnimation() {
        if (Preferences.getAnimationState(context)){
            animatorSet.cancel()
            imageView.setColorFilter(resources.getColor(R.color.red), PorterDuff.Mode.SRC_IN)
            imageView.visibility = View.VISIBLE

        }
        else{
            imageView.visibility = View.INVISIBLE
        }

    }

    private fun onNewIntent(intent: Intent) {
        val extras: Bundle? = intent.extras
        if (extras != null) {
            if (extras.containsKey("NotificationMessage")) {
            }
        }

    }

    private fun customizeToolbar(toolbar: Toolbar){
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayShowTitleEnabled(false);
        toolbar.overflowIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        toolbar.title = ""
        toolbar.subtitle = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.longBreakEnd = true
    }

}
