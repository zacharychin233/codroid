package org.codroid.editor.buffer.linearr

import org.junit.Assert.assertEquals
import org.junit.Test

class LineArrayTest {

    private val mLineArray = LineArray(
        this.javaClass.classLoader!!.getResourceAsStream("AddonException")
    )

    @Test
    fun rowAt() {
        assertEquals("package org.codroid.interfaces.exceptions;", mLineArray.rowAt(0))
        assertEquals("}", mLineArray.rowAt(24))
        assertEquals("", mLineArray.rowAt(25))
    }


    @Test
    fun insert() {
        mLineArray.insert("Hello", 1, 0)
        assertEquals("Hello", mLineArray.rowAt(1))
        assertEquals(675, mLineArray.length())
        mLineArray.insert("World", 1, 5)
        assertEquals("HelloWorld", mLineArray.rowAt(1))
        assertEquals(680, mLineArray.length())

        mLineArray.insert("LINE", 42)
        assertEquals("package org.codroid.interfaces.exceptions;LINE", mLineArray.rowAt(0))
        assertEquals(684, mLineArray.length())
        mLineArray.insert("NEW", 47)
        assertEquals("NEWHelloWorld", mLineArray.rowAt(1))
        assertEquals(687, mLineArray.length())

        mLineArray.insert("END", mLineArray.length())
        assertEquals("END", mLineArray.rowAt(mLineArray.rows() - 1))
        assertEquals(690, mLineArray.length())
    }

    @Test
    fun insertionWithNewLines() {
        mLineArray.insert("Line1\nLine2", 0)
        assertEquals(27, mLineArray.rows())
        assertEquals(681, mLineArray.length())
        assertEquals("Line1", mLineArray.rowAt(0))
        assertEquals("Line2package org.codroid.interfaces.exceptions;", mLineArray.rowAt(1))

        mLineArray.insert("Line3\nLine4\nLine5\nLine6", mLineArray.length())
        assertEquals(30, mLineArray.rows())
        assertEquals(704, mLineArray.length())
        assertEquals("Line6", mLineArray.rowAt(29))
        assertEquals("Line5", mLineArray.rowAt(28))
        assertEquals("Line4", mLineArray.rowAt(27))
        assertEquals("Line3", mLineArray.rowAt(26))
    }

    @Test
    fun delete() {
        mLineArray.delete(0, 42)
        assertEquals(26, mLineArray.rows())
        assertEquals(628, mLineArray.length())
        assertEquals("", mLineArray.rowAt(0))

        mLineArray.delete(0, 1)
        assertEquals(25, mLineArray.rows())
        assertEquals(627, mLineArray.length())

        mLineArray.delete(0, 2)
        assertEquals(23, mLineArray.rows())
        assertEquals(625, mLineArray.length())
        assertEquals("import org.codroid.interfaces.log.Logger;", mLineArray.rowAt(0))

        mLineArray.delete(33, 58)
        assertEquals(20, mLineArray.rows())
        assertEquals(600, mLineArray.length())
        assertEquals(
            "import org.codroid.interfaces.loga superclass that should only be inherited by the exceptions about addon.",
            mLineArray.rowAt(0)
        )
    }


    @Test
    fun replace() {
        mLineArray.replace("Hello ", 0, 0)
        assertEquals("Hello package org.codroid.interfaces.exceptions;", mLineArray.rowAt(0))
        assertEquals(26, mLineArray.rows())
        assertEquals(676, mLineArray.length())

        mLineArray.replace("LINE", 49, 49)
        assertEquals("LINE", mLineArray.rowAt(1))
        assertEquals(26, mLineArray.rows())
        assertEquals(680, mLineArray.length())

        mLineArray.replace("Line2\nLine3\nLine4", 55, 61)
        assertEquals(28, mLineArray.rows())
        assertEquals(691, mLineArray.length())
        assertEquals("Line2", mLineArray.rowAt(3))
        assertEquals("Line3", mLineArray.rowAt(4))
        assertEquals("Line4 org.codroid.interfaces.log.Logger;", mLineArray.rowAt(5))

        mLineArray.replace("RUIN", 0, mLineArray.length())
        assertEquals(1, mLineArray.rows())
        assertEquals(4, mLineArray.length())
    }


    @Test
    fun length() {
        assertEquals(670, mLineArray.length())
    }

    @Test
    fun rows() {
        assertEquals(26, mLineArray.rows())
    }

    @Test
    fun testToString() {
        assertEquals(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */\n" +
                    "public class AddonException extends Exception {\n" +
                    "\n" +
                    "    /**\n" +
                    "     * Print the stack trace by addon logger.\n" +
                    "     *\n" +
                    "     * @param logger which logger you want to use.\n" +
                    "     */\n" +
                    "    public void printStackTrace(Logger logger) {\n" +
                    "        StringBuilder builder = new StringBuilder();\n" +
                    "        builder.append(this.toString());\n" +
                    "        for (var i : getStackTrace()) {\n" +
                    "            builder.append(\"\\n\\tat \");\n" +
                    "            builder.append(i.toString());\n" +
                    "        }\n" +
                    "        logger.e(builder.toString());\n" +
                    "    }\n" +
                    "}\n", mLineArray.toString()
        )
    }

    @Test
    fun constructor2() {
        val seq = LineArray(
            "package org.codroid.interfaces.exceptions;\n" +
                    "\n" +
                    "\n" +
                    "import org.codroid.interfaces.log.Logger;\n" +
                    "\n" +
                    "/**\n" +
                    " * This is a superclass that should only be inherited by the exceptions about addon.\n" +
                    " */"
        )

        seq.delete(1, 0)
        assertEquals(8, seq.rows())
        assertEquals(43 + 2 + 43 + 4 + 85 + 3, seq.length())
        assertEquals("package org.codroid.interfaces.exceptions;", seq.rowAt(0))
    }
}