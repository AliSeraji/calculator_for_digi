package com.example.dgcalculator

import android.view.ViewGroup

class InputViewHolderFactory(val listener: DgRecyclerAdopter.OnRecyclerItemClickListener) : MultiViewHolderFactory {
    override fun createInstance(
        parent: ViewGroup,
        viewType: Int
    ): DgRecyclerAdopter.MultiViewViewHolder {
        return DgRecyclerAdopter.InputHolder.from(parent,listener)
    }
}