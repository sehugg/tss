/**
 * CPU Emulation Suites for Java
 */
package org.twintail.cpu;

import java.util.logging.Logger;

import org.twintail.Log;
import org.twintail.j2se.tss.J2SELog;

/**
 * class Test6502
 *
 * This class tests Cpu6502 class.
 * @author Takashi Toyoshima <toyoshim@gmail.com>
 */
public final class Test6502 implements Memory {
    private static final int MEMORY_SIZE = 0x10000;
    private static final int DEFAULT_SEED = 0xffff;
    private static final int SEED_UPDATE_MASK = 9;
    private static final int SEED_UPDATE_RSHIFT = 3;
    private static final int SEED_UPDATE_LSHIFT = 15;
    private static final int SHORT_MASK = 0xffff;
    private static final int BYTE_MASK = 0xff;
    private static final int BYTE_BITS = 8;
    private static final int NUM_OF_STEP_TESTS = 256;
    private static final int REG_A = 0;
    private static final int REG_B = 1;
    private static final int REG_X = 2;
    private static final int REG_Y = 3;
    private static final int REG_Z = 4;
    private static final int REG_P = 5;
    private static final int REG_S = 6;
    private static final int REG_PC = 7;
    private static final int INST = 8;
    private static final int ADDRESS = 0;
    private static final int DATA = 1;
    private static final int END_OF_DATA = 0xffff;

