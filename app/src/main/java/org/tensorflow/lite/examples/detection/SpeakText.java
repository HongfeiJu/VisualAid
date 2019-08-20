package org.tensorflow.lite.examples.detection;

import android.speech.tts.TextToSpeech;

public class SpeakText {
    private static final SpeakText ourInstance = new SpeakText();
    private TextToSpeech textToSpeech;

    public static SpeakText getInstance() {
        return ourInstance;
    }

    private SpeakText() {

    }
}
