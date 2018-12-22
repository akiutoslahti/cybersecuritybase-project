package sec.project.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PlainTextPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence cs) {
        return cs.toString();
    }

    @Override
    public boolean matches(CharSequence cs, String string) {
        return string.equals(cs);
    }

}
