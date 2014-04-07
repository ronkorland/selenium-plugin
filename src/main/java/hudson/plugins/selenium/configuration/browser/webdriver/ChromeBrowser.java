package hudson.plugins.selenium.configuration.browser.webdriver;

import hudson.Extension;
import hudson.model.Computer;
import hudson.plugins.selenium.configuration.browser.SeleniumBrowserServerUtils;
import hudson.plugins.selenium.process.SeleniumRunOptions;
import hudson.util.FormValidation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class ChromeBrowser extends ServerRequiredWebDriverBrowser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8505665387429684157L;

	/**
	 * System property to specify the chrome binary location. Could be done
	 * through a tool installer and probably moved into the chromedriver plugin.
	 */
	transient final protected String PARAM_BINARY_PATH = "webdriver.chrome.driver";

	@DataBoundConstructor
	public ChromeBrowser(int maxInstances, String version, String server_binary) {
		super(maxInstances, version, "chrome", server_binary);
	}

	@Override
	public Map<String, String> getJVMArgs() {
		Map<String, String> args = new HashMap<String, String>();
		combine(args, PARAM_BINARY_PATH, getServerBinary());
		return args;
	}

	@Override
	public void initOptions(Computer c, SeleniumRunOptions opt) {
		String server_path = SeleniumBrowserServerUtils
				.uploadChromeDriverIfNecessary(c, getServerBinary());
		if (server_path != null) {
			opt.getJVMArguments().put(PARAM_BINARY_PATH, server_path);
		}
		opt.addOptionIfSet("-browser",
				StringUtils.join(initBrowserOptions(c, opt), ","));
	}

	@Extension
	public static class DescriptorImpl extends WebDriverBrowserDescriptor {

		public String getMaxInstances() {
			return "5";
		}

		@Override
		public String getDisplayName() {
			return "Chrome";
		}

	}
}
