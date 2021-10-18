package enigma;



import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Zachary Zhang
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        String next = _input.nextLine();
        if (next.charAt(0) != '*') {
            throw new EnigmaException("Not start with *");
        }
        setUp(enigma, next);
        while (_input.hasNextLine()) {
            String nextLine = _input.nextLine();
            if (nextLine.isEmpty()) {
                _output.println();
                continue;
            }
            if (nextLine.contains("*")) {
                enigma.clearRotor();
                setUp(enigma, nextLine);

                continue;
            }
            printMessageLine(enigma.convert(nextLine));
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            ArrayList<Rotor> allRotors = new ArrayList<>();
            String temp = _config.nextLine();
            if (temp.contains("(") || temp.contains("*")
                    || temp.contains(")")) {
                throw new EnigmaException("Alphabet incorrect");
            }
            _alphabet = new Alphabet(temp);
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            while (_config.hasNext()) {
                _name = _config.next();
                type = _config.next();
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Rotor rot;
            String temp = "";
            permut = _config.next();
            while (_config.hasNext(".*[\\(|\\)]+.*")) {
                if (permut.contains(")(")) {
                    String alter = permut.replace(")(", " ");
                    alter = alter.replace("(", "");
                    alter = alter.replace(")", "");
                    String[] templist = alter.split(" ");

                    for (String s : templist) {
                        temp += ("(" + s + ")" + " ");
                    }
                } else {
                    temp += (permut + " ");
                }
                permut = _config.next();
            }

            if (permut.contains("(")) {
                temp += (permut + " ");
            }

            if (!_config.hasNext()) {
                temp += (permut + " ");
            }
            Permutation permute = new Permutation(temp, _alphabet);
            if (type.charAt(0) == 'M') {
                rot = new MovingRotor(_name, permute, type.substring(1));
            } else if (type.charAt(0) == 'N') {
                rot = new FixedRotor(_name, permute);
            } else {
                rot = new Reflector(_name, permute);
            }
            return rot;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String [] setting = settings.split(" ");
        String [] newSetting = new String[M.numRotors()];
        if (setting.length < M.numRotors()) {
            throw new EnigmaException("Setting size shorter");
        }
        for (int i = 0; i < M.numRotors(); i++) {
            newSetting[i] = setting[i + 1];
        }
        M.insertRotors(newSetting);
        String reflector = "";
        for (int i = M.numRotors() + 2; i < setting.length; i++) {
            reflector += (setting[i] + " ");
        }
        Permutation permute1 = new Permutation(reflector, _alphabet);
        M.setRotors(setting[M.numRotors() + 1]);
        M.setPlugboard(permute1);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg.isEmpty()) {
            System.out.println();
        }
        for (int i = 0; i < msg.length(); i++) {
            if (i > 0 && i % 5 == 0) {
                _output.print(" ");
            }
            if (i == msg.length() - 1) {
                _output.println(msg.charAt(i));
            } else {
                _output.print(msg.charAt(i));
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Name of Rotor. */
    private String _name;

    /** Type of Rotor. */
    private String type;

    /** Permutation of Rotor. */
    private String permut;
}
