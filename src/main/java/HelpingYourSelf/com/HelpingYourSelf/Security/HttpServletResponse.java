package HelpingYourSelf.com.HelpingYourSelf.Security;

public interface HttpServletResponse {
    int SC_UNAUTHORIZED = 401;
    void sendError(int sc, String msg) throws java.io.IOException;
}
