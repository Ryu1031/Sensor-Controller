package utils;

public class Log {
    public static final boolean DEBUG = Contacts.DEBUG_ENABLE;
    public static final String TAG = Log.class.getSimpleName();

    public static void v() {
        if (DEBUG) {
            android.util.Log.v(TAG, getMetaInfo());
        }
    }

    public static void v(String message) {
        if (DEBUG) {
            android.util.Log.v(TAG, getMetaInfo() + null2str(message));
        }
    }

    public static void d() {
        if (DEBUG) {
            android.util.Log.d(TAG, getMetaInfo());
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            android.util.Log.d(TAG, getMetaInfo() + null2str(message));
        }
    }

    public static void i() {
        if (DEBUG) {
            android.util.Log.i(TAG, getMetaInfo());
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            android.util.Log.i(TAG, getMetaInfo() + null2str(message));
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            android.util.Log.w(TAG, getMetaInfo() + null2str(message));
        }
    }

    public static void w(String message, Throwable e) {
        if (DEBUG) {
            android.util.Log.w(TAG, getMetaInfo() + null2str(message), e);
            printThrowable(e);
            if (e.getCause() != null) {
                printThrowable(e.getCause());
            }
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            android.util.Log.e(TAG, getMetaInfo() + null2str(message));
        }
    }

    public static void e(String message, Throwable e) {
        if (DEBUG) {
            android.util.Log.e(TAG, getMetaInfo() + null2str(message), e);
            printThrowable(e);
            if (e.getCause() != null) {
                printThrowable(e.getCause());
            }
        }
    }

    public static void e(Throwable e) {
        if (DEBUG) {
            printThrowable(e);
            if (e.getCause() != null) {
                printThrowable(e.getCause());
            }
        }
    }

    private static String null2str(String string) {
        if (string == null) {
            return "(null)";
        }
        return string;
    }

    private static void printThrowable(Throwable e) {
        android.util.Log.e(TAG, e.getClass().getName() + ": " + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            android.util.Log.e(TAG, "  at " + Log.getMetaInfo(element));
        }
    }

    private static String getMetaInfo() {
        final StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        return Log.getMetaInfo(element);
    }

    public static String getMetaInfo(StackTraceElement element) {
        final String fullClassName = element.getClassName();
        final String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        final String methodName = element.getMethodName();
        final int lineNumber = element.getLineNumber();
        final String metaInfo = "[" + simpleClassName + "#" + methodName + ":" + lineNumber + "]";
        return metaInfo;
    }

}
