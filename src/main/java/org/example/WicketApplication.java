package org.example;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;

public class WicketApplication extends WebApplication {
	@Override
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	@Override
	public void init() {
		super.init();
	}
}
