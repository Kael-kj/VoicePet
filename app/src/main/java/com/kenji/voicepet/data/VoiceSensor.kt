package com.kenji.voicepet.data

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.abs

class VoiceSensor {

    @SuppressLint("MissingPermission")
    fun observeVoiceLevel(): Flow<Float> = flow {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) return@flow

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val buffer = ShortArray(bufferSize)
        audioRecord.startRecording()

        try {
            while (true) {
                val readCount = audioRecord.read(buffer, 0, bufferSize)
                if (readCount > 0) {
                    var sum = 0.0
                    for (i in 0 until readCount) {
                        sum += abs(buffer[i].toInt())
                    }
                    val average = sum / readCount

                    val normalizedVolume = (average / 3000.0).toFloat().coerceIn(0f, 1f)

                    emit(normalizedVolume)
                }
                kotlinx.coroutines.delay(30)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                audioRecord.stop()
                audioRecord.release()
            } catch (e: Exception) {}
        }
    }
}