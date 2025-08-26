package org.ferrum.plasmoRadio;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.voice.api.audio.codec.AudioDecoder;
import su.plo.voice.api.audio.codec.AudioEncoder;
import su.plo.voice.api.encryption.Encryption;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;

import java.util.*;

public class RadioManager {

    private static Encryption encryption;
    private static AudioDecoder decoder;
    private static AudioEncoder encoder;

    public static void init() {
        //encryption = RadioAddon.getVoiceServer.getDefaultEncryption();
        //decoder = RadioAddon.getVoiceServer.createOpusDecoder(false);
        //encoder = RadioAddon.getVoiceServer.createOpusEncoder(false);

    }

    public static void unloadChunk(Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        int minX = chunk.getX() << 4;
        int maxX = minX + 15;
        int minZ = chunk.getZ() << 4;
        int maxZ = minZ + 15;

        Iterator<Map.Entry<Location, RadioBlock>> iterator = RadioDeviceRegistry.devices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, RadioBlock> entry = iterator.next();
            Location loc = entry.getKey();

            if (!loc.getWorld().getName().equals(worldName)) continue;

            int x = loc.getBlockX();
            int z = loc.getBlockZ();

            if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                entry.getValue().remove();
                iterator.remove();
            }
        }
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
