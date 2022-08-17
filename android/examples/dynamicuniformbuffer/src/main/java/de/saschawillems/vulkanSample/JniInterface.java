package de.saschawillems.vulkanSample;

public class JniInterface {


    static {
        // Load native library
        System.loadLibrary("native-lib");
    }


    public static  native void ChangeDisplayCount(int count);
}
