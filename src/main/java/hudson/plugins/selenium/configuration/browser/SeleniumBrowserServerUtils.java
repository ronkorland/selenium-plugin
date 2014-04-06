/**
 * 
 */
package hudson.plugins.selenium.configuration.browser;

import hudson.FilePath.FileCallable;
import hudson.Functions;
import hudson.model.Computer;
import hudson.remoting.RemoteInputStream;
import hudson.remoting.VirtualChannel;
import hudson.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;

/**
 * @author Richard Lavoie
 * 
 */
public final class SeleniumBrowserServerUtils {

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static String uploadIEDriverIfNecessary(Computer computer,
			String server_binary) {
		String server_path = null;
		if (StringUtils.isBlank(server_binary)) {
			try {
				Boolean isWin64bit = isWin64bit(computer);

				if (isWin64bit != null) {
					URL url = SeleniumBrowserServerUtils.class.getClassLoader()
							.getResource(
									"IEDriverServer_"
											+ (isWin64bit ? "64" : "32")
											+ ".exe");
					final InputStream is = new RemoteInputStream(
							url.openStream());
					server_path = computer.getNode().getRootPath()
							.act(new FileCallable<String>() {

								private static final long serialVersionUID = 4508849758404950847L;

								public String invoke(File f,
										VirtualChannel channel)
										throws IOException,
										InterruptedException {
									File out = new File(f, "IEDriverServer.exe");
									if (out.exists()) {
										out.delete();
									}
									IOUtils.copy(is, out);
									return out.getAbsolutePath();
								}
							});
				}

			} catch (Exception e) {
				server_path = server_binary;
			}
		} else {
			server_path = server_binary;
		}
		return server_path;
	}

	public static String uploadChromeDriverIfNecessary(Computer computer,
			String server_binary) {
		String server_path = null;
		if (StringUtils.isBlank(server_binary)) {
			try {

				URL url = SeleniumBrowserServerUtils.class.getClassLoader()
						.getResource(getChromeDriverName());
				final InputStream is = new RemoteInputStream(url.openStream());
				server_path = computer.getNode().getRootPath()
						.act(new FileCallable<String>() {

							private static final long serialVersionUID = 4508849758404950847L;

							public String invoke(File f, VirtualChannel channel)
									throws IOException, InterruptedException {
								File out = new File(f, getChromeDriverName());
								if (out.exists()) {
									out.delete();
								}
								IOUtils.copy(is, out);
								return out.getAbsolutePath();
							}
						});

			} catch (Exception e) {
				server_path = server_binary;
			}
		} else {
			server_path = server_binary;
		}
		return server_path;
	}

	private static String getChromeDriverName() {
		Platform current = Platform.getCurrent();

		switch (current) {
		case ANDROID:
		case ANY:
			return null;
		case MAC:
			return "chromedriver_mac32";
		case LINUX:
		case UNIX:
			return "chromedriver_linux32";
		case VISTA:
		case WIN8:
		case WINDOWS:
		case XP:
			return "chromedriver_win32.exe";
		}
		return null;
	}

	private static Boolean isWin64bit(Computer computer) throws IOException,
			InterruptedException {
		Boolean isWin64bit = computer.getNode().getRootPath()
				.act(new FileCallable<Boolean>() {

					private static final long serialVersionUID = -726600253548951419L;

					public Boolean invoke(File f, VirtualChannel channel)
							throws IOException, InterruptedException {
						if (!Functions.isWindows()) {
							return null;
						}
						Process p = Runtime
								.getRuntime()
								.exec("cmd /c if defined ProgramFiles(x86) ( exit 1 ) else ( exit 0 )");
						int exitValue = p.waitFor();

						return exitValue == 1;
					}
				});
		return isWin64bit;
	}

}
