package ar.edu.unmdp.entrenamiento.audio

import android.content.Context
import ar.edu.unmdp.entrenamiento.instruments.Instrument
import ar.edu.unmdp.entrenamiento.instruments.InstrumentPlayer

class AndroidInstrumentPlayer(context: Context) : InstrumentPlayer {
    private val samples = AndroidSamplePlayer(context)
    override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int =
        samples.play(instrument.asset, onStarted)
}
