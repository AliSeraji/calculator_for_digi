package com.example.dgcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class MultiViewViewHolderProvider {
    private val viewHolderTypes : HashMap<Int, Class<out DgRecyclerAdopter.MultiViewViewHolder>> = HashMap()
    private val viewHolderFactories = HashMap<Int, MultiViewHolderFactory>()


    fun createViewHolder(parent: ViewGroup, viewType : Int) : DgRecyclerAdopter.MultiViewViewHolder {
        return if(viewHolderFactories[viewType] != null) {
            viewHolderFactories[viewType]!!.createInstance(parent,viewType)
        } else {
            viewHolderTypes[viewType]!!.getConstructor(ViewGroup::class.java).newInstance(LayoutInflater.from(parent.context).inflate(viewType, parent))
            //viewHolderFactories[viewType]!!.createInstance(parent,viewType)
        }
    }

    fun registerViewHolderClass(resId : Int, clazz: Class<out DgRecyclerAdopter.MultiViewViewHolder>) {
        viewHolderTypes[resId] = clazz
    }

    fun registerViewHolderFactory(viewType : Int, viewHolderFactory: MultiViewHolderFactory) {
        viewHolderFactories[viewType] = viewHolderFactory
    }
}
interface MultiViewHolderFactory {
    fun createInstance(parent: ViewGroup, viewType : Int) : DgRecyclerAdopter.MultiViewViewHolder
}