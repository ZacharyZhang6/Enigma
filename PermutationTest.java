package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class. For the purposes of
 * this lab (in order to test) this is an abstract class, but in proj1, it will
 * be a concrete class. If you want to copy your tests for proj1, you can make
 * this class concrete by removing the 4 abstract keywords and implementing the
 * 3 abstract methods.
 *
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        Alphabet alpha = new Alphabet();
        Permutation perm = new Permutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(ZACH) (B)", new Alphabet("ABCHZ"));
        assertEquals('A', p.invert('C'));
        assertEquals('B', p.invert('B'));
        assertEquals('H', p.invert('Z'));
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(ZACH) (B)", new Alphabet("ABCHZ"));
        assertEquals('A', p.permute('Z'));
        assertEquals('B', p.permute('B'));
        assertEquals('Z', p.permute('H'));
    }

    @Test
    public void testSize() {
        Permutation p = new Permutation("(ZACH) (B)", new Alphabet("ABCHZ"));
        assertEquals(5, p.size());
    }

    @Test
    public void testPermuteInt() {
        Permutation p = new Permutation("(ZACH) (B)", new Alphabet("ABCHZ"));
        assertEquals(4, p.permute(3));
        assertEquals(0, p.permute(4));
        assertEquals(1, p.permute(1));
    }

    @Test
    public void testInvertInt() {
        Permutation p = new Permutation("(ZACH) (B)", new Alphabet("ABCHZ"));
        assertEquals(3, p.invert(4));
        assertEquals(4, p.invert(0));
        assertEquals(1, p.invert(1));
    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(ZACH) (B)", new Alphabet("ABCHZ"));
        assertFalse(p.derangement());
        Permutation a = new Permutation("(ZACH)", new Alphabet("ACHZ"));
        assertTrue(a.derangement());
    }

    @Test
    public void testAlphabet() {
        Alphabet b = new Alphabet("ABCHZ");
        Permutation p = new Permutation("(ZACH) (B)", b);
        assertEquals(b, p.alphabet());
    }
}
