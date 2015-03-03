package fr.an.util.calllogger.format;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;
import fr.an.shared.util.calllogger.CallMsgInfo;
import fr.an.shared.util.calllogger.util.format.CallInfoStackFormat;

public class TestStackFormat extends TestCase {

    public TestStackFormat(String name) {
        super(name);
    }

    public void test1() {
        int nbCall = 10;
        CallMsgInfo callInfo = buildCallStack(nbCall);

        CallInfoStackFormat fmt = new CallInfoStackFormat();
        String text = doFormat(callInfo, fmt);
        String checkText = "cRoot / " + buildMessageCall(1, nbCall);
        assertEquals(text, checkText);
    }

    /**
     * internal utility for CallInfoStackFormat
     * @param callInfo
     * @param fmt
     * @return
     */
    private String doFormat(CallMsgInfo callInfo, CallInfoStackFormat fmt) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintWriter printer = new PrintWriter(bout);
        fmt.format(printer, callInfo);
        printer.flush();
        String text = bout.toString();
        return text;
    }

    /** internal utility to build a stack of CallMsgInfo */
    private static CallMsgInfo buildCallStack(int nbCall) {
        CallMsgInfo cRoot = new CallMsgInfo();
        cRoot.setPre("cRoot", new Object[0]);

        CallMsgInfo[] stack = new CallMsgInfo[nbCall];
        stack[0] = cRoot;
        for (int i = 1; i < nbCall; i++) {
            CallMsgInfo ci = new CallMsgInfo();
            stack[i] = ci;
            ci.setPre("c" + i, new Object[0]);
            ci.setParent(stack[i - 1]);
        }
        return stack[nbCall - 1];
    }

    /** internal 
     * @return text "c<fromIndex> / c<fromIndex+1> .. / c<toIndex>" 
     */
    private static String buildMessageCall(int fromIndex, int toIndex) {
        StringBuffer sb = new StringBuffer();
        for (int i = fromIndex; i < toIndex; i++) {
            sb.append("c" + i);
            if (i + 1 < toIndex)
                sb.append(" / ");
        }
        return sb.toString();
    }

}
