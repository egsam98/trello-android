package com.project.trello_fintech.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 * Состояние микрофона
 * @property isOn Boolean
 * @property icon Int
 */
enum class MicState(val isOn: Boolean, val icon: Int) {
    ON(true, android.R.drawable.ic_btn_speak_now),
    OFF(false, android.R.drawable.stat_notify_call_mute)
}


/**
 * Состояние видеокамеры
 * @property isOn Boolean
 * @property icon Int
 */
enum class VideoState(val isOn: Boolean, val icon: Int) {
    ON(true, android.R.drawable.presence_video_online),
    OFF(false, android.R.drawable.presence_video_busy)
}

/**
 * ViewModel для VideoCallActivity
 * @property micState MutableLiveData<MicState>
 * @property videoState MutableLiveData<VideoState>
 */
class VideoCallViewModel: ViewModel() {

    val micState = MutableLiveData<MicState>(MicState.ON)
    val videoState = MutableLiveData<VideoState>(VideoState.OFF)

    fun turnOnOffMic() {
        micState.value = if (micState.value!!.isOn) MicState.OFF else MicState.ON
    }

    fun turnOnOffVideo() {
        videoState.value = if (videoState.value!!.isOn) VideoState.OFF else VideoState.ON
    }
}