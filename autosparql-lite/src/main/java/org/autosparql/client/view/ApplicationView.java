package org.autosparql.client.view;

import java.util.List;

import org.autosparql.client.AppEvents;
import org.autosparql.client.widget.SearchResultPanel;
import org.autosparql.shared.Example;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.RootPanel;

public class ApplicationView extends View {


	private Viewport viewport;
	//	private InputPanel north;
	private SearchResultPanel center = null;

	public ApplicationView(Controller controller)
	{
		super(controller);
	}

	protected void initialize()	
	{
		super.initialize();
	}

	public void display(List<Example> examples)
	{
		if(examples==null||examples.isEmpty())
		{
			//if(RootPanel.get("gwt-table")!=null) {RootPanel.get("gwt-table").clear();}
			return;
		}
		System.out.println("displaying "+examples);
		if(center==null) {createCenter();RootPanel.get("gwt-table").add(viewport);}
		//		for(Example example: examples)
		//		center.gridStore.add(example);
		center.setResult(examples);
		//Window.alert("display");
	}

	private void initUI()
	{
		viewport = new Viewport();
		viewport.setLayout(new BorderLayout());

		//String query = com.google.gwt.user.client.Window.Location.getParameter("query");
		//        if(query==null||query.isEmpty())
		//        {
		//        	viewport.add(new Label("no query asked"));
		//        }
		//        else
		//        {
		//        	
		//    		createCenter();        	
		//        }
		//		createNorth();

		//RootPanel.get("gwt-table").add(viewport);
	}

	//	private void createNorth() {
	//		north = new InputPanel();
	//		viewport.add(north, new BorderLayoutData(LayoutRegion.NORTH));
	//	}

	private void createCenter()
	{
		center = new SearchResultPanel();
		//viewport.add(center.grid, new BorderLayoutData(LayoutRegion.CENTER));
		viewport.add(center, new BorderLayoutData(LayoutRegion.CENTER));
		HorizontalPanel buttonPanel = new HorizontalPanel();
		viewport.add(buttonPanel, new BorderLayoutData(LayoutRegion.SOUTH));
		Button learnButton = new Button("learn");
		buttonPanel.add(learnButton);
		learnButton.addSelectionListener(new SelectionListener<ButtonEvent>(
				)
				{		
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				center.learn();
			}
				});
	}

	protected void handleEvent(AppEvent event) {
		if (event.getType() == AppEvents.Init) {
			initUI();
		}
	}

	public void showError(String string)
	{

	}

}