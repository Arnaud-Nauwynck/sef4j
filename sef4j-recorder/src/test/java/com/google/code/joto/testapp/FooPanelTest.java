package com.google.code.joto.testapp;

import org.junit.Test;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.JotoContextFacadePanel;
import com.google.code.joto.util.io.ui.UiTestUtils;

public class FooPanelTest {

	public static void main(String[] args) {
		JotoConfig config = new JotoConfig();
		JotoContext context = new JotoContext(config);
		
		IFooService fooServiceImpl = new FooServiceImpl();
		IFooService fooProxy = context.createDefaultMethodEventWriterProxy(fooServiceImpl);

		context.getObjReplMap().addObjectInstanceReplacement(fooServiceImpl, "fooServiceImpl", null);
		context.getObjReplMap().addObjectInstanceReplacement(fooProxy, "fooProxy", null);
		
		FooPanel panel = new FooPanel(fooProxy);
		UiTestUtils.doShowInFrame(panel.getJComponent());
		
		JotoContextFacadePanel jotoFacadePanel = new JotoContextFacadePanel(context); 
		UiTestUtils.doShowInFrame(jotoFacadePanel.getJComponent());
	}
	
	@Test
	public void testOpenFrame() {
		JotoConfig config = new JotoConfig();
		JotoContext context = new JotoContext(config);
		
		IFooService fooServiceImpl = new FooServiceImpl();
		IFooService fooProxy = context.createDefaultMethodEventWriterProxy(fooServiceImpl);

		context.getObjReplMap().addObjectInstanceReplacement(fooServiceImpl, "fooServiceImpl", null);
		context.getObjReplMap().addObjectInstanceReplacement(fooProxy, "fooProxy", null);
		
		FooPanel panel = new FooPanel(fooProxy);
		
		JotoContextFacadePanel jotoFacadePanel = new JotoContextFacadePanel(context); 
		UiTestUtils.showInFrame(panel.getJComponent(), jotoFacadePanel.getJComponent());
	}
	
}
