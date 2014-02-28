package com.github.sevntu.checkstyle.checks.coding;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class ForbidPrivateTypesInMethodsCheckTest extends BaseCheckTestSupport {

    private final DefaultConfiguration checkConfig = createCheckConfig(ForbidPrivateTypesInMethodsCheck.class);
    
    private final String METHOD_RETURN = ForbidPrivateTypesInMethodsCheck.METHOD_RETURN;
    private final String METHOD_RETURN_SUGGEST = ForbidPrivateTypesInMethodsCheck.METHOD_RETURN_SUGGEST;
    private final String METHOD_PARAMETERS = ForbidPrivateTypesInMethodsCheck.METHOD_PARAMETRS;

    @Test
    public final void testFile1() throws Exception {
        String[] expected = {
            getCompletedMessage(6, "C1", METHOD_PARAMETERS),
            getCompletedMessage(10, "C1", METHOD_RETURN)
        };

        verify(checkConfig, getPath("InputForbidPrivateTypesInMethodsCheck.java"), expected);
    }
    
    @Test
    public final void testFile2() throws Exception {
        String[] expected = {
            getCompletedMessage( 7, "C1", METHOD_PARAMETERS),
            getCompletedMessage(11, "C1", METHOD_RETURN),
            getCompletedMessage(15, "C2", METHOD_PARAMETERS),
            getCompletedMessage(19, "C2", METHOD_RETURN),
            getCompletedMessage(23, "C3", METHOD_PARAMETERS),
            getCompletedMessage(27, "C3", "Exception", METHOD_RETURN_SUGGEST),
        };

        verify(checkConfig, getPath("InputForbidPrivateTypesInMethodsCheck2.java"), expected);
    }
    
    @Test
    public final void testFile3() throws Exception {
        String[] expected = {
            getCompletedMessage( 9, "C1", METHOD_PARAMETERS),
            getCompletedMessage(13, "C1", "Serializable", METHOD_RETURN_SUGGEST),
            getCompletedMessage(17, "C2", METHOD_PARAMETERS),
            getCompletedMessage(21, "C2", "Serializable", METHOD_RETURN_SUGGEST),
            getCompletedMessage(25, "C3", METHOD_PARAMETERS),
            getCompletedMessage(29, "C3", "Exception", METHOD_RETURN_SUGGEST),
        };

        verify(checkConfig, getPath("InputForbidPrivateTypesInMethodsCheck3.java"), expected);
    }
    
    private String getCompletedMessage(int line, String str, String msg) {
        return line + ": " + getCheckMessage(msg).replace("{0}", str);
    }
    
    private String getCompletedMessage(int line, String str1, String str2, String msg) {
        return getCompletedMessage(line, str1, msg).replace("{1}", str2);
    }
}