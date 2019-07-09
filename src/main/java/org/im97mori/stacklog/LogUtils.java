package org.im97mori.stacklog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

/**
 * output clickable log for Eclipse console and Android Studio Logcat
 */
public class LogUtils {

	/**
	 * Writer for Android
	 */
	private static class AndroidWriter extends StringWriter {

		/**
		 * {@link android.util.Log#d(String, String)} method
		 */
		protected final Method mMethod;

		/**
		 * Constructor
		 * 
		 * @param method {@link android.util.Log#d(String, String)} method
		 */
		private AndroidWriter(Method method) {
			super();
			mMethod = method;
		}
	}

	/**
	 * {@link Writer} instance for log output
	 */
	private static final PrintWriter PRINT_WRITER;

	/**
	 * clicable log format for Eclipse console or Android Studio Logcat
	 */
	private static final String FORMAT;

	static {

		// find android.util.Log.d(String, String) method
		Method method = null;
		try {
			Class<?> clazz = Class.forName("android.util.Log");
			method = clazz.getMethod("d", String.class, String.class);
		} catch (Exception e) {
			// do nothing.
		}

		// method found
		if (method == null) {
			// Java(Eclipse)

			// write to System.out
			PRINT_WRITER = new PrintWriter(System.out);
			FORMAT = "%1$s (%2$s:%3$s)%5$s\n";
		} else {
			// Android(Android Studio)

			// write to android.util.Log.d()
			PRINT_WRITER = new PrintWriter(new AndroidWriter(method) {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void flush() {
					synchronized (lock) {
						StringBuffer sb = getBuffer();
						try {
							((Method) mMethod).invoke(null, "stackLog", sb.toString());
						} catch (Exception e) {
							// do nothing.
						}
						sb.setLength(0);

						lock.notifyAll();
					}
				}

			}, false) {

			};
			FORMAT = "%1$s.%4$s(%2$s:%3$s)%5$s";
		}
	}

	/**
	 * @see #stackLogWithOffset(int, Object...)
	 */
	public static void stackLog(Object... args) {
		stackLogWithOffset(1, args);
	}

	/**
	 * Output clickable log
	 * 
	 * @param offset offset of stacktrace index
	 * @param args   additonal data for log
	 */
	public static void stackLogWithOffset(int offset, Object... args) {
		// default stacktrace index is next of this method
		int index = offset + 1;

		// find this method index
		StackTraceElement[] stackTraceElementArray = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stackTraceElementArray.length; i++) {
			StackTraceElement stackTraceElement = stackTraceElementArray[i];
			if (LogUtils.class.getName().equals(stackTraceElement.getClassName())
					&& "stackLogWithOffset".equals(stackTraceElement.getMethodName())) {
				index += i;
				break;
			}
		}

		if (index >= 0 && index < stackTraceElementArray.length) {
			StackTraceElement stackTraceElement = stackTraceElementArray[index];

			String arg5;
			if (args.length == 0) {
				arg5 = "";
			} else {
				arg5 = "\n\t" + Arrays.deepToString(args);
			}

			//@formatter:off
			PRINT_WRITER.printf(Locale.US, FORMAT
					, stackTraceElement.getClassName()
					, stackTraceElement.getFileName()
					, stackTraceElement.getLineNumber()
					, stackTraceElement.getMethodName()
					, arg5)
			.flush();
			//@formatter:on

		}
	}
}
