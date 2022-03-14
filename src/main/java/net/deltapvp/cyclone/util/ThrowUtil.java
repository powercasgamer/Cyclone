/*
 * This file is part of Cyclone, licensed under the MIT License.
 *
 *  Copyright (c) 2022-122 powercas_gamer
 *  Copyright (c) 2022-122 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.deltapvp.cyclone.util;

public class ThrowUtil {

    /**
     * Checks whether or not a CharSequence is null or empty
     * 
     * @param sequence the sequence to check
     */
    public static void checkNullOrEmpty(CharSequence sequence) {
        if (sequence == null) {
            throw new NullPointerException("charsequence cannot be null");
        } else if (sequence.length() == 0) {
            throw new IllegalStateException("charsequence cannot be be empty");
        }
    }

    /**
     * Checks whether or not a Number is null or negative
     * 
     * @param number the number to check
     */
    public static void checkNullOrNegative(Number number) {
        if (number == null) {
            throw new NullPointerException("number cannot be null");
        } else if (number.intValue() < 0) {
            throw new IllegalStateException("number cannot be be negative");
        }
    }
}
