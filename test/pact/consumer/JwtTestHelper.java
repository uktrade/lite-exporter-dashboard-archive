package pact.consumer;

import com.google.common.collect.ImmutableMap;
import filters.common.JwtRequestFilter;
import play.libs.ws.WSRequestExecutor;

import java.util.Map;

public class JwtTestHelper {
  /*
    {
      "typ": "JWT",
      "alg": "HS256"
    }
    {
      "iss": "Some lite application",
      "exp": 1825508319,
      "jti": "Jv1XdmhlFhrbQhq5QaPgyg",
      "iat": 1510148319,
      "nbf": 1510148199,
      "sub": "123456",
      "email": "example@example.com",
      "fullName": "Mr Test"
    }
    Secret: demo-secret-which-is-very-long-so-as-to-hit-the-byte-requirement
  */
  public static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJTb21lIGxpdGUgYXBwbGljYXRpb24iLCJleHAiOjE4MjU1MDgzMTksImp0aSI6Ikp2MVhkbWhsRmhyYlFocTVRYVBneWciLCJpYXQiOjE1MTAxNDgzMTksIm5iZiI6MTUxMDE0ODE5OSwic3ViIjoiMTIzNDU2IiwiZW1haWwiOiJleGFtcGxlQGV4YW1wbGUuY29tIiwiZnVsbE5hbWUiOiJNciBUZXN0In0.4IUJs49UGBaN_c8Huf1dpYtFbOD43hzrWFvWUmz-_ak";

  public static final String JWT_AUTHORIZATION_HEADER_VALUE = "Bearer " + JWT_TOKEN;

  public static final Map<String, String> JWT_AUTHORIZATION_HEADER = ImmutableMap.of("Authorization", JWT_AUTHORIZATION_HEADER_VALUE);

  private JwtTestHelper() {
  }

  public static class TestJwtRequestFilter extends JwtRequestFilter {
    public TestJwtRequestFilter() {
      super(null, null);
    }

    @Override
    public WSRequestExecutor apply(WSRequestExecutor executor) {
      return request -> {
        request.setHeader("Authorization", "Bearer " + JWT_TOKEN);
        return executor.apply(request);
      };
    }
  }
}
