package ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.pomotodo.Preferences
import com.example.pomotodo.Preferences.getLongBreakDuration
import com.example.pomotodo.Preferences.getPomodoroDuration
import com.example.pomotodo.Preferences.getShortBreakDuration
import com.example.pomotodo.R

private val pomodoroSpinnerItems = arrayOf(15,20,25,30,35,40,45)
private val shortSpinnerItems = arrayOf(3,4,5)
private val longSpinnerItems = arrayOf(10,15,20,25)

class SettingsFragment : Fragment() {

    private lateinit var pomodoroSpinner: Spinner
    private lateinit var shortSpinner: Spinner
    private lateinit var longSpinner: Spinner
    private lateinit var animationCheckBox: CheckBox


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(context,
            R.layout.fragment_settings,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pomodoroSpinner = view.findViewById(R.id.pomodoro_break_durations)
        shortSpinner = view.findViewById(R.id.short_break_durations)
        longSpinner = view.findViewById(R.id.long_break_durations)
        animationCheckBox = view.findViewById(R.id.animation_check_box)

//
        val pomodoroAdapter: ArrayAdapter<Int> = ArrayAdapter(requireContext(),
            R.layout.spinner_item,
            pomodoroSpinnerItems
        )
        pomodoroSpinner.adapter = pomodoroAdapter

        val shortAdapter: ArrayAdapter<Int> = ArrayAdapter(requireContext(),
            R.layout.spinner_item,
            shortSpinnerItems
        )
        shortSpinner.adapter = shortAdapter

        val longAdapter: ArrayAdapter<Int> = ArrayAdapter(requireContext(),
            R.layout.spinner_item,
            longSpinnerItems
        )
        longSpinner.adapter = longAdapter
//

        //Animation Check Box

        animationCheckBox.isChecked =
            Preferences.getAnimationState(context)

        animationCheckBox.setOnClickListener {
            if (animationCheckBox.isChecked){
                Preferences.setAnimationState(
                    context,
                    true
                )
            }

            else{
                Preferences.setAnimationState(
                    context,
                    false
                )
            }
        }

        setSpinnerChoices()


        spinnerListener(pomodoroSpinner)
        spinnerListener(shortSpinner)
        spinnerListener(longSpinner)


    }

    private fun spinnerListener (spinner:Spinner) {


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(spinner) {
                   // pomodoroSpinner ->Utils.setTime(spinner.selectedItem.toString().toInt() * 60)
                    pomodoroSpinner -> Preferences.setPomodoroDuration(
                        context,
                        spinner.selectedItem.toString().toInt() * 60
                    )
                    shortSpinner -> Preferences.setShortBreakDuration(
                        context,
                        spinner.selectedItem.toString().toInt() * 60
                    )
                    longSpinner -> Preferences.setLongBreakDuration(
                        context,
                        spinner.selectedItem.toString().toInt() * 60
                    )
                }

            }
        }
    }

    private fun setSpinnerChoices () {
        pomodoroSpinner.setSelection(
            Preferences.getPomodoroDurationPosition(
                context,
                getPomodoroDuration(context)
            )
        )

        shortSpinner.setSelection(
            Preferences.getShortBreakDurationPosition(
                context,
                getShortBreakDuration(context)
            )
        )

        longSpinner.setSelection(
            Preferences.getLongBreakDurationPosition(
                context,
                getLongBreakDuration(context)
            )
        )

    }

}