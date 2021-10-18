package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Zachary Zhang
 */
class Permutation {
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String read = cycles;
        read = read.replace("(", "");
        read = read.replace(")", "");
        _cycles = read.split(" ");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String[] permuteCycle = new String[_cycles.length + 1];
        for (int i = 0; i < _cycles.length; i++) {
            permuteCycle[i] = _cycles[i];
        }
        permuteCycle[_cycles.length + 1] = cycle;
        _cycles = permuteCycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char temp = '0';
        char letter = _alphabet.toChar(wrap(p));
        for (int j = 0; j < _cycles.length; j++) {
            for (int i = 0; i < _cycles[j].length(); i++) {
                if (_cycles[j].charAt(i) == letter) {
                    if ((i + 1) == _cycles[j].length()) {
                        temp = _cycles[j].charAt(0);
                    } else {
                        temp = _cycles[j].charAt((i + 1));
                    }
                    return _alphabet.toInt(temp);
                }

            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char temp = '0';
        char letter = _alphabet.toChar(wrap(c));
        for (int j = 0; j < _cycles.length; j++) {
            for (int i = 0; i < _cycles[j].length(); i++) {
                if (_cycles[j].charAt(i) == letter) {
                    if ((i - 1) < 0) {
                        temp = _cycles[j].charAt(_cycles[j].length() - 1);
                    } else {
                        temp = _cycles[j].charAt((i - 1));
                    }
                    return _alphabet.toInt(temp);
                }

            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        return _alphabet.toChar(permute(index));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = _alphabet.toInt(c);
        return _alphabet.toChar(invert(index));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int i = 0;
        while (i < _cycles.length) {
            if (_cycles[i].length() <= 1) {
                return false;
            }
            i += 1;
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** A string list that allows the letter to permute. */
    private String [] _cycles;
}
