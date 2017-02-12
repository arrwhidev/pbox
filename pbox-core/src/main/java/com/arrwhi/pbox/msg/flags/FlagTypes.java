package com.arrwhi.pbox.msg.flags;

/**
 * Created by arran on 12/02/17.
 */
enum FlagTypes {
    IS_DIRECTORY(0);
    // TODO: Add types as new flags are needed.

    private int i;
    FlagTypes(int i) {
        this.i = i;
    }

    public int get() {
        return i;
    }
}