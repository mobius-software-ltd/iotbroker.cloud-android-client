package com.mobius.software.android.iotbroker.main.utility;

import java.nio.ByteBuffer;

public class ConvertorUtil {

    public static Integer bytesToInt(byte[] array) {
        String string = new String(array);
        return Integer.parseInt(string);
    }

    public static byte[] intToByte(int num) {
        ByteBuffer array = ByteBuffer.allocate(4);
        array.putInt(num);
        return array.array();
    }
}
