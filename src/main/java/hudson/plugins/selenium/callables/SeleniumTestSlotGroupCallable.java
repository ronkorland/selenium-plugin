package hudson.plugins.selenium.callables;

import hudson.plugins.selenium.RegistryHolder;
import hudson.plugins.selenium.SeleniumTestSlot;
import hudson.plugins.selenium.SeleniumTestSlotGroup;
import hudson.remoting.Callable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSlot;

public class SeleniumTestSlotGroupCallable implements
		Callable<Collection<SeleniumTestSlotGroup>, RuntimeException> {

	private static final long serialVersionUID = 1L;

	public Collection<SeleniumTestSlotGroup> call() throws RuntimeException {
		Map<URL, SeleniumTestSlotGroup> groups = new HashMap<URL, SeleniumTestSlotGroup>();

		Registry registry = RegistryHolder.registry;
		if (registry != null) {
			for (RemoteProxy proxy : registry.getAllProxies()) {
				for (TestSlot slot : proxy.getTestSlots()) {
					URL host = slot.getProxy().getRemoteHost();
					SeleniumTestSlotGroup grp = groups.get(host);
					if (grp == null) {
						String platform = (String) slot.getCapabilities().get(
								"platform");
						grp = new SeleniumTestSlotGroup(host, platform);
						groups.put(host, grp);
					}
					grp.addTestSlot(new SeleniumTestSlot(slot));
				}

			}
		}
		List<SeleniumTestSlotGroup> values = new ArrayList<SeleniumTestSlotGroup>(
				groups.values());
		Collections.sort(values);
		return values;
	}

}
