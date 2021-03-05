package com.adrian.chwsdblib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

/**
 * A simple [Fragment] subclass.
 * Use the [OperatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OperatingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private fun String.operatingLog() = "${System.currentTimeMillis()}:$this"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_operating, container, false).also {
            ViewModelProvider(requireActivity()).get(LogViewModel::class.java).also { vm ->
                it.findViewById<AppCompatButton>(R.id.btnInsert)
                    .setOnClickListener { vm.logValue.postValue("Insert Data".operatingLog()) }
                it.findViewById<AppCompatButton>(R.id.btnDelete)
                    .setOnClickListener { vm.logValue.postValue("Delete Data".operatingLog()) }
                it.findViewById<AppCompatButton>(R.id.btnUpdate)
                    .setOnClickListener { vm.logValue.postValue("Update Data".operatingLog()) }
                it.findViewById<AppCompatButton>(R.id.btnQuery)
                    .setOnClickListener { vm.logValue.postValue("Query Data".operatingLog()) }
                it.findViewById<AppCompatButton>(R.id.btnCleanLog)
                    .setOnClickListener { vm.logValue.postValue("Clear") }
            }
        }
    }
}