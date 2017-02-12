package com.arrwhi.pbox.msg.flags;

/**
 * Created by arran on 12/02/17.
 *
 * Helper wrapper for flags, uses 1 byte for 8 flags (1 bit per flag).
 *
 * 0 0 0 0 0 0 0 0
 * | | | | | | | |
 * | | | | | | | isDirectory (0)
 * | | | | | | ?
 * | | | | | ?
 * | | | | ?
 * | | | ?
 * | | ?
 * | ?
 * ?
 */
public class Flags {
    private byte flags = 0;

    public Flags() {
        flags = 0;
    }

    public Flags(byte initialFlags) {
        flags = initialFlags;
    }

    public boolean isDirectory() {
        return isBitSet(FlagTypes.IS_DIRECTORY.get());
    }

    public void setIsDirectory(boolean isDirectory) {
        if (isDirectory) {
            flags |= 1 << FlagTypes.IS_DIRECTORY.get();
        } else {
            flags &= ~(1 << FlagTypes.IS_DIRECTORY.get());
        }
    }

    public String toString() {
        String bitsAsString = "";
        for (int i = 7; i > -1; i--) {
            bitsAsString += bitAt(i);
        }
        return bitsAsString;
    }

    public byte getFlags() {
        return flags;
    }

    private boolean isBitSet(int i) {
        return bitAt(i) == 0 ? false : true;
    }

    private int bitAt(int i) {
        return (flags >> i) & 1;
    }
}
