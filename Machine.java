package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Zachary Zhang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotorsList = new ArrayList<Rotor>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (String rotor: rotors) {
            for (Rotor r : _rotorsList) {
                if (r.name().equals(rotor)) {
                    throw new EnigmaException("duplicate name");
                }
            }


            boolean haveSameName = false;
            for (Rotor temp: _allRotors) {
                if (rotor.equals(temp.name())) {
                    _rotorsList.add(temp);
                    haveSameName = true;
                }
            }

            if (!haveSameName) {
                throw new EnigmaException("bad name");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Wrong length");
        }
        for (int i = 0; i < setting.length(); i++) {
            char temp = setting.charAt(i);
            if (!_alphabet.contains(temp)) {
                throw error("character not in the alphabet");
            }
            _rotorsList.get(i + 1).set(temp);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */

    int convert(int c) {

        boolean[] moveable = new boolean[_numRotors];
        moveable[_numRotors - 1] = true;
        for (int i = 0; i < _numRotors - 1; i++) {
            if (_rotorsList.get(i + 1).atNotch()
                    && _rotorsList.get(i).rotates()) {
                moveable[i] = true;
                moveable[i + 1] = true;
            }
        }
        for (int i = 0; i < _numRotors; i++) {
            if (moveable[i]) {
                _rotorsList.get(i).advance();
            }
        }
        int convert = _plugboard.permute(c);
        for (int j = _numRotors - 1; j >= 0; j--) {
            convert = _rotorsList.get(j).convertForward(convert);
        }
        for (int j = 1; j < _numRotors; j++) {
            convert = _rotorsList.get(j).convertBackward(convert);
        }
        convert = _plugboard.invert(convert);
        return convert;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String update = "";
        msg = msg.replace(" ", "");
        for (int i = 0; i < msg.length(); i++) {
            if (_alphabet.contains(msg.charAt(i))) {
                update += _alphabet.toChar
                        (convert(_alphabet.toInt(msg.charAt(i))));
            } else {
                throw new EnigmaException("char not in alphabet");
            }
        }
        return update;
    }

    /** Clear rotor information. */
    public void clearRotor() {
        _rotorsList = new ArrayList<Rotor>();

    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls. */
    private int _pawls;

    /** Collection of rotors. */
    private Collection<Rotor> _allRotors;

    /** List of rotors. */
    private ArrayList<Rotor> _rotorsList;

    /** Permutation of the rotors. */
    private Permutation _plugboard;
}
