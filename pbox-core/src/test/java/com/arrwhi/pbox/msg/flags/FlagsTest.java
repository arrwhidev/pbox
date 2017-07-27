package com.arrwhi.pbox.msg.flags;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 12/02/17.
 */
public class FlagsTest {

    @Test
    public void defaultConstructor_shouldSetAllBitsToZero() throws Exception {
        Flags flags = new Flags();
        assertThat(flags.toString(), equalTo("00000000"));
    }

    @Test
    public void constructorWithValue_shouldSetAllBitsToZero() throws Exception {
        Flags flags = new Flags((byte) 3);
        assertThat(flags.toString(), equalTo("00000011"));
    }

    @Test
    public void isDirectory_shouldReturnFalseByDefault() throws Exception {
        Flags flags = new Flags();
        assertThat(flags.isDirectory(), equalTo(false));
        assertThat(flags.toString(), equalTo("00000000"));
    }

    @Test
    public void isDirectory_shouldReturnTrueWhenSetToTrue() throws Exception {
        Flags flags = new Flags();
        flags.setIsDirectory(true);
        assertThat(flags.isDirectory(), equalTo(true));
        assertThat(flags.toString(), equalTo("00000001"));
    }

    @Test
    public void isDirectory_shouldReturnFalseWhenCleared() throws Exception {
        Flags flags = new Flags();
        flags.setIsDirectory(true);
        assertThat(flags.isDirectory(), equalTo(true));
        assertThat(flags.toString(), equalTo("00000001"));

        flags.setIsDirectory(false);
        assertThat(flags.isDirectory(), equalTo(false));
        assertThat(flags.toString(), equalTo("00000000"));
    }
}
