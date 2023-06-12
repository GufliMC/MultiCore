package com.guflimc.multicore.common.adapter;

import org.jetbrains.annotations.NotNull;

import java.io.*;

public class SerializableAdapter {

    public static byte[] adapt(@NotNull Serializable obj) {
        try (
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
        ) {
            oos.writeObject(obj);
            oos.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Serializable adapt(byte @NotNull [] data) {
        try (
                ByteArrayInputStream is = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(is);
        ) {
            return (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}