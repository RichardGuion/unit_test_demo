
// A singleton class to do Test Logging - I like to do a lot of logging for tests
// in order to analyze test failures.
public class TestLogger {

    // Our single instance of TestLogger
    private static TestLogger instance = new TestLogger();

    private TestLogger() {
    }

    public static TestLogger getTestLogger() {
        return instance;
    }

    public void logTestOperation(String testOperation) {
        System.out.println(String.format(testOperation));
    }
}
