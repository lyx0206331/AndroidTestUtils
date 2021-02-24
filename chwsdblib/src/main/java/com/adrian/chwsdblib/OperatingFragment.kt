package com.adrian.chwsdblib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OperatingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OperatingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}