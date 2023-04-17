package com.example.ps_news.views.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.ps_news.R
import com.moengage.inapp.MoEInAppHelper

class ThirdFragment : Fragment() {


    lateinit var btnJumpToHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
//        MoEInAppHelper.getInstance().setInAppContext(setOf("ThirdFrag"))
//        MoEInAppHelper.getInstance().showInApp(context!!)
    }

    override fun onPause() {
        super.onPause()
//        MoEInAppHelper.getInstance().resetInAppContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)

        btnJumpToHome = view.findViewById(R.id.btn_jump_to_home)

        btnJumpToHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment, HomeFragment())
                .commit()
        }

        return view
    }
}