package de.reiss.thepokerguys.eventbus

import org.greenrobot.eventbus.EventBus

object AppEventBus {

    fun register(listener: Any) {
        EventBus.getDefault().register(listener)
    }

    fun unregister(listener: Any) {
        EventBus.getDefault().unregister(listener)
    }

    fun post(event: AppEvent) {
        EventBus.getDefault().post(event)
    }

}