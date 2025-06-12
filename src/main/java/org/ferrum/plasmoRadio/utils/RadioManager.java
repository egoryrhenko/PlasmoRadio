package org.ferrum.plasmoRadio.utils;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.BandPass;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.RadioAddon;
import su.plo.voice.api.audio.codec.AudioDecoder;
import su.plo.voice.api.audio.codec.AudioEncoder;
import su.plo.voice.api.audio.codec.CodecException;
import su.plo.voice.api.encryption.Encryption;
import su.plo.voice.api.encryption.EncryptionException;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.*;

public class RadioManager {

    private static Encryption encryption;
    private static AudioDecoder decoder;
    private static AudioEncoder encoder;


    public static HashMap<Location, Microphone> microphones = new HashMap<>();
    public static HashMap<Location, Speaker> speakers = new HashMap<>();

    public static void init() {
        encryption = RadioAddon.getVoiceServer.getDefaultEncryption();
        decoder = RadioAddon.getVoiceServer.createOpusDecoder(false);
        encoder = RadioAddon.getVoiceServer.createOpusEncoder(false);

    }


    public static ServerActivation.PlayerActivationListener onActivation() {
        return (player, packet) -> {
            Location loc = Bukkit.getPlayer(player.getInstance().getUuid()).getLocation();
            for (Microphone microphone : RadioManager.microphones.values()) {
                if (loc.distanceSquared(microphone.location) < 4) {
                    for (Speaker speaker : microphone.speakers) {
                        ServerStaticSource source = speaker.getSource(player.getInstance().getUuid());
                        source.sendAudioPacket(
                                new SourceAudioPacket(
                                        packet.getSequenceNumber(),
                                        (byte) source.getState(),
                                        packet.getData(),
                                        source.getId(),
                                        (short) 10
                                ),
                                (short) 10
                        );
                    }
                }

            }
            return ServerActivation.Result.IGNORED;
        };
    }

    public static ServerActivation.PlayerActivationEndListener onActivationEnd() {
        return (player, packet) -> {
            for (Speaker speaker : RadioManager.speakers.values()) {
                speaker.removeSource(player.getInstance().getUuid());
            }
            return ServerActivation.Result.IGNORED;
        };
    }


    public static byte[] changeVolume(byte[] encryptedInput, float volume) {
        try {
            // Decode Opus -> PCM
            short[] pcm = decoder.decode(encryption.decrypt(encryptedInput));

            for (int i = 0 ; i < pcm.length ; i++) {
                pcm[i] = (short) (pcm[i] * volume);
            }

            //oldRadioEffect(pcm);

            // Encrypt
            return encryption.encrypt(encoder.encode(pcm));
        } catch (Exception e) {
            PlasmoRadio.log(e.getMessage());
            return encryptedInput; // fallback — возвращаем оригинал если ошибка
        }
    }

    public static void oldRadioEffect(short[] pcm) {
        // Downsampling (упрощённо)
        int downsampleFactor = 4;
        for (int i = 0; i < pcm.length; i += downsampleFactor) {
            short sample = pcm[i];
            // Клиппинг
            if (sample > 20000) sample = 20000;
            if (sample < -20000) sample = -20000;
            // Нелинейное искажение
            float s = sample / 32768f;
            s = (float) Math.tanh(s * 3.0);
            pcm[i] = (short) (s * 32767);

            // Добавим шум
            int noise = (int) ((Math.random() - 0.5) * 200); // шум +-100
            pcm[i] = (short) Math.max(Math.min(pcm[i] + noise, Short.MAX_VALUE), Short.MIN_VALUE);
        }
    }
}
