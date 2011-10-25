package org.autosparql.client.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.autosparql.client.Application;
import org.autosparql.client.AutoSPARQLService;
import org.autosparql.client.AutoSPARQLServiceAsync;
import org.autosparql.client.Transformer;
import org.autosparql.shared.Example;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.FastSet;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SearchResultPanel extends ContentPanel
{
	AutoSPARQLServiceAsync service = AutoSPARQLService.Util.getInstance();
	private static final Set defaultProperties = defaultProperties();

	private static Set defaultProperties()
	{
		Set defaultProperties = new FastSet();
		for(String s : new String[] {"label","imageURL","comment","uri"}) {defaultProperties.add(s);}
		return defaultProperties;
	}

	private static final boolean HIGHLIGHT_POSITIVES = true;

	public Grid<Example> grid = null;
	ListStore<Example> gridStore = null;

	private final FastSet positives = new FastSet();
	private final FastSet negatives = new FastSet();

	private final PagingToolBar toolbar;
	private Button relearnButton = new Button("relearn");

	private SearchResultPanelSelectionListener listener = new SearchResultPanelSelectionListener();

	class SearchResultPanelSelectionListener extends SelectionListener<ButtonEvent>
	{
		@Override
		public void componentSelected(ButtonEvent ce)
		{
			if(ce.getSource()==relearnButton)
			{
				relearn();
			}
		}		
	}

	void relearn()
	{

	}
	
	public SearchResultPanel()
	{
		setHeading("Result");
		setCollapsible(false);
		//setLayout(new FitLayout());
		setLayout(new RowLayout(Orientation.VERTICAL));

		//setBottomComponent(toolbar);

		relearnButton.addSelectionListener(listener);
		addButton(relearnButton );

		setBottomComponent(new Button("bottom"));

		toolbar = new PagingToolBar(10);
		setTopComponent(toolbar);

		//grid.setAutoHeight(true);
	}

	public void markPositive(Example e)
	{
		negatives.remove(e);
		grid.getView().refresh(true);
		positives.add(e.getURI());
		updateRowStyle();
	}

	public void markNegative(Example e)
	{
		positives.remove(e);
		negatives.add(e.getURI());
		if(gridStore!=null) {gridStore.remove(e);}
		//Window.alert("removing "+e);
	}

	private List<ColumnConfig> columnConfigs(List<Example> examples)
	{
		List<ColumnConfig> columnConfigs = new LinkedList<ColumnConfig>();

		ColumnConfig buttonConfig = new ColumnConfig("button", "", 35);
		buttonConfig.setRenderer(new PlusMinusButtonCellRender(this));
		columnConfigs.add(buttonConfig);

		//configs.add(new ColumnConfig("uri", "url", 200));
		ColumnConfig labelConfig = new ColumnConfig("label", "label", 100);
		labelConfig.setResizable(true);
		columnConfigs.add(labelConfig);

		ColumnConfig imageConfig = new ColumnConfig("imageURL", "imageURL", 100);
		imageConfig.setRenderer(new ImageCellRenderer(100,100));
		columnConfigs.add(imageConfig);

		Set<String> properties = new HashSet<String>();
		for(Example example: examples)
		{	
			properties.addAll(example.getProperties().keySet());
		}
		for(String property: properties)
		{
			if(defaultProperties.contains(property)) {continue;}
			ColumnConfig config = new ColumnConfig(property,Transformer.displayProperty(property),100);
			if(property.contains("image")) {config.setRenderer(new ImageCellRenderer(100,100));}

			columnConfigs.add(config);
		}

		ColumnConfig commentConfig = new ColumnConfig("comment", "comment", 100);
		columnConfigs.add(commentConfig);

		return columnConfigs;
	}

	private Grid<Example> createGrid(List<Example> examples)
	{
		System.out.println(examples);
		PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(examples);
		PagingLoader<PagingLoadResult<Example>> loader = new BasePagingLoader<PagingLoadResult<Example>>(proxy);
		toolbar.bind(loader);	
		gridStore = new ListStore<Example>(loader);
		grid = new Grid<Example>(gridStore,new ColumnModel(columnConfigs(examples)));
		grid.setHeight(1000);
//		BufferView view = new BufferView();
//		view.ensureVisible(4, 0, false);
//		view.setRowHeight(100);
//		//view.setForceFit(true);
//		grid.setView(view);
		loader.load();
		
		//		GridView view = grid.getView();
		//		view.setAutoFill(true);
		//		//view.setForceFit(true);
		//		grid.setColumnLines(true);
		//		//grid.setColumnReordering(true);
		grid.setAutoExpandColumn("comment");
		//grid.setAutoHeight(true);

		//		grid.setAutoWidth(true);
		//		grid.setColumnResize(true);
		//		grid.setColumnLines(true);
		//		grid.setColumnReordering(true);
		//		//grid.setStripeRows(true);
		//		//grid.setAutoExpandColumn("uri");
		//		//grid.setAutoExpandColumn("label");
		//		//grid.setAutoExpandColumn("imageURL");
		updateRowStyle();

		return grid;
	}

	private void updateRowStyle()
	{
		if(grid!=null)
		{
			grid.getView().setViewConfig(new GridViewConfig(){
				@Override
				public String getRowStyle(ModelData model, int rowIndex,ListStore<ModelData> ds)
				{
					//Window.alert(model.get("uri").toString());
					System.out.println(positives);
					System.out.println(model.get("uri"));
					if(HIGHLIGHT_POSITIVES&&positives.contains(model.get("uri")))	{return "row-Style-Positive";}
					else if(model instanceof Example&&((Example)model).containsSolrData) {return "row-Style-SolrData";}
					else if(rowIndex % 2 == 0)		{return "row-Style-Odd";}	
					return "row-Style-Even";
				}
			});
		}
	}

	public void setResult(List<Example> examples)
	{
		if(grid!=null) {this.remove(grid);}
		grid = createGrid(examples);
		
		add(grid);
		layout();
	}

	public void learn()
	{
		final WaitDialog waiting  = new WaitDialog("Learning the new examples");
		waiting.show();
		AsyncCallback<List<Example>> callback = new AsyncCallback<List<Example>>()
		{
			@Override
			public void onSuccess(List<Example> examples)
			{
				//if(1==1)throw new RuntimeException(examples.toString());
				setResult(examples);
				waiting.hide();
			}
			
			@Override
			public void onFailure(Throwable caught)
			{
				waiting.hide();
				throw new RuntimeException(caught);				
			}
		};
	
		service.getExamplesByQTL(new ArrayList<String>(positives), new ArrayList<String>(negatives), callback);
	}

}