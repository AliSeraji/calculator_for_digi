package com.example.dgcalculator

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ResultViewModel(application:Application) : AndroidViewModel(application) {

    private lateinit var firstResultSet:MutableLiveData<ArrayList<ResultData>>
    private lateinit var recyclerViewData:MutableLiveData<ArrayList<MultiViewData>>


    init {
        viewModelScope.launch {
            setInitialValue()
        }
    }

    private suspend fun setInitialValue(){
        firstResultSet = MutableLiveData<ArrayList<ResultData>>()
        recyclerViewData=MutableLiveData<ArrayList<MultiViewData>>()
        var tempArray = ArrayList<MultiViewData>()
        var res = ArrayList<ResultData>()
        withContext(Dispatchers.Default){
            var tempList=ArrayList<Float>()
            for(i in(0 until 6)){
                tempList.add(0f)
            }
            res.add(ResultData(0,tempList))
            firstResultSet.postValue(res)
            tempArray.add(MultiViewData(R.layout.recyclerview_input_item,null))
            tempArray.add(MultiViewData(R.layout.recyclerview_result_item, res))
            recyclerViewData.postValue(tempArray)

        }

    }

    fun getRecyclerViewData():MutableLiveData<ArrayList<MultiViewData>>{
        return recyclerViewData
    }

    fun getFirstResultSet():MutableLiveData<ArrayList<ResultData>>{
        return firstResultSet
    }


    fun calculate(inputs:FloatArray){
        var g2=inputs[1]*(7f/100f)
        viewModelScope.launch {
            if (g2 <=5000f) {
                recyclerViewData.value!![1].content!![0].results[0] = 5000f
            }
            else if (g2 >= 50000f) {
                recyclerViewData.value!![1].content!![0].results[0] = 50000f
            }
            else{
                recyclerViewData.value!![1].content!![0].results[0] = g2
            }
            recyclerViewData.value!![1].content!![0].results[1] = inputs[1] * (inputs[2]/100f)
            recyclerViewData.value!![1].content!![0].results[2]=(recyclerViewData.value!![1].content!![0].results[0]+recyclerViewData.value!![1].content!![0].results[1])*(9f/100f)+recyclerViewData.value!![1].content!![0].results[0]+recyclerViewData.value!![1].content!![0].results[1]
            recyclerViewData.value!![1].content!![0].results[3]=inputs[1]-inputs[0]-recyclerViewData.value!![1].content!![0].results[2]
            recyclerViewData.value!![1].content!![0].results[4]= inputs[1]-recyclerViewData.value!![1].content!![0].results[2]
            recyclerViewData.value!![1].content!![0].results[5]= ((recyclerViewData.value!![1].content!![0].results[4]/inputs[0])*100f)-100f
        }

    }
}