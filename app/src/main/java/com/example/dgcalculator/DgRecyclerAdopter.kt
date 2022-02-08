package com.example.dgcalculator

import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.os.Handler
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dgcalculator.databinding.RecyclerviewInputItemBinding
import com.example.dgcalculator.databinding.RecyclerviewResultItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private val ITEM_INPUT = 1
private val ITEM_RESULT = 2

class DgRecyclerAdopter(private val context: Context,
                        private val viewHolderProvider: MultiViewViewHolderProvider)
    : ListAdapter<DgRecyclerAdopter.DataItem, DgRecyclerAdopter.MultiViewViewHolder>(AdopterDataDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val mContext = context

    fun addViewSubmitList(res: List<MultiViewData>){
        adapterScope.launch {
            val items=when(res){
                null-> listOf(null)
                else -> res.map{DataItem.MultiViewDataItem(it)}
            }

            withContext(Dispatchers.Main){
                submitList(items)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return getItem(position).id!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewViewHolder {
        return viewHolderProvider.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: MultiViewViewHolder, position: Int) {
        holder.onBindVewHolder(mContext,position,getItem(position) as DataItem.MultiViewDataItem)
    }


    abstract class MultiViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        open fun onBindVewHolder(context: Context,position:Int, multiViewItem: DataItem.MultiViewDataItem) {

        }

        open fun onViewDetached(position: Int, multiViewItem: MultiViewData) {

        }



    }

    class ResultHolder(private val binding: RecyclerviewResultItemBinding) :MultiViewViewHolder(binding.root){
        override fun onBindVewHolder(context: Context,position: Int, multiViewItem: DataItem.MultiViewDataItem) {
            super.onBindVewHolder(context,position, multiViewItem)
            this.bind(multiViewItem)
        }

        private fun bind(item: DataItem.MultiViewDataItem){
            val resItem =item.content
            binding.processingAndSendingRes.text = resItem!![0].results[0].toInt().toString()
            binding.commissionRes.text=resItem[0].results[1].roundToLong().toString()
            binding.finalDeductionsRes.text=resItem[0].results[2].roundToLong().toString()
            binding.netProfitRes.text = resItem[0].results[3].roundToLong().toString()
            binding.netReceivedRes.text= resItem[0].results[4].roundToLong().toString()
            binding.interestRatesRes.text=resItem[0].results[5].roundToLong().toString()
        }


        companion object{
            fun from(parent: ViewGroup):ResultHolder{
                val layoutInflater= LayoutInflater.from(parent.context)
                val binding=RecyclerviewResultItemBinding.inflate(layoutInflater,parent,false)
                return ResultHolder(binding)
            }
        }
    }

    class InputHolder(
        private val binding: RecyclerviewInputItemBinding,
        private val listener: OnRecyclerItemClickListener
    ):MultiViewViewHolder(binding.root) {
        override fun onBindVewHolder(context: Context,position: Int, multiViewItem: DataItem.MultiViewDataItem) {
            super.onBindVewHolder(context,position, multiViewItem)
            binding.calculate.setOnClickListener {

                var arrayOfInput= FloatArray(3)
                binding.calculate.startLoading()
                binding.calculate.isEnabled=false
                binding.calculate.resetAfterFailed = true

                if(binding.buyPriceInput.text.toString().isNotEmpty()
                    and binding.sellPriceInput.text.toString().isNotEmpty()
                    and binding.percentageOfCommissionLayoutInput.text.toString().isNotEmpty()){
                    arrayOfInput[0] = binding.buyPriceInput.text.toString().toFloat()
                    arrayOfInput[1] = binding.sellPriceInput.text.toString().toFloat()
                    arrayOfInput[2] = binding.percentageOfCommissionLayoutInput.text.toString().toFloat()
                    listener.onCalculateCLick(arrayOfInput)
                    Toast.makeText(context,R.string.toast_success,Toast.LENGTH_LONG).show()
                    binding.calculate.loadingSuccessful()

                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.calculate.isEnabled=true
                        binding.calculate.reset()
                    }, 1000)

                }
                else{
                    binding.calculate.loadingFailed()
                    Toast.makeText(context,R.string.toast_failure,Toast.LENGTH_LONG).show()
                }
                binding.calculate.isEnabled=true
                binding.calculate.reset()
            }
        }

        companion object{
            fun from(parent: ViewGroup,listener: OnRecyclerItemClickListener):InputHolder{
                val layoutInflater=LayoutInflater.from(parent.context)
                val binding=RecyclerviewInputItemBinding.inflate(layoutInflater,parent,false)
                return InputHolder(binding,listener)
            }
        }
    }

    interface OnRecyclerItemClickListener {
        fun onCalculateCLick(inputs:FloatArray)
    }

    sealed class DataItem {
        abstract val id:Int?
        abstract val content:ArrayList<ResultData>?
        data class MultiViewDataItem(val item:MultiViewData):DataItem(){
            override val id = item.id
            override val content = item.content
        }
    }

    class AdopterDataDiffCallback:DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }
}