    private static final int[][] EXP_STEP_REGS = {
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0001, 0xfe },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0004, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0xfd, 0xfa39, 0x02 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0xfd, 0xfa3a, 0x80 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0xfd, 0xfa52, 0x11 },
        { 0x16, 0x00, 0x00, 0x00, 0x00, 0x14, 0xfd, 0xfa54, 0x9e },
        { 0x16, 0x00, 0x00, 0x00, 0x00, 0x14, 0xfd, 0xfa57, 0x14 },
        { 0x16, 0x00, 0x00, 0x00, 0x00, 0x14, 0xfd, 0xfa59, 0xb4 },
        { 0x16, 0x00, 0x00, 0xfd, 0x00, 0x94, 0xfd, 0xfa5b, 0x15 },
        { 0xd7, 0x00, 0x00, 0xfd, 0x00, 0x94, 0xfd, 0xfa5d, 0xbe },
        { 0xd7, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa60, 0x47 },
        { 0xd7, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa61, 0x35 },
        { 0x17, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa63, 0x9e },
        { 0x17, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa66, 0xd9 },
        { 0x17, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa69, 0xdc },
        { 0x17, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa6a, 0x96 },
        { 0x17, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa6c, 0x27 },
        { 0x17, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa6d, 0xf9 },
        { 0x27, 0x00, 0x79, 0xfd, 0x00, 0x14, 0xfd, 0xfa70, 0xd9 },
        { 0x27, 0x00, 0x79, 0xfd, 0x00, 0x94, 0xfd, 0xfa73, 0x47 },
        { 0x27, 0x00, 0x79, 0xfd, 0x00, 0x94, 0xfd, 0xfa74, 0xb6 },
        { 0x27, 0x00, 0xa6, 0xfd, 0x00, 0x94, 0xfd, 0xfa76, 0x05 },
        { 0xf7, 0x00, 0xa6, 0xfd, 0x00, 0x94, 0xfd, 0xfa78, 0x28 },
        { 0xf7, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfe, 0xfa79, 0xfb },
        { 0xf7, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfe, 0xfa7a, 0x6f },
        { 0xf7, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfe, 0xfa7b, 0x20 },
        { 0xf7, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfc, 0x2016, 0xbd },
        { 0xe6, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x2019, 0x3b },
        { 0xe6, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x201a, 0xd1 },
        { 0xe6, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x201c, 0x5e },
        { 0xe6, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfc, 0x201f, 0xb2 },
        { 0x78, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfc, 0x2021, 0x25 },
        { 0x10, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfc, 0x2023, 0x09 },
        { 0xb4, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x2025, 0x44 },
        { 0xb4, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x2026, 0x86 },
        { 0xb4, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x2028, 0xb5 },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x10, 0xfc, 0x202a, 0x1e },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x202d, 0x22 },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x202e, 0x8d },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x2031, 0x81 },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x2033, 0x8f },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x2034, 0xc4 },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x2036, 0xe3 },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x91, 0xfc, 0x2037, 0x7e },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x203a, 0x3c },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x203d, 0xc6 },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x203f, 0xf6 },
        { 0x09, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x2041, 0x45 },
        { 0x99, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x2043, 0x6c },
        { 0x99, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x9864, 0x92 },
        { 0x99, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x9866, 0x05 },
        { 0x9b, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x9868, 0x2c },
        { 0x9b, 0x00, 0xa6, 0xfd, 0x00, 0x50, 0xfc, 0x986b, 0x33 },
        { 0x9b, 0x00, 0xa6, 0xfd, 0x00, 0x50, 0xfc, 0x986c, 0x12 },
        { 0x9f, 0x00, 0xa6, 0xfd, 0x00, 0xd0, 0xfc, 0x986e, 0x87 },
        { 0x9f, 0x00, 0xa6, 0xfd, 0x00, 0xd0, 0xfc, 0x986f, 0xf7 },
        { 0x9f, 0x00, 0xa6, 0xfd, 0x00, 0xd0, 0xfc, 0x9870, 0xb8 },
        { 0x9f, 0x00, 0xa6, 0xfd, 0x00, 0x90, 0xfc, 0x9871, 0x4a },
        { 0x4f, 0x00, 0xa6, 0xfd, 0x00, 0x11, 0xfc, 0x9872, 0x7a },
        { 0x4f, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0xfd, 0x9873, 0x19 },
        { 0x5f, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0xfd, 0x9876, 0xfc },
        { 0x5f, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0xfd, 0x9877, 0x73 },
        { 0x5f, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0xfd, 0x9878, 0x1f },
        { 0x5f, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0xfd, 0x9879, 0xeb },
        { 0x5f, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0xfd, 0x987a, 0xe0 },
        { 0x5f, 0x00, 0xa6, 0x7d, 0x00, 0x90, 0xfd, 0x987c, 0xe5 },
        { 0x51, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0xfd, 0x987e, 0xcd },
        { 0x51, 0x00, 0xa6, 0x7d, 0x00, 0x10, 0xfd, 0x9881, 0xe0 },
        { 0x51, 0x00, 0xa6, 0x7d, 0x00, 0x90, 0xfd, 0x9883, 0xe5 },
        { 0x92, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfd, 0x9885, 0xcb },
        { 0x92, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfd, 0x9886, 0x17 },
        { 0x92, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfd, 0x9887, 0x93 },
        { 0x92, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfd, 0x9888, 0xab },
        { 0x92, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfd, 0x9889, 0x0e },
        { 0x92, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfd, 0x988c, 0x68 },
        { 0xfa, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfe, 0x988d, 0xb2 },
        { 0x83, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfe, 0x988f, 0x23 },
        { 0x83, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfe, 0x9890, 0x44 },
        { 0x83, 0x00, 0xa6, 0x7d, 0x00, 0xd0, 0xfe, 0x9891, 0x39 },
        { 0x03, 0x00, 0xa6, 0x7d, 0x00, 0x50, 0xfe, 0x9894, 0x4a },
        { 0x01, 0x00, 0xa6, 0x7d, 0x00, 0x51, 0xfe, 0x9895, 0x60 },
        { 0x01, 0x00, 0xa6, 0x7d, 0x00, 0x51, 0x00, 0x0007, 0x8e },
        { 0x01, 0x00, 0xa6, 0x7d, 0x00, 0x51, 0x00, 0x000a, 0xe0 },
        { 0x01, 0x00, 0xa6, 0x7d, 0x00, 0xd1, 0x00, 0x000c, 0xdf },
        { 0x01, 0x00, 0xa6, 0x7d, 0x00, 0xd1, 0x00, 0x000d, 0x46 },
        { 0x01, 0x00, 0xa6, 0x7d, 0x00, 0x50, 0x00, 0x000f, 0x71 },
        { 0x9d, 0x00, 0xa6, 0x7d, 0x00, 0x90, 0x00, 0x0011, 0xff },
        { 0x9d, 0x00, 0xa6, 0x7d, 0x00, 0x90, 0x00, 0x0012, 0x7e },
        { 0x9d, 0x00, 0xa6, 0x7d, 0x00, 0x11, 0x00, 0x0015, 0x61 },
        { 0xc5, 0x00, 0xa6, 0x7d, 0x00, 0x90, 0x00, 0x0017, 0x6e },
        { 0xc5, 0x00, 0xa6, 0x7d, 0x00, 0x10, 0x00, 0x001a, 0xa6 },
        { 0xc5, 0x00, 0xcb, 0x7d, 0x00, 0x90, 0x00, 0x001c, 0x91 },
        { 0xc5, 0x00, 0xcb, 0x7d, 0x00, 0x90, 0x00, 0x001e, 0x1c },
        { 0xc5, 0x00, 0xcb, 0x7d, 0x00, 0x90, 0x00, 0x0021, 0x70 },
        { 0xc5, 0x00, 0xcb, 0x7d, 0x00, 0x90, 0x00, 0x0023, 0xf0 },
        { 0xc5, 0x00, 0xcb, 0x7d, 0x00, 0x90, 0x00, 0x0025, 0x77 },
        { 0xc5, 0x00, 0xcb, 0x7d, 0x00, 0x90, 0x00, 0x0026, 0xe3 },
        { 0xc5, 0x00, 0xcb, 0x7d, 0x00, 0x90, 0x00, 0x0027, 0xc8 },
        { 0xc5, 0x00, 0xcb, 0x7e, 0x00, 0x10, 0x00, 0x0028, 0xfd },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x51, 0x00, 0x002b, 0xf7 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x51, 0x00, 0x002c, 0x9e },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x51, 0x00, 0x002f, 0x10 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x51, 0x00, 0x003d, 0x47 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x51, 0x00, 0x003e, 0xfc },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x51, 0x00, 0x003f, 0x78 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x55, 0x00, 0x0040, 0x1f },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x55, 0x00, 0x0041, 0xb8 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x15, 0x00, 0x0042, 0xe2 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x15, 0x00, 0x0043, 0x7f },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x15, 0x00, 0x0044, 0xf1 },
        { 0x38, 0x00, 0xcb, 0x7e, 0x00, 0x15, 0x00, 0x0046, 0x7d },
        { 0x09, 0x00, 0xcb, 0x7e, 0x00, 0x15, 0x00, 0x0049, 0x9f },
        { 0x09, 0x00, 0xcb, 0x7e, 0x00, 0x15, 0x00, 0x004a, 0x1d },
        { 0xad, 0x00, 0xcb, 0x7e, 0x00, 0x95, 0x00, 0x004d, 0x58 },
        { 0xad, 0x00, 0xcb, 0x7e, 0x00, 0x91, 0x00, 0x004e, 0x44 },
        { 0xad, 0x00, 0xcb, 0x7e, 0x00, 0x91, 0x00, 0x004f, 0x9a },
        { 0xad, 0x00, 0xcb, 0x7e, 0x00, 0x91, 0xcb, 0x0050, 0x60 },
        { 0xad, 0x00, 0xcb, 0x7e, 0x00, 0x91, 0xcd, 0x4d16, 0x06 },
        { 0xad, 0x00, 0xcb, 0x7e, 0x00, 0x11, 0xcd, 0x4d18, 0x35 },
        { 0x08, 0x00, 0xcb, 0x7e, 0x00, 0x11, 0xcd, 0x4d1a, 0x98 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x11, 0xcd, 0x4d1b, 0x34 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0xd1, 0xcd, 0x4d1d, 0x96 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0xd1, 0xcd, 0x4d1f, 0x22 },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0xd1, 0xcd, 0x4d20, 0x3c },
        { 0x7e, 0x00, 0xcb, 0x7e, 0x00, 0x11, 0xcd, 0x4d23, 0xad },
        { 0xd9, 0x00, 0xcb, 0x7e, 0x00, 0x91, 0xcd, 0x4d26, 0xb6 },
        { 0xd9, 0x00, 0xf8, 0x7e, 0x00, 0x91, 0xcd, 0x4d28, 0x01 },
        { 0xdf, 0x00, 0xf8, 0x7e, 0x00, 0x91, 0xcd, 0x4d2a, 0x0e },
        { 0xdf, 0x00, 0xf8, 0x7e, 0x00, 0x11, 0xcd, 0x4d2d, 0xaa },
        { 0xdf, 0x00, 0xdf, 0x7e, 0x00, 0x91, 0xcd, 0x4d2e, 0xa6 },
        { 0xdf, 0x00, 0x61, 0x7e, 0x00, 0x11, 0xcd, 0x4d30, 0x91 },
        { 0xdf, 0x00, 0x61, 0x7e, 0x00, 0x11, 0xcd, 0x4d32, 0x19 },
        { 0xdf, 0x00, 0x61, 0x7e, 0x00, 0x91, 0xcd, 0x4d35, 0xc5 },
        { 0xdf, 0x00, 0x61, 0x7e, 0x00, 0x90, 0xcd, 0x4d37, 0xef },
        { 0xdf, 0x00, 0x61, 0x7e, 0x00, 0x90, 0xcd, 0x4d38, 0xa0 },
        { 0xdf, 0x00, 0x61, 0x92, 0x00, 0x90, 0xcd, 0x4d3a, 0xa4 },
        { 0xdf, 0x00, 0x61, 0x8e, 0x00, 0x90, 0xcd, 0x4d3c, 0x84 },
        { 0xdf, 0x00, 0x61, 0x8e, 0x00, 0x90, 0xcd, 0x4d3e, 0xa5 },
        { 0x6a, 0x00, 0x61, 0x8e, 0x00, 0x10, 0xcd, 0x4d40, 0x8a },
        { 0x61, 0x00, 0x61, 0x8e, 0x00, 0x10, 0xcd, 0x4d41, 0xd2 },
        { 0x61, 0x00, 0x61, 0x8e, 0x00, 0x11, 0xcd, 0x4d43, 0x44 },
        { 0x61, 0x00, 0x61, 0x8e, 0x00, 0x11, 0xcd, 0x4d44, 0x3e },
        { 0x61, 0x00, 0x61, 0x8e, 0x00, 0x11, 0xcd, 0x4d47, 0x4b },
        { 0x61, 0x00, 0x61, 0x8e, 0x00, 0x11, 0xcd, 0x4d48, 0xa7 },
        { 0x61, 0x00, 0x61, 0x8e, 0x00, 0x11, 0xcd, 0x4d49, 0x16 },
        { 0x61, 0x00, 0x61, 0x8e, 0x00, 0x91, 0xcd, 0x4d4b, 0xa2 },
        { 0x61, 0x00, 0x62, 0x8e, 0x00, 0x11, 0xcd, 0x4d4d, 0xb1 },
        { 0xc7, 0x00, 0x62, 0x8e, 0x00, 0x91, 0xcd, 0x4d4f, 0x3a },
        { 0xc6, 0x00, 0x62, 0x8e, 0x00, 0x91, 0xcd, 0x4d50, 0xce },
        { 0xc6, 0x00, 0x62, 0x8e, 0x00, 0x11, 0xcd, 0x4d53, 0x89 },
        { 0xc6, 0x00, 0x62, 0x8e, 0x00, 0x51, 0xcd, 0x4d55, 0xc2 },
        { 0xc6, 0x00, 0x62, 0x8e, 0x00, 0x51, 0xcd, 0x4d56, 0xdb },
        { 0xc6, 0x00, 0x62, 0x8e, 0x00, 0x51, 0xcd, 0x4d57, 0xd4 },
        { 0xc6, 0x00, 0x62, 0x8e, 0x00, 0x51, 0xcd, 0x4d58, 0x05 },
        { 0xf7, 0x00, 0x62, 0x8e, 0x00, 0xd1, 0xcd, 0x4d5a, 0x2e },
        { 0xf7, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d5d, 0x7f },
        { 0xf7, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d5e, 0x9b },
        { 0xf7, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d5f, 0x83 },
        { 0xf7, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d60, 0x47 },
        { 0xf7, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d61, 0x99 },
        { 0xf7, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d64, 0xa1 },
        { 0x63, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d66, 0xaf },
        { 0x63, 0x00, 0x62, 0x8e, 0x00, 0x50, 0xcd, 0x4d67, 0xfa },
        { 0x63, 0x00, 0xbf, 0x8e, 0x00, 0xd0, 0xce, 0x4d68, 0xd0 },
        { 0x63, 0x00, 0xbf, 0x8e, 0x00, 0xd0, 0xce, 0x4d96, 0xa0 },
        { 0x63, 0x00, 0xbf, 0x86, 0x00, 0xd0, 0xce, 0x4d98, 0xa4 },
        { 0x63, 0x00, 0xbf, 0x74, 0x00, 0x50, 0xce, 0x4d9a, 0x81 },
        { 0x63, 0x00, 0xbf, 0x74, 0x00, 0x50, 0xce, 0x4d9c, 0x88 },
        { 0x63, 0x00, 0xbf, 0x73, 0x00, 0x50, 0xce, 0x4d9d, 0x53 },
        { 0x63, 0x00, 0xbf, 0x73, 0x00, 0x50, 0xce, 0x4d9e, 0xca },
        { 0x63, 0x00, 0xbe, 0x73, 0x00, 0xd0, 0xce, 0x4d9f, 0xcd },
        { 0x63, 0x00, 0xbe, 0x73, 0x00, 0x51, 0xce, 0x4da2, 0x79 },
        { 0x87, 0x00, 0xbe, 0x73, 0x00, 0xd0, 0xce, 0x4da5, 0xff },
        { 0x87, 0x00, 0xbe, 0x73, 0x00, 0xd0, 0xce, 0x4da6, 0x13 },
        { 0x87, 0x00, 0xbe, 0x73, 0x00, 0xd0, 0xce, 0x4da7, 0x07 },
        { 0x87, 0x00, 0xbe, 0x73, 0x00, 0xd0, 0xce, 0x4da8, 0x8b },
        { 0x87, 0x00, 0xbe, 0x73, 0x00, 0xd0, 0xce, 0x4da9, 0x3b },
        { 0x87, 0x00, 0xbe, 0x73, 0x00, 0xd0, 0xce, 0x4daa, 0xd2 },
        { 0x87, 0x00, 0xbe, 0x73, 0x00, 0x51, 0xce, 0x4dac, 0x45 },
        { 0xc6, 0x00, 0xbe, 0x73, 0x00, 0xd1, 0xce, 0x4dae, 0x6b },
        { 0xc6, 0x00, 0xbe, 0x73, 0x00, 0xd1, 0xce, 0x4daf, 0xb4 },
        { 0xc6, 0x00, 0xbe, 0x37, 0x00, 0x51, 0xce, 0x4db1, 0x15 },
        { 0xf7, 0x00, 0xbe, 0x37, 0x00, 0xd1, 0xce, 0x4db3, 0xb9 },
        { 0x2b, 0x00, 0xbe, 0x37, 0x00, 0x51, 0xce, 0x4db6, 0x28 },
        { 0x2b, 0x00, 0xbe, 0x37, 0x00, 0x20, 0xcf, 0x4db7, 0xd5 },
        { 0x2b, 0x00, 0xbe, 0x37, 0x00, 0xa0, 0xcf, 0x4db9, 0x7e },
        { 0x2b, 0x00, 0xbe, 0x37, 0x00, 0x20, 0xcf, 0x4dbc, 0xf1 },
        { 0xe2, 0x00, 0xbe, 0x37, 0x00, 0xa0, 0xcf, 0x4dbe, 0x7e },
        { 0xe2, 0x00, 0xbe, 0x37, 0x00, 0x21, 0xcf, 0x4dc1, 0xa6 },
        { 0xe2, 0x00, 0x6b, 0x37, 0x00, 0x21, 0xcf, 0x4dc3, 0x91 },
        { 0xe2, 0x00, 0x6b, 0x37, 0x00, 0x21, 0xcf, 0x4dc5, 0x1a },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0xa1, 0xcf, 0x4dc6, 0x56 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0x21, 0xcf, 0x4dc8, 0xe0 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0xa0, 0xcf, 0x4dca, 0xe4 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0x21, 0xcf, 0x4dcc, 0xc3 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0x21, 0xcf, 0x4dcd, 0x76 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0xa1, 0xcf, 0x4dcf, 0xc0 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0x21, 0xcf, 0x4dd1, 0xc0 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0x20, 0xcf, 0x4dd3, 0xc7 },
        { 0xe3, 0x00, 0x6b, 0x37, 0x00, 0x20, 0xcf, 0x4dd4, 0xae },
        { 0xe3, 0x00, 0xd6, 0x37, 0x00, 0xa0, 0xcf, 0x4dd7, 0x2c },
        { 0xe3, 0x00, 0xd6, 0x37, 0x00, 0x60, 0xcf, 0x4dda, 0x92 },
        { 0xe3, 0x00, 0xd6, 0x37, 0x00, 0x60, 0xcf, 0x4ddc, 0x03 },
        { 0xe3, 0x00, 0xd6, 0x37, 0x00, 0x60, 0xcf, 0x4ddd, 0x68 },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0x60, 0xd0, 0x4dde, 0x18 },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0x60, 0xd0, 0x4ddf, 0x28 },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0x22, 0xd1, 0x4de0, 0xd9 },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0x20, 0xd1, 0x4de3, 0x1e },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0xa0, 0xd1, 0x4de6, 0x95 },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0xa0, 0xd1, 0x4de8, 0x38 },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0xa1, 0xd1, 0x4de9, 0x4f },
        { 0x46, 0x00, 0xd6, 0x37, 0x00, 0xa1, 0xd1, 0x4dea, 0xfa },
        { 0x46, 0x00, 0x77, 0x37, 0x00, 0x21, 0xd2, 0x4deb, 0x30 },
        { 0x46, 0x00, 0x77, 0x37, 0x00, 0x21, 0xd2, 0x4ded, 0xb1 },
        { 0x00, 0x00, 0x77, 0x37, 0x00, 0x23, 0xd2, 0x4def, 0x3a },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xa1, 0xd2, 0x4df0, 0xc7 },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xa1, 0xd2, 0x4df1, 0xec },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xa0, 0xd2, 0x4df4, 0x3c },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xe0, 0xd2, 0x4df7, 0x44 },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xe0, 0xd2, 0x4df8, 0x08 },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xe0, 0xd1, 0x4df9, 0x64 },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xe0, 0xd1, 0x4dfb, 0x46 },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0x61, 0xd1, 0x4dfd, 0x76 },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xe1, 0xd1, 0x4dff, 0xc2 },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xe1, 0xd1, 0x4e00, 0x4c },
        { 0xff, 0x00, 0x77, 0x37, 0x00, 0xe1, 0xd1, 0x2ad0, 0xa6 },
        { 0xff, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ad2, 0x95 },
        { 0xff, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ad4, 0x3f },
        { 0xff, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ad5, 0x82 },
        { 0xff, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ad6, 0xc3 },
        { 0xff, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ad7, 0x94 },
        { 0xff, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ad9, 0x32 },
        { 0xd9, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2adb, 0xa3 },
        { 0xd9, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2adc, 0x13 },
        { 0xd9, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2add, 0xbb },
        { 0xd9, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ade, 0x8e },
        { 0xd9, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ae1, 0x58 },
        { 0xd9, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ae2, 0x17 },
        { 0xd9, 0x00, 0xdb, 0x37, 0x00, 0xe1, 0xd1, 0x2ae3, 0x98 },
        { 0x37, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x2ae4, 0xab },
        { 0x37, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x2ae5, 0x5d },
        { 0x3a, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x2ae8, 0x7c },
        { 0x3a, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x1e31, 0x83 },
        { 0x3a, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x1e32, 0xf4 },
        { 0x3a, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x1e33, 0x9c },
        { 0x3a, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x1e36, 0xd3 },
        { 0x3a, 0x00, 0xdb, 0x37, 0x00, 0x61, 0xd1, 0x1e37, 0x88 },
        { 0x3a, 0x00, 0xdb, 0x36, 0x00, 0x61, 0xd1, 0x1e38, 0x4f },
        { 0x3a, 0x00, 0xdb, 0x36, 0x00, 0x61, 0xd1, 0x1e39, 0xca },
        { 0x3a, 0x00, 0xda, 0x36, 0x00, 0xe1, 0xd1, 0x1e3a, 0x31 },
        { 0x28, 0x00, 0xda, 0x36, 0x00, 0x61, 0xd1, 0x1e3c, 0xbd },
        { 0x61, 0x00, 0xda, 0x36, 0x00, 0x61, 0xd1, 0x1e3f, 0x74 },
        { 0x61, 0x00, 0xda, 0x36, 0x00, 0x61, 0xd1, 0x1e41, 0xd3 },
        { 0x61, 0x00, 0xda, 0x36, 0x00, 0x61, 0xd1, 0x1e42, 0x8a },
        { 0xda, 0x00, 0xda, 0x36, 0x00, 0xe1, 0xd1, 0x1e43, 0x4f },
        { 0xda, 0x00, 0xda, 0x36, 0x00, 0xe1, 0xd1, 0x1e44, 0xd8 },
    };
    private static final int[][] EXP_STEP_MEMS = {
        { 0x0e00, 0x08 },
        { 0x0100, 0x00 },
        { 0x01ff, 0x06 },
        { 0x01fe, 0x10 },
        { 0x6d6f, 0x00 },
        { 0x0005, 0x68 },
        { 0x6b03, 0x00 },
        { 0x0035, 0x79 },
        { 0x01fe, 0xfa },
        { 0x01fd, 0x7d },
        { 0xa975, 0x29 },
        { 0x0060, 0xa6 },
        { 0xecf3, 0xb4 },
        { 0xe436, 0x09 },
        { 0xdb8b, 0x09 },
        { 0x8a9e, 0x89 },
        { 0x000c, 0xdf },
        { 0x0011, 0xff },
        { 0xe0f7, 0x99 },
        { 0x79f3, 0x82 },
        { 0xfe1f, 0xa6 },
        { 0x00e2, 0x2a },
        { 0x8eb3, 0x3b },
        { 0x1e16, 0x72 },
        { 0x6ac0, 0xc5 },
        { 0xfc0f, 0x32 },
        { 0x6d16, 0x00 },
        { 0x0079, 0x22 },
        { 0x0013, 0xcb },
        { 0x7ba1, 0x4a },
        { 0x008c, 0xdf },
        { 0x003b, 0x8e },
        { 0xcdc6, 0x21 },
        { 0x0000, 0xfe },
        { 0xb9ec, 0x70 },
        { 0x58d5, 0x27 },
        { 0x5309, 0xf7 },
        { 0x0a29, 0x63 },
        { 0x8edb, 0x34 },
        { 0x8ff0, 0x26 },
        { 0xdbc2, 0xe2 },
        { 0x0033, 0x09 },
        { 0x0043, 0xbf },
        { 0x8ffd, 0xe3 },
        { 0xeb58, 0xc8 },
        { 0x0094, 0x46 },
        { 0x01d2, 0xe0 },
        { 0x004b, 0x00 },
        { 0x0011, 0x7f },
        { 0x0011, 0xbf },
        { 0x0036, 0xff },
        { 0x00ba, 0x37 },
        { 0xfd67, 0xdb },
        { 0x7e50, 0x00 },
        { 0x00c6, 0x00 },
        { END_OF_DATA, END_OF_DATA },
    };

    private char[] memory = new char[MEMORY_SIZE];
    private int seed = DEFAULT_SEED;
    private Cpu6502 cpu = new Cpu6502();
    private int traceIndex = 0;
    private int traceScore = 0;
    private boolean traceError = false;
    private int traceAddress;
    private int traceData;

    /**
     * Class constructor.
     */
    public Test6502() {
        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = generateRandomByte();
            /*
            Log.getLog().info(String.format("memory %04x <= %02x\n",
                    i, memory[i] & BYTE_MASK));
            */
        }
        cpu.setMemory(this);
    }

    /**
     * Test random step execution details.
     * @return test result
     */
    public boolean step() {
        int pass = 0;
        boolean firstError = true;
        for (int s = 0; s < NUM_OF_STEP_TESTS; s++) {
            int pc = cpu.readRegister(Cpu6502.REG_PC);
            cpu.runStep();
            boolean ok = true;
            if (traceError) {
                traceError = false;
                ok = false;
                Log.getLog().warn("UNEXPECTED MEMORY ACCESS: "
                        + String.format("\ne: %04x := %02x",
                                EXP_STEP_MEMS[traceIndex - 1][ADDRESS],
                                EXP_STEP_MEMS[traceIndex - 1][DATA])
                                + String.format("\na: %04x := %02x",
                                        traceAddress, traceData));
            }
            for (int r = 0; r < Cpu6502.NUM_OF_REGS; r++) {
                if ((cpu.readRegister(r) & BYTE_MASK)
                    != (EXP_STEP_REGS[s][r] & BYTE_MASK)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                pass++;
            } else if (firstError) {
                String msg = String.format("STEP %d failed (PC:%04x/INST:%02x)",
                        s, pc, (int) (memory[pc] & BYTE_MASK))
                        + "\n    a  b  x  y  z  s  p   pc  i\ne: "
                        + String.format(
                                "%02x %02x %02x %02x %02x %02x %02x %04x %02x",
                                EXP_STEP_REGS[s][REG_A],
                                EXP_STEP_REGS[s][REG_B],
                                EXP_STEP_REGS[s][REG_X],
                                EXP_STEP_REGS[s][REG_Y],
                                EXP_STEP_REGS[s][REG_Z],
                                EXP_STEP_REGS[s][REG_S],
                                EXP_STEP_REGS[s][REG_P],
                                EXP_STEP_REGS[s][REG_PC],
                                (int) (memory[EXP_STEP_REGS[s][REG_PC]]
                                              & BYTE_MASK))
                        + "\na: "
                        + String.format(
                                "%02x %02x %02x %02x %02x %02x %02x %04x %02x",
                                cpu.readRegister(Cpu6502.REG_A),
                                cpu.readRegister(Cpu6502.REG_B),
                                cpu.readRegister(Cpu6502.REG_X),
                                cpu.readRegister(Cpu6502.REG_Y),
                                cpu.readRegister(Cpu6502.REG_Z),
                                cpu.readRegister(Cpu6502.REG_S),
                                cpu.readRegister(Cpu6502.REG_P),
                                cpu.readRegister(Cpu6502.REG_PC),
                                (int) (memory[cpu.readRegister(Cpu6502.REG_PC)]
                                          & BYTE_MASK));
                Log.getLog().info(msg);
                firstError = false;
            }
        }
        if (pass == NUM_OF_STEP_TESTS) {
            Log.getLog().info("STEP OK (pass " + NUM_OF_STEP_TESTS + " tests");
        } else {
            Log.getLog().error("STEP NG (pass " + pass + "/"
                    + NUM_OF_STEP_TESTS + " tests)");
        }
        return true;
    }

    /**
     * Generate random bit value.
     * @return generated random bit value
     */
    private int generateRandomBit() {
        int v = seed & SEED_UPDATE_MASK;
        v ^= (v >> SEED_UPDATE_RSHIFT);
        seed = ((seed >> 1) | (v << SEED_UPDATE_LSHIFT)) & SHORT_MASK;
        return seed & 1;
    }

    /**
     * Generate random byte value.
     * @return generated random byte value
     */
    private char generateRandomByte() {
        char result = 0;
        for (int i = 0; i < BYTE_BITS; i++) {
            result = (char) ((result << 1) | generateRandomBit());
        }
        return result;
    }

    /**
     * Write 8-bit data to addressed memory.
     * @param address memory address to write
     * @param data data to write
     */
    public void writeChar(final int address, final char data) {
        memory[address] = data;
        //Log.getLog().warn(String.format("%04x := %02x", address, (int) data));
        if (traceError) {
            return;
        }
        if ((EXP_STEP_MEMS[traceIndex][ADDRESS] != address)
                || (EXP_STEP_MEMS[traceIndex][DATA] != (data & BYTE_MASK))) {
            traceError = true;
            traceAddress = address;
            traceData = data;
        } else {
            traceScore++;
        }
        if (EXP_STEP_MEMS[traceIndex][DATA] != END_OF_DATA) {
            traceIndex++;
        }
    }

    /**
     * Read 8-bit data from addressed memory.
     * @param address memory address to read
     * @return read data
     */
    public char readChar(final int address) {
        return memory[address];
    }

    /**
     * Main to run simple test.
     * @param args arguments (not used)
     */
    public static void main(final String[] args) {
        Log.setLog(new J2SELog());
        Test6502 test = new Test6502();
        test.step();
    }
}
