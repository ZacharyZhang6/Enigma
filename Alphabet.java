package enigma;

import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Zachary Zhang
 */
class Alphabet {

    /** String of character. */
    private String _chars;

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        int i = 0;
        while (i < size()) {
            if (_chars.charAt(i) == ch) {
                return true;
            }
            i += 1;
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= size() || index < 0) {
            throw new EnigmaException("index out of range");
        }
        return _chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int i = 0;
        while (i < size()) {
            if (_chars.charAt(i) == ch) {
                return i;
            }
            i += 1;
        }
        throw new EnigmaException("character not in the alphabet");
    }
}
