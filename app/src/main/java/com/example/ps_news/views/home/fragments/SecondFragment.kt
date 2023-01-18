package com.example.ps_news.views.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.ps_news.R
import com.moengage.inapp.MoEInAppHelper

class SecondFragment : Fragment() {

    lateinit var btnJumpToThird: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        MoEInAppHelper.getInstance().setInAppContext(setOf("SecondFrag"))
        MoEInAppHelper.getInstance().showInApp(context!!)
    }

    override fun onPause() {
        MoEInAppHelper.getInstance().resetInAppContext()
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second2, container, false)

        btnJumpToThird = view.findViewById(R.id.btn_jump_to_third)

        btnJumpToThird.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment, ThirdFragment())
                .commit()
        }

        return view
    }
}