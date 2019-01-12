package employee;


import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Session
{
    public static final String SESSION_COOKIE_NAME = "session";
    final int SESSION_TOKEN_LEN = 120;
    RandomString rndStr = new RandomString(SESSION_TOKEN_LEN);

    private static Session instance = null;

    HashMap<String, Long> sessions = new HashMap<>();

    private Session()
    {

    }

    public static Session getInstance()
    {
        if(instance == null)
            instance = new Session();


        return instance;
    }

    public String newSessionToken(long id)
    {
        String token = "";
        do
        {
            token = rndStr.nextString();
        } while(sessions.containsKey(token));


        sessions.put(token, id);

        return token;
    }

    public boolean isSessionExists(String token)
    {
        return sessions.containsKey(token);
    }

    public void removeSession(String token)
    {
        sessions.remove(token);
    }

    public long userIdFromToken(String token)
    {
        if(!sessions.containsKey(token))
            return -1;

        return sessions.get(token);
    }

    public class RandomString
    {

        public String nextString()
        {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }


        private final Random random;

        private final char[] symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

        private final char[] buf;

        public RandomString(int length, Random random)
        {
            if (length < 1) throw new IllegalArgumentException();
            this.random = Objects.requireNonNull(random);
            this.buf = new char[length];
        }

        /**
         * Create an alphanumeric strings from a secure generator.
         */
        public RandomString(int length) {
            this(length, new SecureRandom());
        }

        /**
         * Create session identifiers.
         */
        public RandomString() {
            this(21);
        }

    }


}
