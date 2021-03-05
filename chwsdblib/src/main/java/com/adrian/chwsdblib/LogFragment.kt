package com.adrian.chwsdblib

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
/**
 * A simple [Fragment] subclass.
 * Use the [LogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false).also { root ->
            root.findViewById<AppCompatTextView>(R.id.tvOperatingLog).also { tv ->
                tv.movementMethod = ScrollingMovementMethod.getInstance()
                ViewModelProvider(requireActivity())[LogViewModel::class.java].logValue.observe(this,
                    {
                        if (it == "Clear") {
                            tv.text = ""
                        } else {
                            tv.append("${it}\n")
                        }
                    })
            }
        }
    }
}