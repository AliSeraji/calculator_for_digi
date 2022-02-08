package com.example.dgcalculator

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dgcalculator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerview:DgRecyclerAdopter
    private lateinit var viewModel:ResultViewModel
    private lateinit var baseViewHolderProvider:MultiViewViewHolderProvider


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forceRTL()
        setApplicationLocale("fa")
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        init()
    }

    private fun init(){

        viewModel =ViewModelProvider(this).get(ResultViewModel::class.java)
        baseViewHolderProvider=MultiViewViewHolderProvider()

        baseViewHolderProvider.registerViewHolderFactory(R.layout.recyclerview_input_item,InputViewHolderFactory(object:DgRecyclerAdopter.OnRecyclerItemClickListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onCalculateCLick(inputs:FloatArray) {
                viewModel.getRecyclerViewData().observe(this@MainActivity,{
                    this@MainActivity.lifecycleScope.launch{
                        viewModel.calculate(inputs)
                        recyclerview.notifyDataSetChanged()
                    }
                })
            }
        }))
        baseViewHolderProvider.registerViewHolderFactory(R.layout.recyclerview_result_item,ResultViewHolderFactory())


        recyclerview= DgRecyclerAdopter(this,baseViewHolderProvider)
        binding.recyclerView.adapter=recyclerview

        viewModel.getRecyclerViewData().observe(this,{
            it.let {
                recyclerview.addViewSubmitList(it)
            }
        })
    }



    private fun forceRTL(){
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }


    private fun setApplicationLocale(locale: String) {
        val resources = resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(locale.toLowerCase()))
        } else {
            config.locale = Locale(locale.toLowerCase())
        }
        resources.updateConfiguration(config, dm)
    }

}