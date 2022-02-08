package com.example.dgcalculator

import android.view.ViewGroup

class ResultViewHolderFactory : MultiViewHolderFactory{
    override fun createInstance(
        parent: ViewGroup,
        viewType: Int
    ): DgRecyclerAdopter.MultiViewViewHolder {
        return DgRecyclerAdopter.ResultHolder.from(parent)
    }
}