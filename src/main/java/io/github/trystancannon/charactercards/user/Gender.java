/*
 * The MIT License
 *
 * Copyright 2015 Trystan Cannon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.trystancannon.charactercards.user;

/**
 * @author Trystan Cannon
 */
public class Gender {
    private final boolean isMale;
    
    public Gender(boolean isMale) {
        this.isMale = isMale;
    }
    
    @Override
    public String toString() {
        return isMale ? "Male" : "Female";
    }
    
    /**
     * Parses and creates a <code>Gender</code> object based on the text found
     * in the given string. However, if nothing could be gleaned, <code>null</code>
     * is returned.
     * 
     * @param sex
     * @return Gender object or null, depending on whether or not the given string
     * could be parsed.
     */
    public static Gender parseSex(String sex) {
        if (sex != null) {
            if (sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("man") || sex.equalsIgnoreCase("boy")) {
                return new Gender(true);
            }
            else if (sex.equalsIgnoreCase("female") || sex.equalsIgnoreCase("woman") || sex.equalsIgnoreCase("girl")) {
                return new Gender(false);
            }
        }
        
        return null;
    }
}
