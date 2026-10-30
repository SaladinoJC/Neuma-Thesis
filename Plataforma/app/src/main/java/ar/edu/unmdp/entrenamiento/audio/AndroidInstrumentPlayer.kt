package ar.edu.unmdp.entrenamiento.audio

import android.content.Context
import ar.edu.unmdp.entrenamiento.instruments.Instrument
import ar.edu.unmdp.entrenamiento.instruments.InstrumentPlayer
import ar.edu.unmdp.entrenamiento.instruments.InstrumentMelody

class AndroidInstrumentPlayer(context: Context) : InstrumentPlayer {
    private val samples = AndroidSamplePlayer(context)
    override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int =
        samples.playPhrase(melody.midiNotes.zip(melody.durationsMillis).map { (midi, duration) ->
            SampleNote(instrument.melodyAsset(midi), duration)
        }, melody.gapMillis, onStarted)
}
