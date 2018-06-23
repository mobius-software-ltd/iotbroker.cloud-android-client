package com.mobius.software.android.iotbroker.main.utility;

import java.nio.ByteBuffer;

public class ConvertorUtil {

    public static Integer byteToInt(byte[] array) {
        ByteBuffer wrapped = ByteBuffer.wrap(array);
        return wrapped.getInt();
    }

    public static byte[] intToByte(int num) {
        ByteBuffer array = ByteBuffer.allocate(4);
        array.putInt(num);
        return array.array();
    }
}
